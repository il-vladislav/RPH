package rph.labyrinth;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;

import rph.labyrinth.ontology.Configuration;
import rph.labyrinth.ontology.Map;


/**
 * @author Premysl Volf
 *
 */
public class Labyrinth {

	public static final int EMPTY_SQUARE = 0x00FFFFFF;
	public static final int WALL_SQUARE = 0x00000000;
	public static final int START_SQUARE = 0x00FF0000;
	public static final int GOAL_SQUARE = 0x000000FF;
	public static final int RESULT_PATH = 0x0000FF00;

	protected Configuration config = null;
	protected LabyrinthGUI gui;
	protected String confName;
	protected Player player;

	private boolean firstMap;
	protected boolean planningStarted;
	private boolean isStepping;
	protected Iterator<Map> iterator;
	protected Map mapConfig;
	protected int[][] map;
	protected LinkedList<Path> paths = new LinkedList<Path>();

	private ReentrantLock rLock = new ReentrantLock();
	private Condition rCond = rLock.newCondition();

	protected long startTime;
	protected long curTime;
	protected int curLength;
	protected long totalTime;
	protected int completedMaps;
	protected int failedMaps;
	protected int totalLength;
	private int mapNumber;
	protected String reason;
	protected double totalRelativeLength;
	protected double currentRelativeLength;

	private int[][] originalMap;

	protected void loadConfiguration() throws Exception {
		FileInputStream input = new FileInputStream(new File(confName));
		config = (Configuration)AglobeXMLtools.unmarshallJAXBObject(Configuration.class, input);
		input.close();

		loadPlayer();
		if(gui!=null){
			gui.init(config.getWindowLeft(),config.getWindowTop(),config.getWindowWidth(),config.getWindowHeight());
		}

		firstMap = true;
		planningStarted = false;
		iterator = config.getMap().iterator();
		mapConfig = iterator.next();
		loadMap(mapConfig);
		if(gui!=null){
			gui.initPlayground(map);
		}

		mapNumber=0;
		curTime = 0;
		curLength = 0;
		totalTime = 0;
		completedMaps = 0;
		failedMaps = 0;
		totalLength = 0;
		totalRelativeLength = 0;
		currentRelativeLength = 0;

		if(gui!=null){
			gui.playerLabel.setText(player.getName());
			gui.mapLabel.setText("0 / "+config.getMap().size());
			gui.statusLabel.setText("Not started yet");
			gui.curTimeLabel.setText(""+(curTime/1000000000.)+" s");
			gui.curPathLabel.setText("0");
			gui.totalMapsLabel.setText(completedMaps+" / "+failedMaps);
			gui.totalPathLabel.setText(""+totalLength);
			gui.totalTimeLabel.setText(""+(totalTime/1000000000.)+" s");
		}
	}

	private void loadPlayer() throws Exception {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		Class<?> cc;
		cc = cl.loadClass(config.getPlayer());
		if (!Player.class.isAssignableFrom(cc)) {
			throw new Exception("Incompatible Player1 class name: " + config.getPlayer());
		}
		player = (Player) cc.newInstance();
		player.init(this);
	}

