package game;

import pieces.Piece;
import pieces.Piece.Color;

/**
 * @author John Hoffmann
 * 
 *         This class is used to represent squares on the board, a single x and
 *         y coordinate
 */
public class Square implements Comparable<Square> {
	private int x; // the x coordinate position (1 - 8) shown as (a - h)
	private int y; // the y coordinate position (1 - 8)

	public Square() {
		x = 0;
		y = 0;
	}

	/**
	 * @param x the column (a - h)
	 * @param y the row (1 - 8)
	 */
	public Square(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @param square the square being copied
	 */
	public Square(Square square) {
		x = square.getX();
		y = square.getY();
	}

	/**
	 * isFirstRank determines if the square is on the first rank, relative to the
	 * color. White's first rank is 1, Black's first rank is 8
	 * 
	 * @param color the color who's perspective is taken
	 * @return true, if the square is on the first rank
	 */
	public boolean isFirstRank(Piece.Color color) {
		if (y == (color == Color.WHITE ? 1 : 8)) {
			return true;
		}
		return false;
	}

	/**
	 * isBackRank does the opposite of isFirstRank. White's back rank is 8, Black's
	 * back rank is 1
	 * 
	 * @param color the color who's perspective is taken
	 * @return true, if the square is on the back rank
	 */
	public boolean isBackRank(Piece.Color color) {
		return !isFirstRank(color);
	}

	/**
	 * isEdgeRank determines if the square is on either the first rank or back rank
	 * 
	 * @return true, if y is 1 or 8
	 */
	public boolean isEdgeRank() {
		return y == 1 || y == 8;
	}

	/**
	 * getValue returns a unique value for the square (used in compareTo and equals
	 * methods)
	 * 
	 * @return (x * 10) + y
	 */
	public int getValue() {
		return (x * 10) + y;
	}

	// getters and setters
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setPosition(Square square) {
		x = square.getX();
		y = square.getY();
	}

	public void setPosition(gui.nodes.Square square) {
		x = square.getC();
		y = square.getR();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Square)) {
			return false;
		}
		Square o = (Square) other;
		if (getValue() == o.getValue()) {
			return true;
		}
		return false;
	}

	/**
	 * overloaded equals method for when coordinates are given instead of a square
	 * 
	 * @param x the column
	 * @param y the row
	 * @return true if same square, false otherwise
	 */
	public boolean equals(int x, int y) {
		Square square = new Square(x, y);
		return equals(square);
	}

	@Override
	public int compareTo(Square square) {
		return getValue() - square.getValue();
	}

	@Override
	public String toString() {
		return ((char) (x + 96)) + "" + y;
	}
}
