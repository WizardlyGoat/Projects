package pieces;

import java.util.ArrayList;

import ai.ComSettings;
import ai.ComputerMove;
import game.Square;
import game.StandardGame;

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
 *         - cannot be in check or pass through "check" while castling
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
		this.canCastle = ((King) pieceCopy).isCanCastle();
	}

	/*************************************************************************************
	 * hasLegalMove determines if a piece can go to a destination
	 * 
	 * @param destinationSquare the square the piece is trying to go
	 * @param pieces            the array of all the pieces
	 * @return true if the piece can move to destinationSquare, false otherwise
	 */
	@Override
	public boolean hasLegalMove(Square destinationSquare, ArrayList<Piece> pieces) {
		boolean hasLegalMove;
		// difference in x movement
		int xDiff = Math.abs(getPosition().getX() - destinationSquare.getX());
		// difference in y movement
		int yDiff = Math.abs(getPosition().getY() - destinationSquare.getY());

		// moving 1 in any direction
		// ( xDiff + yDiff < 3 ) so that the king doesn't move too far
		if ((xDiff + yDiff < 3) && ((xDiff == 1) || (yDiff == 1))) {
			// make sure king can take the square
			// make sure the king wouldn't be in danger
			hasLegalMove = (canTake(destinationSquare, pieces)) && (notCheck(destinationSquare, pieces));
		}
		// castling
		else if (xDiff == 2 && yDiff == 0 && canCastle) {
			Piece rook = getCastleRook(destinationSquare, pieces); // nearby rook that may castle with the king
			boolean notBlocked; // false if there is no rook available to castle with or something is in the way
			if (rook != null) {
				notBlocked = notBlocked(getPosition(), rook.getPosition(), pieces);
			} else {
				notBlocked = false;
			}
			// square that the king passes through while castling
			// x (row) is ( ( old square + new square ) / 2 ), and y (column) is the same
			Square inBetweenSquare = new Square((getPosition().getX() + destinationSquare.getX()) / 2,
					getPosition().getY());
			// make sure a the path to the rook is not blocked
			// make sure the king is not in check
			// make sure the king wouldn't pass through check
			// make sure the king wouldn't end in check
			hasLegalMove = notBlocked && !StandardGame.inCheck(pieces, getColor())
					&& (notCheck(inBetweenSquare, pieces)) && (notCheck(destinationSquare, pieces));
		} else {
			hasLegalMove = false;
		}
		return hasLegalMove;
	}

	/****************************************************************************************
	 * canSupport determines if the piece can reach the supportSquare
	 * 
	 * @param supportSquare the square the piece is trying to support
	 * @param pieces        the array of all the pieces
	 * @return true if the piece can reach supportSquare, false otherwise
	 */
	@Override
	public boolean canSupport(Square supportSquare, ArrayList<Piece> pieces) {
		boolean canSupport;
		// difference in x movement
		int xDiff = Math.abs(getPosition().getX() - supportSquare.getX());
		// difference in y movement
		int yDiff = Math.abs(getPosition().getY() - supportSquare.getY());

		// moving 1 in any direction
		// ( xDiff + yDiff < 3 ) so that the king doesn't move too far
		if ((xDiff + yDiff < 3) && ((xDiff == 1) || (yDiff == 1))) {
			// make sure the king wouldn't be in danger
			canSupport = notCheck(supportSquare, pieces);
		} else {
			canSupport = false;
		}
		return canSupport;
	} // end canSupport

	/******************************************************************************************
	 * canSee determines if the piece can see the sightSquare
	 * 
	 * for piece controlled squares, only squares a piece could take on
	 * 
	 * @param sightSquare the square the piece is trying to see
	 * @param pieces      the array of all the pieces
	 * @return true if the piece can reach the sightSquare, false otherwise
	 */
	public boolean canSee(Square sightSquare, ArrayList<Piece> pieces) {
		boolean canSee;
		// difference in x movement
		int xDiff = Math.abs(getPosition().getX() - sightSquare.getX());
		// difference in y movement
		int yDiff = Math.abs(getPosition().getY() - sightSquare.getY());

		// moving 1 in any direction
		// ( xDiff + yDiff < 3 ) so that the king doesn't move too far
		if ((xDiff + yDiff < 3) && ((xDiff == 1) || (yDiff == 1))) {
			canSee = true;
		} else {
			canSee = false;
		}
		return canSee;
	} // end canSee

	/**************************************************************************************
	 * getSquareComfort determines how much the piece wants to be on the square it
	 * is on
	 * 
	 * @param destinationSquare the square that the piece is considering moving to
	 * @param pieces            the array of all pieces
	 * @return score: the amount of points going to this square awards (or loses)
	 */
	public double getSquareComfort(Square currSquare, ArrayList<Piece> pieces, ComSettings difficulty) {
		return 0;

	} // end getSquareComfort

	/************************************************************************************
	 * getPieceHesitation determines how much the piece wants to move
	 * 
	 * @param pieces   the arrayList of all pieces
	 * @param currMove the current ComputerMove (with the destination square)
	 * @return hesitation: a positive (wants to move) or negative (doesn't want to
	 *         move) score
	 */
	public double getPieceHesitation(StandardGame game, ComputerMove currMove, ComSettings difficulty) {
		// get current position
		ArrayList<Piece> pieces = game.getPastPositions().get(game.getPastPositions().size() - 1);
		double hesitation = 0.0; // assume the piece is indifferent
		// difference in x positions tells whether the king is castling or not
		int xDiff = Math.abs(getPosition().getX() - currMove.getDestinationSquare().getX());
		// hesitation or eagerness to castle
		if (xDiff == 2) {
			hesitation += difficulty.getDesireToCastle();
		}
		// not castling, but still moving the king when castling is still an option
		else if (canCastleStill(pieces)) {
			hesitation -= difficulty.getDesireToCastle();
		}
		// adjust according to piece hesitation
		hesitation += difficulty.getKingHesitation();
		// save to reasoning if there is a hesitation
		if (hesitation > 0.01 || hesitation < -0.01) {
			currMove.getAddReason().println(this + ": " + Math.round(hesitation * 100.0) / 100.0);
		}
		return hesitation;
	} // end getPieceHesitation

	/***********************************************************************************
	 * getCastleRook gets the nearby rook that may castle with the king
	 * 
	 * @param kingSquare the square the king is on or will be on
	 * @param pieces     the arrayList of all the pieces
	 * @return the rook Piece that can castle. returns null if there's no piece or
	 *         can't castle
	 */
	public Piece getCastleRook(Square kingSquare, ArrayList<Piece> pieces) {
		Piece rook = null;
		// white can only castle on row 1, black only on row 8
		int castleRow = (getColor() == Color.WHITE) ? 1 : 8;
		// get nearby rook
		if (kingSquare.sameSquare(3, castleRow)) {
			rook = StandardGame.getPieceOnSquare(1, castleRow, pieces);
		} else if (kingSquare.sameSquare(7, castleRow)) {
			rook = StandardGame.getPieceOnSquare(8, castleRow, pieces);
		}
		// make sure rook is not an empty space; make sure the piece is a rook; make
		// sure the rook can castle
		if (rook != null && rook.getType() == Type.ROOK && !(((Rook) rook).isCanCastle())) {
			rook = null;
		}
		return rook;
	} // end getCastleRook

	/******************************************************************************
	 * canCastleStill checks to see if castling is still a future possibility
	 * 
	 * @param pieces the arrayList of all pieces
	 * @return true if castling is still possible, false otherwise
	 */
	public boolean canCastleStill(ArrayList<Piece> pieces) {
		// check if king can castle
		if (!canCastle) {
			return false;
		}
		// find a rook that can castle still
		for (Piece p : pieces) {
			if (p.getType() == Piece.Type.ROOK && p.getColor() == getColor() && ((Rook) p).isCanCastle()) {
				return true;
			}
		}
		return false;
	} // end canCastleStill

	// getters, setters
	public boolean isCanCastle() {
		return canCastle;
	}

	public void setCanCastle(boolean canCastle) {
		this.canCastle = canCastle;
	}
}
