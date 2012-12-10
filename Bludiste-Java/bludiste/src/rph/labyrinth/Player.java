package rph.labyrinth;

/**
 * Player is a generic labyrinth player. All players have to be inherited from this class.
 * It contains methods for exchanging data with labyrinth server.
 *
 */
public abstract class Player {

	private Labyrinth labyrinth;
	
    final public void init(Labyrinth labyrinth) {
    	this.labyrinth = labyrinth;
    }
       
    /**
     * addPath allows player to visualize some auxiliary path (it does not need to be continuous) on the map  
     * @param path
     */
    final protected void addPath(Path path) {
		labyrinth.addPath(path);
	}
	
    /**
     * addPath allows player to remove previously added path  
     * @param path
     */
	final protected void removePath(Path path) {
		labyrinth.removePath(path);
	}
	
	/**
	 * removes all path inserted by player
	 */
	final protected void removeAllPaths() {
		labyrinth.removeAllPaths();
	}
	
	/**
	 * Pauses the planning method until button is pressed. This allows player to see visualization of added auxiliary paths
	 */
	final protected void waitForNextStep() {
		labyrinth.waitForNextStep();
	}
    
    /**
     * Returns name of the labyrinth player
     * @return name of the labyrinth player
     */
    protected abstract String getName();
    
    /**
     * Method is called by labyrinth server and player should return shortest path from start to goal square
     * @param map - map with defined start and goal and wall squares 
     * @return player returns final continuous path connecting start and goal square without intersecting wall squares. 
     */
    protected abstract Path findPath(int[][] map);
}