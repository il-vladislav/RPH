package rph.reversi;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;

import rph.reversi.ontology.Configuration;


/**
 * @author Premysl Volf
 *
 */
public class Reversi {

	protected Configuration config = null;
	private ReversiGUI gui;
	protected Player player1;
	protected Player player2;
	protected int pgWidth;
	protected int pgHeight;

	protected Player currentPlayer;
	protected Player otherPlayer;
	protected int[][] playground;
	protected int gameNumber;
	protected int maxStones;
	protected boolean firstStarts;
	protected long startThinkingTime;

	protected int stones[] = new int[2];
	protected long thinkingTime[] = new long[2];
	protected double p1Wins;
	protected int p1TotalStones;
	protected double p2Wins;
	protected int p2TotalStones;

	private String confName;
	private Timer timer;
	private boolean started = false;
	private boolean paused = false;
	private int playerOnMove = -1;
	private ReentrantLock rLock = new ReentrantLock();
	private Condition rCond = rLock.newCondition();

	private void loadConfiguration() throws Exception {
		FileInputStream input = new FileInputStream(new File(confName));
		config = (Configuration)AglobeXMLtools.unmarshallJAXBObject(Configuration.class, input);
		input.close();

		loadPlayers();
		if(gui!=null){
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					gui.init(config.getWindowLeft(),config.getWindowTop(), config.getWindowWidth(),config.getWindowHeight());
					gui.p1NameLabel.setText("Player 1 - "+player1.getName());
					gui.p2NameLabel.setText("Player 2 - "+player2.getName());
				}
			});
		}

		maxStones = config.getPlaygroundSize()*config.getPlaygroundSize();
	}

	protected void loadPlayers() throws Exception {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		Class<?> cc;

		String player1ClassName = config.getPlayer1();
		String player2ClassName = config.getPlayer2();

		//        System.out.println("* player1ClassName - "+player1ClassName);
		//        System.out.println("* player2ClassName - "+player2ClassName);

		cc = cl.loadClass(player1ClassName);
		if (!Player.class.isAssignableFrom(cc)) {
			throw new Exception("Incompatible Player1 class name: " + player1ClassName);
		}
		player1 = (Player) cc.newInstance();
		player1.init(this,0);

		cc = cl.loadClass(player2ClassName);
		if (!Player.class.isAssignableFrom(cc)) {
			throw new Exception("Incompatible Player2 class name: " + player2ClassName);
		}
		player2 = (Player) cc.newInstance();
		player2.init(this,1);

		//        System.out.println("* Player 1 - "+player1.getName());
		//        System.out.println("* Player 2 - "+player2.getName());
	}


	protected void run(String[] args) {
		if (args.length==0) {
			System.err.println("The program needs a configuration as a parameter");
			return;
		}
		confName = args[0];
		//create gui
		if(!GraphicsEnvironment.isHeadless()) {
			gui = new ReversiGUI(this);
		}
		try {
			loadConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		timer = new Timer();
		initGameSet();
	}

	private void initGameSet() {
		gameNumber = 0;
		firstStarts = true;
		p1Wins=0;
		p1TotalStones=0;
		thinkingTime[0] = 0;
		p2Wins=0;
		p2TotalStones=0;
		thinkingTime[1] = 0;

		if (gui!=null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					gui.gamesLabel.setText(gameNumber+" / "+config.getNumberOfGames());
					gui.p1TotalStonesLabel.setText(""+p1TotalStones);
					gui.p1TotalGamesLabel.setText(""+p1Wins);
					gui.p1TotalTimeLabel.setText((thinkingTime[0]/1000000000.)+" s");

					gui.p2TotalStonesLabel.setText(""+p2TotalStones);
					gui.p2TotalGamesLabel.setText(""+p2Wins);
					gui.p2TotalTimeLabel.setText((thinkingTime[1]/1000000000.)+" s");
				}
			});
		}

		// init playground
		pgWidth = config.getPlaygroundSize();
		pgHeight = config.getPlaygroundSize();
		playground = new int[pgWidth][pgHeight];
		for (int x=0; x<pgWidth; x++) {
			for (int y=0; y<pgWidth; y++) {
				playground[x][y]= -1;
			}
		}
		playground[pgWidth/2-1][pgHeight/2-1]=0;
		playground[pgWidth/2][pgHeight/2]=0;
		playground[pgWidth/2][pgHeight/2-1]=1;
		playground[pgWidth/2-1][pgHeight/2]=1;
		if (gui!=null) {
			gui.initPlayground(playground);
		}
	}

	private void initGame(boolean firstStarts) {
		// init playground
		if (!started) {
			return;
		}
		pgWidth = config.getPlaygroundSize();
		pgHeight = config.getPlaygroundSize();
		playground = new int[pgWidth][pgHeight];
		for (int x=0; x<pgWidth; x++) {
			for (int y=0; y<pgWidth; y++) {
				playground[x][y]= -1;
			}
		}
		playground[pgWidth/2-1][pgHeight/2-1]=0;
		playground[pgWidth/2][pgHeight/2]=0;
		playground[pgWidth/2][pgHeight/2-1]=1;
		playground[pgWidth/2-1][pgHeight/2]=1;

		if(gui!=null){
			gui.initPlayground(playground);
		}

		// initialize
		player1.startGame(0,1,copyPlayground(playground));
		player2.startGame(1,0,copyPlayground(playground));

		if (firstStarts) {
			currentPlayer = player1;
			otherPlayer = player2;
		}
		else {
			currentPlayer = player2;
			otherPlayer = player1;
		}

		stones[0] = 2;
		stones[1] = 2;

		if(gui!=null){
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					gui.gamesLabel.setText(gameNumber+" / "+config.getNumberOfGames());
					gui.p1StonesLabel.setText(""+stones[0]);
					gui.p2StonesLabel.setText(""+stones[1]);
				}
			});
		}

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(gui!=null){
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							gui.statusLabel.setText("Player "+(currentPlayer.getIndex()+1)+" is thinking...");
						}
					});
				}
				playerOnMove = currentPlayer.getIndex();
				startThinkingTime = System.nanoTime();
				currentPlayer.makeNextTurn(copyPlayground(playground));
			}
		},config.getMoveDelay());
	}

	protected void endOfGame(final String reason) {
		gameNumber++;
		String s;
		p1TotalStones += stones[0];
		p2TotalStones += stones[1];
		if (stones[0]>stones[1]) {
			p1Wins++;
			s = ", player 1 has won";
		}
		else if (stones[1]>stones[0]) {
			p2Wins++;
			s = ", player 2 has won";
		}
		else {
			p1Wins += 0.5;
			p2Wins += 0.5;
			s = ", deuce - nobody has won ";
		}
		final String result = s;
		if(gui!=null){
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					gui.statusLabel.setText(reason+"Game finished"+result);
					gui.gamesLabel.setText(gameNumber+" / "+config.getNumberOfGames());

					gui.p1TotalStonesLabel.setText(""+p1TotalStones);
					gui.p1TotalGamesLabel.setText(""+p1Wins);
					gui.p2TotalStonesLabel.setText(""+p2TotalStones);
					gui.p2TotalGamesLabel.setText(""+p2Wins);
				}
			});
		}

		if (gameNumber<config.getNumberOfGames()) {
			if (started) {
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						checkPause();
						firstStarts = !firstStarts;
						initGame(firstStarts);
					}
				},config.getGameDelay());
			}
		}
		else {
			if (gui!=null)
			{
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						synchronized (gui) {
							started = false;
							rLock.lock();
							try {
								paused = false;
								rCond.signal();
							} finally {
								rLock.unlock();
							}
							gui.startStopButton.setText("Start Game");
							gui.startStopButton.setToolTipText("Starts a set of the games");
							gui.playPauseButton.setText("Pause");
							gui.playPauseButton.setToolTipText("Pauses the game");
							gui.playPauseButton.setEnabled(false);
							gui.loadConfigurationButton.setEnabled(true);
						}
					}
				});
			}
		}
	}

	public boolean sendMove(Player player, int x, int y) {
		if (!started) {
			System.out.println("Received move to ["+x+","+y+"] from player "+player.getIndex()+":"+player.getName()+", but the game has not started yet.");
			System.err.println("Received move to ["+x+","+y+"] from player "+player.getIndex()+":"+player.getName()+", but the game has not started yet.");
			return true;
		}
		if (playerOnMove != player.getIndex()) {
			System.out.println("Received move to ["+x+","+y+"] from player "+player.getIndex()+":"+player.getName()+", but the player is not on the move.");
			System.err.println("Received move to ["+x+","+y+"] from player "+player.getIndex()+":"+player.getName()+", but the player is not on the move.");
			return true;
		}
		if (checkAndChangeMove(player.getIndex(),true,x,y)) {
			long endThinkingTime = System.nanoTime();
			playerOnMove = -1;
			if(gui!=null){
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						gui.update(playground);
						gui.statusLabel.setText("Player "+(currentPlayer.getIndex()+1)+" has played");
					}
				});
			}

			thinkingTime[currentPlayer.getIndex()]+= endThinkingTime - startThinkingTime;

			if(gui!=null){
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						gui.p1TotalTimeLabel.setText((thinkingTime[0]/1000000000.)+" s");
						gui.p2TotalTimeLabel.setText((thinkingTime[1]/1000000000.)+" s");
					}
				});
			}

			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (!started) {
						return;
					}
					checkPause();
					repaintOriginal();
					if (stones[0]+stones[1] == maxStones) {
						endOfGame("");
					}
					else {
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								if (!started) {
									return;
								}
								checkPause();
								//check if other player can play somewhere
								if (canPlay(otherPlayer.getIndex())) {
									Player p = currentPlayer;
									currentPlayer = otherPlayer;
									otherPlayer = p;
									if(gui!=null){
										SwingUtilities.invokeLater(new Runnable() {
											@Override
											public void run() {
												gui.statusLabel.setText("Player "+(currentPlayer.getIndex()+1)+" is thinking...");
											}
										});
									}
									playerOnMove = currentPlayer.getIndex();
									startThinkingTime = System.nanoTime();
									currentPlayer.makeNextTurn(copyPlayground(playground));
								}
								else {
									if (canPlay(currentPlayer.getIndex())) {
										if(gui!=null){
											SwingUtilities.invokeLater(new Runnable() {
												@Override
												public void run() {
													gui.statusLabel.setText("Player "+(otherPlayer.getIndex()+1)+" cannot play, Player "+(currentPlayer.getIndex()+1)+" is thinking...");
												}
											});
										}
										playerOnMove = currentPlayer.getIndex();
										startThinkingTime = System.nanoTime();
										currentPlayer.makeNextTurn(copyPlayground(playground));
									}
									else {
										endOfGame("Nobody can move, ");
									}
								}
							}
						},config.getMoveDelay());
					}
				}
			},config.getMoveHighlightDelay());
			return true;
		}
		else {
			return false;
		}
	}

	protected boolean canPlay(int player) {
		for (int i=0;i<pgWidth;i++) {
			for (int j=0;j<pgHeight;j++) {
				if ((playground[i][j] == -1) && (checkAndChangeMove(player,false,i,j)) ) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean checkAndChangeMove(int player, boolean change, int x, int y) {
		if (x<0 || x>=pgWidth || y<0 || y>=pgHeight) {
			return false;
		}
		if (playground[x][y]>=0) {
			return false;
		}
		boolean direction = false;
		direction |= checkAndChangeDirection(player, change,  1,  0, x, y);
		direction |= checkAndChangeDirection(player, change,  1,  1, x, y);
		direction |= checkAndChangeDirection(player, change,  0,  1, x, y);
		direction |= checkAndChangeDirection(player, change, -1,  1, x, y);
		direction |= checkAndChangeDirection(player, change, -1,  0, x, y);
		direction |= checkAndChangeDirection(player, change, -1, -1, x, y);
		direction |= checkAndChangeDirection(player, change,  0, -1, x, y);
		direction |= checkAndChangeDirection(player, change,  1, -1, x, y);
		if (direction && change) {
			playground[x][y] = player + 2;
			stones[player]++;
			if (gui!=null) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						gui.p1StonesLabel.setText(""+stones[0]);
						gui.p2StonesLabel.setText(""+stones[1]);
					}
				});
			}
		}
		return direction;
	}

	private boolean checkAndChangeDirection(int player, boolean change, int dirX, int dirY, int x, int y) {
		boolean neighbourOpponent = false;
		boolean endingMyStone = false;
		int steps = 0;
		while (!endingMyStone) {
			x += dirX;
			y += dirY;
			steps++;
			if (x<0 || x>=pgWidth || y<0 || y>=pgHeight) {
				return false;
			}
			int color = playground[x][y];
			if (color<0) {
				return false;
			}
			if (color!=player) {
				neighbourOpponent = true;
			}
			else {
				endingMyStone = true;
			}
		}
		if (neighbourOpponent && endingMyStone) {
			if (change) {
				dirX *= -1;
				dirY *= -1;
				int otherPlayer = (player+1)%2;
				for (int i=1;i<steps;i++) {
					x += dirX;
					y += dirY;
					playground[x][y] = player + 4;
					stones[player]++;
					stones[otherPlayer]--;
				}
			}
			return true;
		}
		return false;
	}

	private void repaintOriginal() {
		for (int x=0; x<pgWidth; x++) {
			for (int y=0; y<pgHeight; y++) {
				int color = playground[x][y];
				if (color<=1) {
					continue;
				}
				int newColor = color % 2;
				playground[x][y]=newColor;
				if(gui!=null){
					gui.update(playground);
				}
			}
		}
	}

	void startStopButtonAction() {
		if (started) {
			synchronized (gui) {
				started = false;
				rLock.lock();
				try {
					paused = false;
					rCond.signal();
				} finally {
					rLock.unlock();
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						gui.startStopButton.setText("Start Game");
						gui.startStopButton.setToolTipText("Starts a set of the games");
						gui.playPauseButton.setText("Pause");
						gui.playPauseButton.setToolTipText("Pauses the game");
						gui.playPauseButton.setEnabled(false);
						gui.loadConfigurationButton.setEnabled(true);
						gui.statusLabel.setText("Game stopped");
					}
				});
				timer.cancel();
				timer = new Timer();
			}
		}
		else {
			synchronized (gui) {
				started = true;
				paused = false;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						gui.startStopButton.setText("Stop Game");
						gui.startStopButton.setToolTipText("Stops a set of the games");
						gui.playPauseButton.setEnabled(true);
						gui.loadConfigurationButton.setEnabled(false);
					}
				});
			}
			initGameSet();
			initGame(firstStarts);
		}
	}

	void playPauseButtonAction() {
		if (paused) {
			synchronized (gui) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						gui.playPauseButton.setText("Pause");
						gui.playPauseButton.setToolTipText("Pauses the game");
					}
				});
				rLock.lock();
				try {
					paused = false;
					rCond.signal();
				} finally {
					rLock.unlock();
				}
			}
		}
		else {
			synchronized (gui) {
				rLock.lock();
				try {
					paused = true;
				} finally {
					rLock.unlock();
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						gui.playPauseButton.setText("Resume");
						gui.playPauseButton.setToolTipText("Resumes the game");
					}
				});
			}
		}

	}

	void checkPause() {
		rLock.lock();
		try {
			if (paused) {
				try {
					rCond.await();
				} catch (InterruptedException e) {
				}
			}
		} finally {
			rLock.unlock();
		}
	}

	void loadConfigurationButtonAction() {
		try {
			loadConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		timer = new Timer();
		initGameSet();
	}



	/**
	 * @param args
	 */
	 public static void main(String[] args) {
		 Reversi reversi;
		 reversi = new Reversi();
		 reversi.run(args);
	 }

	 protected int[][] copyPlayground(int[][] playground){
		 int[][] copy = new int[playground.length][playground[0].length];

		 for(int i =0;i<playground.length;i++){
			 for(int j=0;j<playground[0].length;j++){
				 copy[i][j] = playground[i][j];
			 }
		 }
		 return copy;

	 }


}
