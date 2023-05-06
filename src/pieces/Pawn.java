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
 *         The pawn has the following movement patterns:
 * 
 *         - can move 1 space forward (toward the opposite color) any time if
 *         there is no piece in the way
 * 
 *         - can move 2 space forward on its first move if there are no pieces
 *         in the way
 * 
 *         - can capture a piece of the opposite color that is 1 space forward
 *         and on an adjacent column (diagonal)
 * 
 *         - can en passant (capture an adjacent pawn (same row) that has just
 *         moved 2 spaces forward)
 * 
 *         - when performing en passant, the pawn still moves 1 diagonal space
 *         forward
 * 
 *         - upon reaching the last row, the pawn turns into the player's choice
 *         of a Queen, Rook, Bishop, or Knight
 */

public class Pawn extends Piece {

	private boolean canGetEnPassant; // true if it just moved two spaces

	public Pawn(int xPosition, int yPosition, Piece.Color color, int index) {
		super(xPosition, yPosition, color, Type.PAWN);
		canGetEnPassant = false; // becomes true for one turn after moving 2 spaces in 1 move
	}

	public Pawn(Piece pieceCopy) {
		super(pieceCopy);
		this.canGetEnPassant = ((Pawn) pieceCopy).getCanGetEnPassant();
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
		boolean isWhite = true; // true if white, false if black
		if (getColor() == Color.BLACK) {
			isWhite = false;
		}
		// will either be null, or a piece that the pawn can enPassant
		Piece enPassantPiece = getEnPassantPiece(pieces);

		// WHITE gets an extra space if (y == 2) | BLACK gets an extra space if (y == 7)
		int extraSpace = 0; // 0: pawn can only go forward 1 | 1: pawn can go forward up to two
		if ((getPosition().getY() == 2 && isWhite)) {
			extraSpace++;
		} else if ((getPosition().getY() == 7 && !isWhite)) {
			extraSpace++;
		}
		// difference in x movement (must be 1 for capture, 0 for going forward)
		int xDiff = Math.abs(getPosition().getX() - destinationSquare.getX());
		// difference in y movement (absolute value)
		int yDiff = Math.abs(getPosition().getY() - destinationSquare.getY());
		// difference in y movement (WHITE must be negative | BLACK must be positive)
		int yMovement = getPosition().getY() - destinationSquare.getY();
		// true if there is a piece on destinationSquare
		boolean capturing = StandardGame.isPieceOnSquare(destinationSquare, pieces);

		// cannot move backwards
		if ((isWhite && yMovement > 0) || (!isWhite && yMovement < 0)) {
			hasLegalMove = false;
			// System.out.println ( "Invalid: Pawns cannot go backwards" );
		}
		// moving 1 forward (can't capture)
		else if (xDiff == 0 && yDiff == 1 && !capturing) {
			// make sure the king wouldn't be in danger
			hasLegalMove = notCheck(destinationSquare, pieces);
		}
		// moving 2 forward (can't capture) (and now able to get enPassant for 1 turn)
		else if (xDiff == 0 && yDiff == 1 + extraSpace && !capturing) {
			// make sure the way is clear
			// make sure the king wouldn't be in danger
			hasLegalMove = (notBlocked(getPosition(), destinationSquare, pieces))
					&& (notCheck(destinationSquare, pieces));
		}
		// capturing diagonally
		else if (yDiff == 1 && xDiff == 1 && capturing) {
			// make sure the pawn can take the square
			// make sure the king wouldn't be in danger
			hasLegalMove = (canTake(destinationSquare, pieces)) && (notCheck(destinationSquare, pieces));
		}
		// enPassant
		// make sure capturing in right direction
		else if (yDiff == 1 && xDiff == 1 && enPassantPiece != null
				&& destinationSquare.getX() == enPassantPiece.getPosition().getX()) {
			// make sure the king wouldn't be in danger
			hasLegalMove = notCheck(destinationSquare, pieces);
		} else {
			hasLegalMove = false;
		}
		return hasLegalMove;
	} // end hasLegalMove

