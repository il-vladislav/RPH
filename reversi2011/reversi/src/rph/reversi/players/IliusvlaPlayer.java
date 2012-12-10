package rph.reversi.players;

import rph.reversi.Player;


/**
 * Dummy player is an example of implementatation of the player. It plays random and after each move it checks 
 * if the move is valid ("real" player should always check validity of the turn by itself).
 */
public class MyPlayer1 extends Player {

	/**
	 * Constant defining maximum number of tried moves. If it is exceeded, player systematically tries all moves.
	 */
	private final int RANDOM_MOVES_NUMBER = 1000;
	
	/**
	 * Width of the playground
	 */
	private int width;
	/**
	 * Height of the playground
	 */
	private int height;
	
	@Override
	protected String getName() {
		return "Dummy Player";
	}

	@Override
	protected void startGame(int myColor, int opponentColor, int[][] playground) {
		// set dimensions of the playground
		width = playground.length;
		height = playground[0].length;
	}

	@Override
	protected void makeNextTurn(int[][] playground) {
		// try predefined number of random moves
		for (int i=0; i<RANDOM_MOVES_NUMBER; i++) {
			// generate random position of stone
			int randomX = (int)(Math.random()*width);
			int randomY = (int)(Math.random()*height);
			// try to apply this move
			if (sendMove(randomX,randomY)) {
				// finish if successful
				return;
			}
			// continue if not successful
			// real player HAS TO CHECK VALIDITY BY ITSELF
		}
		// if random moves fail, try avery position on the playground
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				if (sendMove(x,y)) {
					return;
				}
			}
		}
	}

}
