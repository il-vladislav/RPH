package rph.labyrinth;

import java.awt.Color;
import java.util.ArrayList;

/**
 * Path is used to store both auxiliary and final path. It contains a list of coordinates that do not need to be continuous
 */
public class Path {

	/**
	 * Color used when no color is defined
	 */
	private static final Color DEFAULT_COLOR = Color.GRAY;
	/**
	 * color of the path
	 */
	Color color;
	/**
	 * list of stored coordinates
	 */
	ArrayList<Coordinate> coordinates;

	/**
	 * Constructs empty path with default color
	 */
	public Path() {
		this(null,DEFAULT_COLOR);
	}
	
	/**
	 * Constructs path filed by coordinates with default color. Coordinates are hard copied
	 * @param coordinates - coordinates inserted into a new path (hard copied)
	 */
	public Path(ArrayList<Coordinate> coordinates) {
		this(coordinates,DEFAULT_COLOR);
	}
	
	/**
	 * Constructs empty path with color
	 * @param color - color of a new path
	 */
	public Path(Color color) {
		this(null,color);
	}
	
	/**
	 * constructs copy of a given path
	 * @param path - path to copy
	 */
	public Path(Path path) {
		this(path.coordinates,path.color);
	}
	
	/**
	 * Constructs path based on coordinates (hard copied) and color
	 * @param coordinates - coordinates inserted into a new path (hard copied)
	 * @param color - color of a new path
	 */
	public Path(ArrayList<Coordinate> coordinates, Color color) {
		this.color = color;
		if (coordinates!=null) {
			this.coordinates = new ArrayList<Coordinate>(coordinates);
		}
		else {
			this.coordinates = new ArrayList<Coordinate>();
		}
	}
	
	/**
	 * returns color of the path
	 * @return color of the path
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * sets color of the path
	 * @param color - color to set using java class Color
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * sets color of the path
	 * @param color - rgb color - in hexadecimal format 0x00RRGGBB
	 */
	public void setColor(int color) {
		this.color = new Color(color);
	}
	
	/** 
	 * returns list of coordinates
	 * @return list of coordinates
	 */
	public ArrayList<Coordinate> getCoordinates() {
		return coordinates;
	}
	
	/**
	 * adds new coordinate at the end of the list
	 * @param coordinate - added coordinate
	 */
	public void addCoordinate(Coordinate coordinate) {
		coordinates.add(coordinate);
	}

	/**
	 * creates new object Coordinate using parameters and adds new coordinate at the end of the list 
	 * @param x - x coordinate
	 * @param y - y coordinate
	 */
	public void addCoordinate(int x, int y) {	
		coordinates.add(new Coordinate(x,y));
	}

	@Override
	public String toString() {
		return "Path [coordinates=" + coordinates + "]";
	}
	
	
	
}
