package rph.reversi;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PlaygroundPanel extends JPanel {

	private static final long serialVersionUID = 8315771387404619901L;
	private static final Color[] COLOR_POOL = new Color[]{
        new Color(255,  96,  96),
        new Color( 96,  96, 255),
        new Color(255,  32,  32),
        new Color( 32,  32, 255),
    	new Color(255,  32, 128),
    	new Color(128,  32, 255)};

	private int playground[][];

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
			int leftPad = 0;
			int topPad = 0;
			
			g.setColor(Color.BLACK);
			for(int x = 0; x <= width; x++) {
				g.drawLine(leftPad+x*cellSize, topPad, leftPad+x*cellSize, topPad+height*cellSize);
			}
			for(int y = 0; y <= height; y++) {
				g.drawLine(leftPad, topPad+y*cellSize, leftPad+width*cellSize, topPad+y*cellSize);
			}
			
			int leftCenterPad = leftPad+cellSize/6;
			int topCenterPad = topPad+cellSize/6;
			int r = cellSize*2/3;
			for (int x=0; x<width; x++) {
				for (int y=0; y<height; y++) {
					int color = playground[x][y];
					if (color>=0) {
						g.setColor(COLOR_POOL[color]);
						g.fillOval(leftCenterPad+x*cellSize,topCenterPad+y*cellSize,r,r);
						g.setColor(Color.BLACK);
						g.drawOval(leftCenterPad+x*cellSize,topCenterPad+y*cellSize,r,r);
					}
				}
			}
		}
	}

	void initPlayground(int[][] pg) {
		playground = new int [pg.length][pg[0].length];
		updatePlayground(pg);
	}
	
	void updatePlayground(int[][] pg) {
		copy(pg);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				repaint();
			}
		});
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
