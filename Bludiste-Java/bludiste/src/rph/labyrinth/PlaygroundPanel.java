package rph.labyrinth;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JPanel;

public class PlaygroundPanel extends JPanel {

	private static final long serialVersionUID = 1632875193985453072L;

	private int playground[][];
	private LinkedList<Path> paths = new LinkedList<Path>();

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		synchronized (this) {
			if (playground==null) {
				return;
			}
			int width = playground.length;
			int height = playground[0].length;
			int cellWidth = (getWidth()-1)/width;
			int cellHeight = (getHeight()-1)/height;
			int cellSize = cellWidth<cellHeight?cellWidth:cellHeight;
			
			for (int x=0; x<width; x++) {
				for (int y=0; y<height; y++) {
					int color = playground[x][y] & 0x00FFFFFF;
					g.setColor(new Color(color));
					g.fillRect(x*cellSize,y*cellSize,cellSize,cellSize);
				}
			}
			
			int pad = cellSize/6;
			int r = cellSize*2/3;
			for (Path path : paths) {
				for (Coordinate coord : path.getCoordinates()) {
					if (coord.x<0 || coord.x>=width || coord.y<0 || coord.y>=height) {
						System.err.println("Coordinate ["+coord.x+";"+coord.y+"] is out of the map");
						continue;
					}
					g.setColor(path.color);
					g.fillOval(pad+coord.x*cellSize,pad+coord.y*cellSize,r,r);
					g.setColor(Color.BLACK);
					g.drawOval(pad+coord.x*cellSize,pad+coord.y*cellSize,r,r);
				}
			}

			g.setColor(Color.BLACK);
			for(int x = 0; x <= width; x++) {
				g.drawLine(x*cellSize, 0, x*cellSize, height*cellSize);
			}
			for(int y = 0; y <= height; y++) {
				g.drawLine(0, y*cellSize, width*cellSize, y*cellSize);
			}
			
		}
	}

	void initPlayground(int[][] pg) {
		playground = new int [pg.length][pg[0].length];
		paths.clear();
		copy(pg);
		repaint();
	}

	void updatePaths(LinkedList<Path> update) {
		paths.clear();
		for (Path path : update) {
			paths.add(new Path(path));
		}
		repaint();
	}
	
	private void copy(int[][] pg) {
		synchronized (this) {
			int width = pg.length;
			int height = pg[0].length;
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < height; y++) {
					playground[x][y] = pg[x][y];
				}
			}
		}
	}
	
}
