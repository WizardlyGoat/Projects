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

		// moving in an L shape
		if ((xDiff == 1 && yDiff == 2) || (xDiff == 2 && yDiff == 1)) {
			// make sure the knight can take the square
			// make sure the king wouldn't be in danger
			hasLegalMove = (canTake(destinationSquare, pieces)) && (notCheck(destinationSquare, pieces));
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

		// moving in an L shape
		if ((xDiff == 1 && yDiff == 2) || (xDiff == 2 && yDiff == 1)) {
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

		// moving in an L shape
		if ((xDiff == 1 && yDiff == 2) || (xDiff == 2 && yDiff == 1)) {
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
	 * @param pieces   the array of all pieces
	 * @param currMove the current ComputerMove (with the destination square)
	 * @return hesitation: a positive (wants to move) or negative (doesn't want to
	 *         move) score
	 */
	public double getPieceHesitation(StandardGame game, ComputerMove currMove, ComSettings difficulty) {
		double hesitation = 0.0; // assume the piece is indifferent
		// adjust according to piece hesitation
		hesitation += difficulty.getKnightHesitation();
		// save to reasoning if there is a hesitation
		if (hesitation > 0.01 || hesitation < -0.01) {
			currMove.getAddReason().println(this + ": " + Math.round(hesitation * 100.0) / 100.0);
		}
		return hesitation;
	} // end getPieceHesitation
}
