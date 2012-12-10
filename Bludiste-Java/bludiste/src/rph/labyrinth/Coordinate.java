package rph.labyrinth;

/**
 * Class used to store coordinate on the map
 *
 */
public class Coordinate {

	/**
	 * X coordinate
	 */
	int x;
	/**
	 * Y coordinate
	 */
	int y;
	
	/**
	 * creates new coordinate with x and y coordinates
	 * @param x
	 * @param y
	 */
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets x and y coordinates 
	 * @param x
	 * @param y
	 */
	public void setCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * returns X coordinate
	 * @return X coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * returns Y coordiante
	 * @return Y coordinate
	 */
	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return "["+x+","+y+"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	
	
	
}
