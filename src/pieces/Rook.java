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
 *         The Rook can move along the columns or rows as long as no pieces are
 *         in the way
 * 
 */
public class Rook extends Piece {

	private boolean canCastle;

	public Rook(int xPosition, int yPosition, Piece.Color color) {
		super(xPosition, yPosition, color, Type.ROOK);
		canCastle = true; // becomes false as soon as this piece moves
	}

	public Rook(Piece pieceCopy) {
		super(pieceCopy);
		this.canCastle = ((Rook) pieceCopy).isCanCastle();
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

		// moving across a column / row
		if ((xDiff == 0 && yDiff > 0) || (xDiff > 0 && yDiff == 0)) {
			// make sure the way is not blocked
			// make sure the rook can take the square
			// make sure the king wouldn't be in danger
			hasLegalMove = (notBlocked(getPosition(), destinationSquare, pieces))
					&& (canTake(destinationSquare, pieces)) && (notCheck(destinationSquare, pieces));
		} else {
			hasLegalMove = false;
		}
		return hasLegalMove;
	} // end hasLegalMove

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

		// moving across a column / row
		if ((xDiff == 0 && yDiff > 0) || (xDiff > 0 && yDiff == 0)) {
			// make sure the way is not blocked
			// make sure the king wouldn't be in danger
			canSupport = (notBlocked(getPosition(), supportSquare, pieces)) && (notCheck(supportSquare, pieces));
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

		// moving across a column / row
		if ((xDiff == 0 && yDiff > 0) || (xDiff > 0 && yDiff == 0)) {
			// make sure the way is not blocked
			canSee = notBlocked(getPosition(), sightSquare, pieces);
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
	 * @param pieces   the array of all pieces
	 * @param currMove the current ComputerMove (with the destination square)
	 * @return hesitation: a positive (wants to move) or negative (doesn't want to
	 *         move) score
	 */
	public double getPieceHesitation(StandardGame game, ComputerMove currMove, ComSettings difficulty) {
		double hesitation = 0.0; // assume the piece is indifferent
		// adjust according to piece hesitation
		hesitation += difficulty.getRookHesitation();
		// save to reasoning if there is a hesitation
		if (hesitation > 0.01 || hesitation < -0.01) {
			currMove.getAddReason().println(this + ": " + Math.round(hesitation * 100.0) / 100.0);
		}
		return hesitation;
	} // end getPieceHesitation

	public boolean isCanCastle() {
		return canCastle;
	}

	public void setCanCastle(boolean canCastle) {
		this.canCastle = canCastle;
	}
}
