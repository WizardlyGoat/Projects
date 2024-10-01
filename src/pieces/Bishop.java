package pieces;

import java.util.ArrayList;

import game.Square;

/**
 * 
 * @author John Hoffmann
 * 
 *         The Bishop piece moves along the diagonals as long as there are no
 *         pieces in the way
 * 
 */
public class Bishop extends Piece {

	public Bishop(int xPosition, int yPosition, Piece.Color color) {
		super(xPosition, yPosition, color, Type.BISHOP);
	}

	public Bishop(Piece pieceCopy) {
		super(pieceCopy);
	}

	@Override
	public boolean hasLegalMove(Square destinationSquare, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - destinationSquare.getX());
		int yDiff = Math.abs(getPosition().getY() - destinationSquare.getY());

		if (xDiff == yDiff) {
			return notBlocked(getPosition(), destinationSquare, pieces) && canTake(destinationSquare, pieces)
					&& notCheck(destinationSquare, pieces);
		}
		return false;
	}

	@Override
	public boolean canSupport(Square supportSquare, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - supportSquare.getX());
		int yDiff = Math.abs(getPosition().getY() - supportSquare.getY());

		if (xDiff == yDiff) {
			return notBlocked(getPosition(), supportSquare, pieces) && notCheck(supportSquare, pieces);
		}
		return false;
	}

	public boolean canSee(Square sightSquare, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - sightSquare.getX());
		int yDiff = Math.abs(getPosition().getY() - sightSquare.getY());

		if (xDiff == yDiff) {
			return notBlocked(getPosition(), sightSquare, pieces);
		}
		return false;
	}
}