	protected void loadMap(Map mapConfig) throws Exception {
		BufferedImage image = null;
		image = ImageIO.read(new File(mapConfig.getName()));
		int width = image.getWidth();
		int height = image.getHeight();
		map = new int[width][height];
		originalMap = new int[width][height];
		boolean startSquare = false;
		boolean goalSquare = false;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				int rgb = image.getRGB(x, y) & 0x00FFFFFF;
				map[x][y] = rgb;
				originalMap[x][y] = rgb;
				if (rgb == START_SQUARE) {
					if (startSquare) {
						throw new Exception("There are two start squares on the map");
					}
					startSquare = true;
				}
				if (rgb == GOAL_SQUARE) {
					if (goalSquare) {
						throw new Exception("There are two goal squares on the map");
					}
					goalSquare = true;
				}
			}
		}
		if (!startSquare) {
			throw new Exception("There is no start square on the map");
		}
		if (!goalSquare) {
			throw new Exception("There is no goal square on the map");
		}
	}

	protected void run(String[] args) {
		if (args.length==0) {
			System.err.println("The program needs a configuration as a parameter");
			return;
		}
		confName = args[0];
		//create gui
		if(!GraphicsEnvironment.isHeadless()) {
			gui = new LabyrinthGUI(this);
		}
		try {
			loadConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	void addPath(Path path) {
		if (path!=null) {
			paths.add(path);
		}
	}

	void removePath(Path path) {
		if (path!=null) {
			paths.remove(path);
		}
	}

	void removeAllPaths() {
		paths.clear();
	}

	void waitForNextStep() {
		if (isStepping) {
			curTime += (System.nanoTime()-startTime);
			if(gui!=null) {
				gui.curTimeLabel.setText(""+(curTime/1000000000.)+" s");
				gui.statusLabel.setText("Paused, waiting for next step");
				gui.planMapButton.setEnabled(true);
				gui.makeStepsButton.setEnabled(true);
				gui.updatePaths(paths);
			}
			rLock.lock();
			try {
				try {
					rCond.await();
				} catch (InterruptedException e) {
				}
			} finally {
				rLock.unlock();
			}

		}
	}

	protected void planAction(boolean stepping) {
		if (!planningStarted) {
			if (!firstMap) {
				mapConfig = iterator.next();
				try {
					loadMap(mapConfig);
				} catch (Exception e) {
					System.err.println("Cannot load map: "+mapConfig.getName());
					return;
				}
				if(gui!=null) gui.initPlayground(map);
			}
			else {
				firstMap = false;
			}
			planningStarted = true;
			isStepping = stepping;
			paths.clear();
			mapNumber++;
			if(gui!=null) gui.mapLabel.setText(mapNumber+" / "+config.getMap().size()+" - "+mapConfig.getName());

			Thread thread = new Thread(new Runnable() {
				public void run() {
					if(gui!=null) {
						gui.planMapButton.setEnabled(false);
						gui.makeStepsButton.setEnabled(false);
						gui.loadConfigurationButton.setEnabled(false);
						gui.statusLabel.setText("Player is planning...");
					}

					curTime = 0;
					startTime = System.nanoTime();
					Path resultPath = player.findPath(map);
					curTime += (System.nanoTime()-startTime);

					totalTime += curTime;
					curLength = 0;
					currentRelativeLength = 0;
					planningStarted = false;

					if(gui!=null) {
						gui.planMapButton.setEnabled(true);
						gui.makeStepsButton.setEnabled(true);
						gui.loadConfigurationButton.setEnabled(true);
					}

					reason = "";
					paths.clear();

					if (resultPath==null) {
						gui.updatePaths(paths);
						failedMaps++;
						reason = "No path returned";
					}
					else {
						resultPath.setColor(RESULT_PATH);
						paths.add(resultPath);
						if(gui!=null) gui.updatePaths(paths);
						if (checkPath(resultPath)) {
							completedMaps++;
							curLength = resultPath.coordinates.size()-1;
						}
						else {
							failedMaps++;
						}
					}
					totalLength += curLength;

					currentRelativeLength = (double)curLength/(double)map.length/map[0].length;
					totalRelativeLength += currentRelativeLength; 
					if(gui!=null) {
						gui.curTimeLabel.setText(""+(curTime/1000000000.)+" s");
						gui.totalTimeLabel.setText(""+(totalTime/1000000000.)+" s");
						gui.totalMapsLabel.setText(completedMaps+" / "+failedMaps);
						gui.statusLabel.setText(reason);
						gui.curPathLabel.setText(""+curLength);
						gui.totalPathLabel.setText(""+totalLength);
					}

					if (mapNumber == config.getMap().size()) {
						if(gui!=null) {
							gui.planMapButton.setEnabled(false);
							gui.makeStepsButton.setEnabled(false);
						}
					}
				}
			});
			thread.start();
		}
		else {
			if(gui!=null) {
				gui.planMapButton.setEnabled(false);
				gui.makeStepsButton.setEnabled(false);
				gui.statusLabel.setText("Player is planning...");
			}
			startTime = System.nanoTime();
			isStepping = stepping;
			rLock.lock();
			try {
				rCond.signal();
			} finally {
				rLock.unlock();
			}
		}
	}

	protected boolean checkPath(Path path) {
		if(path==null){
			reason = "returned null. The path does not exist?";
			return false;
		}
		boolean first = true;
		Coordinate prev = null;
		for (Coordinate coord : path.coordinates) {
			if (coord.x<0 || coord.x>=originalMap.length || coord.y<0 || coord.y>=originalMap[0].length) {
				reason = "path contains coordinate ["+coord.x+";"+coord.y+"] out of the map";
				return false;
			}
			if (first) {
				if (originalMap[coord.x][coord.y] != START_SQUARE) {
					reason = "path does not start at the start square";
					return false;
				}
				prev = coord;
				first = false;
				continue;
			}
			if (originalMap[coord.x][coord.y] == WALL_SQUARE) {
				reason = "path contains wall square";
				return false;
			}
			if (!neighboring(coord,prev)) {
				reason = "path is not continuous";
				return false;
			}
			prev = coord;
		}
		if (originalMap[prev.x][prev.y] != GOAL_SQUARE) {
			reason = "path does not finish at the goal square";
			return false;
		}
		reason = "path valid";
		return true;
	}

	private boolean neighboring(Coordinate first, Coordinate second) {
		int diffX = first.x - second.x;
		int diffY = first.y - second.y;
		if ((diffX==0 && Math.abs(diffY)<=1) || (diffY==0 && Math.abs(diffX)<=1)) {
			return true;
		}
		return false;
	}

	void loadConfigurationButtonAction() {
		if(gui!=null) {
			gui.planMapButton.setEnabled(true);
			gui.makeStepsButton.setEnabled(true);
			gui.loadConfigurationButton.setEnabled(true);
		}
		try {
			loadConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Labyrinth labyrinth;
		labyrinth = new Labyrinth();
		labyrinth.run(args);
	}

}
