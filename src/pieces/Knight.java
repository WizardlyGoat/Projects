package pieces;

import java.util.ArrayList;

import game.Square;

/**
 * 
 * @author John Hoffmann
 * 
 *         The Knight can move in an "L" shape (2 in one direction, 1 in the
 *         other)
 * 
 *         The Knight can jump other pieces, therefore doesn't need to call
 *         notBlocked
 */
public class Knight extends Piece {

	public Knight(int xPosition, int yPosition, Piece.Color color) {
		super(xPosition, yPosition, color, Type.KNIGHT);
	}

	public Knight(Piece pieceCopy) {
		super(pieceCopy);
	}

	@Override
	public boolean hasLegalMove(Square destination, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - destination.getX());
		int yDiff = Math.abs(getPosition().getY() - destination.getY());

		if ((xDiff == 1 && yDiff == 2) || (xDiff == 2 && yDiff == 1)) {
			return canTake(destination, pieces) && notCheck(destination, pieces);
		}
		return false;
	}

	@Override
	public boolean canSupport(Square supportSquare, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - supportSquare.getX());
		int yDiff = Math.abs(getPosition().getY() - supportSquare.getY());

		if ((xDiff == 1 && yDiff == 2) || (xDiff == 2 && yDiff == 1)) {
			return notCheck(supportSquare, pieces);
		}
		return false;
	}

	@Override
	public boolean canSee(Square sightSquare, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - sightSquare.getX());
		int yDiff = Math.abs(getPosition().getY() - sightSquare.getY());

		if ((xDiff == 1 && yDiff == 2) || (xDiff == 2 && yDiff == 1)) {
			return true;
		}
		return false;
	}
}
