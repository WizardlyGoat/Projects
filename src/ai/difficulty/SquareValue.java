package ai.difficulty;

import java.util.ArrayList;

import game.Square;
import game.helpers.Finder;
import pieces.Piece;

public interface SquareValue {

	/**
	 * returns an ArrayList<Piece> of squares that the piece can reach
	 * 
	 * @param pieces the ArrayList<Piece> of pieces
	 * @return the ArrayList<Piece> of all squares the piece can reach
	 */
	public static ArrayList<Square> getControlledSquares(Piece piece, ArrayList<Piece> pieces) {
		ArrayList<Square> controlledSquares = new ArrayList<Square>();
		Square currSquare = new Square();

		for (int c = 1; c < 9; c++) {
			for (int r = 1; r < 9; r++) {
				currSquare.setPosition(c, r);

				if (piece.canSee(currSquare, pieces)) {
					controlledSquares.add(new Square(c, r));
				}
			}
		}
		return controlledSquares;
	}

	/**
	 * returns a value 0 through 6 (larger number = closer to center)
	 * 
	 * @param square the square in question
	 * @return an int value 0 through 6
	 */
	public static int getCenterValue(Square square) {
		double columnCenter = (Math.abs(square.getX() - 4.5));
		double rowCenter = (Math.abs(square.getY() - 4.5));
		return (int) (Math.abs(columnCenter + rowCenter - 7));
	}

	/**
	 * returns a value 0 through 7 (larger number = closer to back rank)
	 * 
	 * @param square the square in question
	 * @return an int value 0 through 7
	 */
	public static int getOffensiveValue(Square square, Piece.Color friend) {
		return friend == Piece.Color.WHITE ? square.getY() - 1 : Math.abs(square.getY() - 8);
	}

	/**
	 * returns a value 0 through 2 (2 is the opponent's king's square, 1 is a
	 * possible escape square for that king, 0 otherwise)
	 * 
	 * @param square the square in question
	 * @param friend the color of the AI
	 * @param pieces the ArrayList of pieces
	 * @return an int value 0 through 2
	 */
	public static int getCheckValue(Square square, Piece.Color friend, ArrayList<Piece> pieces) {
		Piece oppKing = null;
		// get opponent's king
		for (Piece p : pieces) {
			if (p.getType() == Piece.Type.KING && p.getColor() != friend) {
				oppKing = p;
				break;
			}
		}

		// square is king's square
		if (oppKing.getPosition().equals(square)) {
			return 2;
		}

		// square is one of the king's escape squares
		int kingX = oppKing.getPosition().getX();
		int kingY = oppKing.getPosition().getY();
		int x = square.getX();
		int y = square.getY();
		if (Math.abs(x - kingX) <= 1 && Math.abs(y - kingY) <= 1 && Finder.getPieceOnSquare(square, pieces) == null) {
			return 1;
		}

		return 0;
	}

}
