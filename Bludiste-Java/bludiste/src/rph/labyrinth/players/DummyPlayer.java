package rph.labyrinth.players;

import java.awt.Color;

import rph.labyrinth.Path;
import rph.labyrinth.Player;

public class DummyPlayer extends Player {

	@Override
	protected String getName() {
		return "Dummy Player";
	}

	@Override
	protected Path findPath(int[][] map) {
		// create new empty path with yellow color and fill it with coordinates
		// coordinates does not need to be continuous
		Path circle = new Path(Color.YELLOW);
		circle.addCoordinate(0,6);
		circle.addCoordinate(0,5);
		circle.addCoordinate(0,4);
		circle.addCoordinate(1,3);
		circle.addCoordinate(1,2);
		circle.addCoordinate(2,1);
		circle.addCoordinate(3,1);
		circle.addCoordinate(4,0);
		circle.addCoordinate(5,0);
		circle.addCoordinate(6,0);
		circle.addCoordinate(7,0);
		circle.addCoordinate(8,1);
		circle.addCoordinate(9,1);
		circle.addCoordinate(10,2);
		circle.addCoordinate(10,3);
		circle.addCoordinate(11,4);
		circle.addCoordinate(11,5);
		circle.addCoordinate(11,6);
		circle.addCoordinate(11,7);
		circle.addCoordinate(10,8);
		circle.addCoordinate(10,9);
		circle.addCoordinate(9,10);
		circle.addCoordinate(8,10);
		circle.addCoordinate(7,11);
		circle.addCoordinate(6,11);
		circle.addCoordinate(5,11);
		circle.addCoordinate(4,11);
		circle.addCoordinate(3,10);
		circle.addCoordinate(2,10);
		circle.addCoordinate(1,9);
		circle.addCoordinate(1,8);
		circle.addCoordinate(0,7);
		// add path to the visiualization as a debug path
		addPath(circle);
		// show the path and wait button action to continue
		waitForNextStep();
		
		// create new empty path with magenta color and fill it with coordinates
		// coordinates does not need to be continuous
		Path smile = new Path(Color.MAGENTA);
		smile.addCoordinate(3,3);
		smile.addCoordinate(4,3);
		smile.addCoordinate(3,4);
		smile.addCoordinate(4,4);
		smile.addCoordinate(7,3);
		smile.addCoordinate(8,3);
		smile.addCoordinate(7,4);
		smile.addCoordinate(8,4);
		smile.addCoordinate(3,7);
		smile.addCoordinate(4,8);
		smile.addCoordinate(5,8);
		smile.addCoordinate(6,8);
		smile.addCoordinate(7,8);
		smile.addCoordinate(8,7);
		// add path to the visiualization as a debug path
		addPath(smile);
		// show the path and wait button action to continue
		waitForNextStep();

		// remove all debug paths from visualization
		removeAllPaths();
		// show the path and wait button action to continue
		waitForNextStep();

		// add several paths to the visiualization as a debug paths
		addPath(circle);
		addPath(smile);
		// show the path and wait button action to continue
		waitForNextStep();	
		
		// return path as a result
		return smile;
	}	
}