	/****************************************************************************************
	 * canSupport determines if the piece can reach (capture) the supportSquare
	 * 
	 * @param supportSquare the square the piece is trying to support
	 * @param pieces        the array of all the pieces
	 * @return true if the piece can reach (capture) supportSquare, false otherwise
	 */
	public boolean canSupport(Square supportSquare, ArrayList<Piece> pieces) {
		boolean canSupport;
		boolean isWhite = true; // true if white, false if black
		if (getColor() == Color.BLACK) {
			isWhite = false;
		}
		// difference in x movement (must be 1 for capture, 0 for going forward)
		int xDiff = Math.abs(getPosition().getX() - supportSquare.getX());
		// difference in y movement (absolute value)
		int yDiff = Math.abs(getPosition().getY() - supportSquare.getY());
		// difference in y movement (WHITE must be negative | BLACK must be positive)
		int yMovement = getPosition().getY() - supportSquare.getY();

		// cannot move backwards
		if ((isWhite && yMovement > 0) || (!isWhite && yMovement < 0)) {
			canSupport = false;
		}
		// capturing diagonally
		else if (yDiff == 1 && xDiff == 1) {
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
		boolean isWhite = true; // true if white, false if black
		if (getColor() == Color.BLACK) {
			isWhite = false;
		}
		// difference in x movement
		int xDiff = Math.abs(getPosition().getX() - sightSquare.getX());
		// difference in y movement
		int yDiff = Math.abs(getPosition().getY() - sightSquare.getY());
		// difference in y movement (WHITE must be negative | BLACK must be positive)
		int yMovement = getPosition().getY() - sightSquare.getY();

		// cannot move backwards
		if ((isWhite && yMovement > 0) || (!isWhite && yMovement < 0)) {
			canSee = false;
		}
		// capturing diagonally
		else if (yDiff == 1 && xDiff == 1) {
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
		double squareComfort = 0.0;
		// the pawn goes in a positive direction for white, negative direction if black
		int direction = (getColor() == Piece.Color.WHITE) ? 1 : -1;
		// the back rank is 8 for white, and 1 for black
		int backRank = (getColor() == Piece.Color.WHITE) ? 8 : 1;
		// the amount of rows between the pawn and the back rank (including the pawn's
		// current row)
		int rowsLeft = Math.abs(getPosition().getY() - backRank) + 1;
		// award points for being a pass pawn
		if (isPassPawn(pieces, backRank, direction)) {
			squareComfort += (difficulty.getPawnPromoteDesire() / rowsLeft);
		}
		return squareComfort;
	} // end getSquareComfort

	/*******************************************************************************
	 * isPassPawn determines if any enemy pawns can stop this pawn from making it to
	 * the back rank
	 * 
	 * @param pieces the array of all pieces
	 * @return true if the pawn is a pass pawn, false otherwise
	 */
	public boolean isPassPawn(ArrayList<Piece> pieces, int backRank, int direction) {
		boolean passPawn = true;
		// x and y position of the pawn
		int x = getPosition().getX();
		int y = getPosition().getY();
		// pieces on the squares in front of the pawn
		Piece blockingPiece;
		// check column to left, column that the pawn is on, and column to the right
		for (int c = x - 1; c <= x + 1; c++) {
			// check each row between the pawn and the backRank
			for (int r = y; r != backRank; r += direction) {
				blockingPiece = StandardGame.getPieceOnSquare(c, r, pieces);
				// if there is a piece and it is a pawn
				if (blockingPiece != null && blockingPiece.getType() == Piece.Type.PAWN) {
					passPawn = false;
				}
			}
		}
		return passPawn;
	} // end isPassPawn

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
		hesitation += difficulty.getPawnHesitation();
		// save to reasoning if there is a hesitation
		if (hesitation > 0.01 || hesitation < -0.01) {
			currMove.getAddReason().println(this + ": " + Math.round(hesitation * 100.0) / 100.0);
		}
		return hesitation;
	} // end getPieceHesitation

	/***********************************************************************************************
	 * getEnPassantPiece gets the adjacent pawn that can receive enPassant
	 * 
	 * @param pieces the array of all the pieces
	 * @return enPassantPiece: the pawn that can receive enPassant, null if
	 *         nonexistent or can't receive enPassant
	 */
	public Piece getEnPassantPiece(ArrayList<Piece> pieces) {
		int x = getPosition().getX(); // current x position
		int y = getPosition().getY(); // current y position
		Piece enPassantPiece = StandardGame.getPieceOnSquare(x - 1, y, pieces); // check adjacent square

		// make sure adjacent square isn't empty
		// make sure adjacent piece is a pawn
		// make sure the adjacent pawn just moved two spaces
		if (!(enPassantPiece != null && enPassantPiece.getType() == Type.PAWN
				&& ((Pawn) enPassantPiece).getCanGetEnPassant())) {
			// check the other side
			enPassantPiece = StandardGame.getPieceOnSquare(x + 1, y, pieces);
			if (!(enPassantPiece != null && enPassantPiece.getType() == Type.PAWN
					&& ((Pawn) enPassantPiece).getCanGetEnPassant())) {
				// set enPassantPiece to null if there is no piece to enPassant
				enPassantPiece = null;
			}
		}
		return enPassantPiece;
	} // end getEnPassantPiece

	// getters and setters
	public void setCanGetEnPassant(boolean ep) {
		canGetEnPassant = ep;
	}

	public boolean getCanGetEnPassant() {
		return canGetEnPassant;
	}
}
