package pieces;

import java.util.ArrayList;

import game.Square;

/**
 * 
 * @author John Hoffmann
 * 
 *         The Queen can move along the diagonals, rows, or columns, as long as
 *         nothing is in the way
 */
public class Queen extends Piece {

	public Queen(int xPosition, int yPosition, Piece.Color color) {
		super(xPosition, yPosition, color, Type.QUEEN);
	}

	public Queen(Piece pieceCopy) {
		super(pieceCopy);
	}

	@Override
	public boolean hasLegalMove(Square destination, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - destination.getX());
		int yDiff = Math.abs(getPosition().getY() - destination.getY());

		if ((xDiff == 0 && yDiff > 0) || (xDiff > 0 && yDiff == 0) || (xDiff == yDiff)) {
			return notBlocked(getPosition(), destination, pieces) && canTake(destination, pieces)
					&& notCheck(destination, pieces);
		}
		return false;
	}

	@Override
	public boolean canSupport(Square supportSquare, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - supportSquare.getX());
		int yDiff = Math.abs(getPosition().getY() - supportSquare.getY());

		if ((xDiff == 0 && yDiff > 0) || (xDiff > 0 && yDiff == 0) || (xDiff == yDiff)) {
			return notBlocked(getPosition(), supportSquare, pieces) && notCheck(supportSquare, pieces);
		}
		return false;
	}

	@Override
	public boolean canSee(Square sightSquare, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - sightSquare.getX());
		int yDiff = Math.abs(getPosition().getY() - sightSquare.getY());

		if ((xDiff == 0 && yDiff > 0) || (xDiff > 0 && yDiff == 0) || (xDiff == yDiff)) {
			return notBlocked(getPosition(), sightSquare, pieces);
		}
		return false;
	}
}
