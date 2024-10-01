package pieces;

import java.util.ArrayList;

import game.EndGameCheck;
import game.Square;

import game.helpers.Finder;

/**
 * 
 * @author John Hoffmann
 * 
 *         The King has the following move set:
 * 
 *         - can move 1 in any direction
 * 
 *         - can castle, in which case can move 2 in the direction of the rook
 *         it's is castling with
 * 
 *         - when castling, the rook jumps over the king and lands adjacent to
 *         it
 * 
 *         - cannot move into "check" (into a position in which an opponent's
 *         piece can take the king)
 * 
 *         - cannot start in check or pass through "check" while castling
 * 
 *         - cannot castle if either the rook or king has moved before
 * 
 */
public class King extends Piece {

	private boolean canCastle;

	public King(int xPosition, int yPosition, Piece.Color color) {
		super(xPosition, yPosition, color, Type.KING);
		canCastle = true;
	}

	public King(Piece pieceCopy) {
		super(pieceCopy);
		this.canCastle = ((King) pieceCopy).canCastle();
	}

	@Override
	public boolean hasLegalMove(Square destination, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - destination.getX());
		int yDiff = Math.abs(getPosition().getY() - destination.getY());

		if ((xDiff + yDiff < 3) && ((xDiff == 1) || (yDiff == 1))) {
			return canTake(destination, pieces) && notCheck(destination, pieces);
		}

		if (canCastle && xDiff == 2 && yDiff == 0) {
			Piece rook = getCastleRook(destination, pieces);
			Square inBetweenSquare = new Square((getPosition().getX() + destination.getX()) / 2, getPosition().getY());

			return rook != null && !EndGameCheck.inCheck(pieces, getColor()) && notCheck(inBetweenSquare, pieces)
					&& notCheck(destination, pieces);
		}
		return false;
	}

	@Override
	public boolean canSupport(Square supportSquare, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - supportSquare.getX());
		int yDiff = Math.abs(getPosition().getY() - supportSquare.getY());

		if ((xDiff + yDiff < 3) && ((xDiff == 1) || (yDiff == 1))) {
			return notCheck(supportSquare, pieces);
		}
		return false;
	}

	@Override
	public boolean canSee(Square sightSquare, ArrayList<Piece> pieces) {
		int xDiff = Math.abs(getPosition().getX() - sightSquare.getX());
		int yDiff = Math.abs(getPosition().getY() - sightSquare.getY());

		if ((xDiff + yDiff < 3) && ((xDiff == 1) || (yDiff == 1))) {
			return true;
		}
		return false;
	}

	/**
	 * searches for and returns the rook that the king can castle with
	 * 
	 * this method does not check to see if the king was in, passes through, or
	 * lands in check
	 * 
	 * @param destination the king's destination square
	 * @param pieces      the ArrayList<Piece> of pieces
	 * @return the Rook that can castle, null otherwise
	 */
	public Piece getCastleRook(Square destination, ArrayList<Piece> pieces) {
		int row = (getColor() == Color.WHITE) ? 1 : 8;
		Piece rook = Finder.getPieceOnSquare(destination.getX() <= 4 ? 1 : 8, row, pieces);

		if (rook != null && rook.getType() == Type.ROOK && ((Rook) rook).canCastle()
				&& notBlocked(getPosition(), rook.getPosition(), pieces)) {
			return rook;
		}
		return null;
	}

	// getters, setters
	public boolean canCastle() {
		return canCastle;
	}

	public void setCanCastle(boolean canCastle) {
		this.canCastle = canCastle;
	}
}
