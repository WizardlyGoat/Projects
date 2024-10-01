package pieces;

import java.util.ArrayList;
import game.EndGameCheck;
import game.Move;
import game.Square;
import game.helpers.ConsoleColors;
import game.helpers.Copier;
import game.helpers.Finder;
import game.helpers.MoveExecuter;

/**
 * @author John Hoffmann
 * 
 *         Piece establishes key parts that every type of piece has
 */
public abstract class Piece implements Comparable<Piece>, ConsoleColors {

	// enum for colors of pieces
	public static enum Color {
		WHITE("White"), BLACK("Black");

		public String name;

		Color(String name) {
			this.name = name;
		}

		public static Color getOppColor(Color color) {
			return (color == WHITE) ? BLACK : WHITE;
		}

	}

	// enum for types of pieces
	public static enum Type {
		KING("King", 0.0), QUEEN("Queen", 9.0), ROOK("Rook", 5.0), BISHOP("Bishop", 3.0), KNIGHT("Knight", 3.0),
		PAWN("Pawn", 1.0);

		public String name;
		public double worth; // AI uses this

		Type(String name, double worth) {
			this.name = name;
			this.worth = worth;
		}
	}

	private Type type;
	private Color color;
	private Square position;

	// AI uses this
	public double positionWorth;

	/**
	 * @param x     the column
	 * @param y     the row
	 * @param color the color of the piece
	 * @param type  the type of piece
	 */
	public Piece(int x, int y, Color color, Type type) {
		position = new Square(x, y);
		this.color = color;
		this.type = type;
		positionWorth = 0.0;
	}

	/**
	 * @param pieceCopy the piece that is being copied
	 */
	public Piece(Piece pieceCopy) {
		type = pieceCopy.getType();
		color = pieceCopy.getColor();
		position = new Square(pieceCopy.getPosition());
		positionWorth = pieceCopy.positionWorth;
	}

	/**
	 * makes sure that a desired move doesn't put the player's king in check
	 * 
	 * @param destination the square this piece is wanting to go to
	 * @param pieces      the ArrayList<Piece> of pieces
	 * @return true if the move does not put the king in check, false otherwise
	 */
	protected boolean notCheck(Square destination, ArrayList<Piece> pieces) {
		ArrayList<Piece> futurePieces = Copier.copyBoard(pieces);
		Move move = new Move(this, destination, futurePieces);

		// a move is not considered check if you just captured the king
		if (move.getCapturePiece() != null && move.getCapturePiece().type == Type.KING) {
			return false;
		}

		MoveExecuter.testMove(move, futurePieces);

		return !EndGameCheck.inCheck(futurePieces, color);
	}

	/**
	 * determines if any squares between two given squares are occupied
	 * 
	 * @param start       the first square
	 * @param destination the second square
	 * @param pieces      the ArrayList<Piece> of pieces
	 * @return true if there are no pieces in the way
	 */
	protected boolean notBlocked(Square start, Square destination, ArrayList<Piece> pieces) {
		int xDirection = (destination.getX() > start.getX()) ? 1 : -1;
		if (destination.getX() == start.getX()) {
			xDirection = 0;
		}

		int yDirection = (destination.getY() > start.getY()) ? 1 : -1;
		if (destination.getY() == start.getY()) {
			yDirection = 0;
		}
		// start the search with the next square along search path
		int x = start.getX() + xDirection; // current x position
		int y = start.getY() + yDirection; // current y position

		while (!destination.equals(x, y)) {
			if (Finder.isPieceOnSquare(x, y, pieces)) {
				return false;
			}
			x += xDirection;
			y += yDirection;
		}
		return true;
	}

	/**
	 * makes sure there isn't a friendly piece on destination
	 * 
	 * @param destination the square the piece is trying to get to
	 * @param pieces      the ArrayList<Piece> of pieces
	 * @return true if there isn't a friendly piece on destination, false otherwise
	 */
	protected boolean canTake(Square destination, ArrayList<Piece> pieces) {
		Piece capturePiece = Finder.getPieceOnSquare(destination, pieces);
		if (capturePiece != null && capturePiece.getColor() == color) {
			return false;
		}
		return true;
	}

	/**
	 * isMajorMinorPiece checks to see if given piece is a Queen, Rook, Bishop, or
	 * Knight
	 * 
	 * @param piece the piece being examined
	 * @return true, if the piece is a major or minor piece
	 */
	public boolean isMajorMinorPiece() {
		if (type != Piece.Type.PAWN && type != Piece.Type.KING) {
			return true;
		}
		return false;
	}

	/**
	 * isMinorPiece determines if given piece is a Bishop or Knight
	 * 
	 * @param piece the piece being examined
	 * @return true, if the piece is a minor piece
	 */
	public boolean isMinorPiece() {
		if (type == Piece.Type.KNIGHT || type == Piece.Type.BISHOP) {
			return true;
		}
		return false;
	}

	/**
	 * ABSTRACT
	 * METHODS-------------------------------------------------------------------
	 */

	/**
	 * determines if a piece can legally move to destination
	 * 
	 * @param destination the square the piece is trying to move to
	 * @param pieces      the ArrayList<Piece> of pieces
	 * @return true if the piece can move to destination, false otherwise
	 */
	public abstract boolean hasLegalMove(Square destination, ArrayList<Piece> pieces);

	/**
	 * determines if the piece can reach the supportSquare
	 * 
	 * @param supportSquare the square the piece is trying to support
	 * @param pieces        the ArrayList<Piece> of pieces
	 * @return true if the piece can reach supportSquare, false otherwise
	 */
	public abstract boolean canSupport(Square supportSquare, ArrayList<Piece> pieces);

	/**
	 * determines if the piece can see the sightSquare (even if it can't move there
	 * due to a pin)
	 * 
	 * @param sightSquare the square the piece is trying to see
	 * @param pieces      the ArrayList<Piece> of pieces
	 * @return true if the piece can reach the sightSquare, false otherwise
	 */
	public abstract boolean canSee(Square sightSquare, ArrayList<Piece> pieces);

	// getters and setters
	public Square getPosition() {
		return position;
	}

	public void setPosition(Square newPosition) {
		position.setPosition(newPosition);
	}

	public void setPosition(int x, int y) {
		position.setPosition(x, y);
	}

	public Color getColor() {
		return color;
	}

	public Type getType() {
		return type;
	}

	@Override
	/**
	 * checks type, color, and position
	 */
	public boolean equals(Object other) {
		if (!(other instanceof Piece)) {
			return false;
		}
		Piece o = (Piece) other;
		if (type == o.type && color == o.color && position.equals(o.position)) {
			return true;
		}
		return false;
	}

	@Override
	/**
	 * compares positions only
	 */
	public int compareTo(Piece piece) {
		return position.compareTo(piece.getPosition());
	}

	@Override
	public String toString() {
		if (color == Color.BLACK) {
			return "[" + MAGENTA + type + RESET + " on " + position + "]";
		}
		return "[" + CYAN + type + RESET + " on " + position + "]";
	}
}