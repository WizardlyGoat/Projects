package game.helpers;

import java.util.ArrayList;
import game.Square;
import pieces.Piece;

/**
 * 
 * @author John Hoffmann
 * 
 *         Finder contains general board inquiries, such as getting a piece on a
 *         particular square
 *
 */
public interface Finder {

	/**
	 * searches pieces and returns pieces on given square
	 * 
	 * @param square the square being examined
	 * @param pieces the ArrayList<Piece> of all pieces
	 * @return the piece with the same position as square. null if no piece is found
	 */
	public static Piece getPieceOnSquare(Square square, ArrayList<Piece> pieces) {
		for (Piece p : pieces) {
			if (p.getPosition().equals(square)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * searches pieces and returns pieces on given square
	 * 
	 * @param x      the column
	 * @param y      the row
	 * @param pieces the ArrayList<Piece> of all pieces
	 * @return the piece with the same position as square. null if no piece is found
	 */
	public static Piece getPieceOnSquare(int x, int y, ArrayList<Piece> pieces) {
		Square square = new Square(x, y);
		return getPieceOnSquare(square, pieces);
	}

	/**
	 * searches pieces and returns pieces on given square
	 * 
	 * @param square the position in question
	 * @param pieces the ArrayList<Piece> of all pieces
	 * @return true if there is a piece with the same position as square, false
	 *         otherwise
	 */
	public static boolean isPieceOnSquare(Square square, ArrayList<Piece> pieces) {
		if (getPieceOnSquare(square, pieces) != null) {
			return true;
		}
		return false;
	}

	/**
	 * searches pieces and returns pieces on given square
	 * 
	 * @param x      the column
	 * @param y      the row
	 * @param pieces the ArrayList<Piece> of all pieces
	 * @return true if there is a piece with the same position as square, false
	 *         otherwise
	 */
	public static boolean isPieceOnSquare(int x, int y, ArrayList<Piece> pieces) {
		if (getPieceOnSquare(x, y, pieces) != null) {
			return true;
		}
		return false;
	}
}
