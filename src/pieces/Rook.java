package pieces;

import java.util.ArrayList;

import game.Square;

/**
 * 
 * @author John Hoffmann
 * 
 *         The Rook can move along the columns or rows as long as no pieces are
 *         in the way
 * 
 */
public class Rook extends Piece {

	private boolean canCastle;

	public Rook(int xPosition, int yPosition, Piece.Color color) {
		super(xPosition, yPosition, color, Type.ROOK);
		canCastle = true;
	}

	public Rook(Piece pieceCopy) {
		super(pieceCopy);
		this.canCastle = ((Rook) pieceCopy).canCastle();
	}

	@Override
	public boolean hasLegalMove(Square destination, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - destination.getX());
		int yDiff = Math.abs(getPosition().getY() - destination.getY());

		if ((xDiff == 0 && yDiff > 0) || (xDiff > 0 && yDiff == 0)) {
			return notBlocked(getPosition(), destination, pieces) && canTake(destination, pieces)
					&& notCheck(destination, pieces);
		}
		return false;
	}

	@Override
	public boolean canSupport(Square supportSquare, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - supportSquare.getX());
		int yDiff = Math.abs(getPosition().getY() - supportSquare.getY());

		if ((xDiff == 0 && yDiff > 0) || (xDiff > 0 && yDiff == 0)) {
			return notBlocked(getPosition(), supportSquare, pieces) && notCheck(supportSquare, pieces);
		}
		return false;
	}

	public boolean canSee(Square sightSquare, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - sightSquare.getX());
		int yDiff = Math.abs(getPosition().getY() - sightSquare.getY());

		if ((xDiff == 0 && yDiff > 0) || (xDiff > 0 && yDiff == 0)) {
			return notBlocked(getPosition(), sightSquare, pieces);
		}
		return false;
	}

	// getters and setters
	public boolean canCastle() {
		return canCastle;
	}

	public void setCanCastle(boolean canCastle) {
		this.canCastle = canCastle;
	}
}
