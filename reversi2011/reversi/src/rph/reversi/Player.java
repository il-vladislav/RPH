package rph.reversi;

/**
 * Player is a generic reversi player. All players have to be inherited from this class.
 * It contains methods for exchanging data with reversi playground server.
 *
 */
public abstract class Player {

	private Reversi reversi;
	private int index;
	
    final public void init(Reversi reversi, int index) {
    	this.reversi = reversi;
    	this.index = index;
    }
    
    final public int getIndex() {
    	return index;
    }
    
    /**
     * sendMove is used to inform server about move the player is willing to play.
     * @param x - X coordinate of the stone
     * @param y - Y coordinat of the stone
     * @return true, iff the intended move is correct considering rules of the reversi game
     * if false is returned, player has to recomute and resend different move
     */
    final protected boolean sendMove(int x, int y) {
    	return reversi.sendMove(this,x,y);
    }

    /**
     * Returns name of the reversi player
     * @return name of the reversi player
     */
    protected abstract String getName();
    
    /**
     * startGame is called by the reversi server at the beginning of each game and gives basic information to the player.
     * @param myColor - contains information about number assigned to the player
     * @param opponentColor - contains information about number assigned to the opponent player
     * @param playground - contains map of the playground represented by two-dimensional matrix. 
     * Each sqaure contains number of -1 representing empty square and number 0 and 1 representing players' stones.  
     */
    protected abstract void startGame(int myColor, int opponentColor, int[][] playground);

    /**
     * makeNextTurn is called by the reversi server when the player has to compute new move. New move is send to the reversi server using sendMove method.
     * @param playground - contains actual map of the playground represented by two-dimensional matrix. 
     * Each sqaure contains number of -1 representing empty square and number 0 and 1 representing players' stones.  
     */
    protected abstract void makeNextTurn(int[][] playground);
}