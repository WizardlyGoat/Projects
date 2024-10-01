package pieces;

import java.util.ArrayList;

import game.Square;
import game.helpers.Finder;

/**
 * 
 * @author John Hoffmann
 * 
 *         The pawn has the following movement patterns:
 * 
 *         - can move 1 space forward (toward the opposite color) any time if
 *         there is no piece in the way
 * 
 *         - can move 2 spaces forward on its first move if there are no pieces
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

	public Pawn(int xPosition, int yPosition, Piece.Color color) {
		super(xPosition, yPosition, color, Type.PAWN);
		canGetEnPassant = false;
	}

	public Pawn(Piece pieceCopy) {
		super(pieceCopy);
		this.canGetEnPassant = ((Pawn) pieceCopy).getCanGetEnPassant();
	}

	@Override
	public boolean hasLegalMove(Square destination, ArrayList<Piece> pieces) {
		boolean isWhite = getColor() == Color.WHITE ? true : false;
		boolean capturing = Finder.isPieceOnSquare(destination, pieces);
		boolean canDoubleJump = (getPosition().getY() == 2 && isWhite) || (getPosition().getY() == 7 && !isWhite);

		int xDiff = Math.abs(getPosition().getX() - destination.getX());
		int yDiff = Math.abs(getPosition().getY() - destination.getY());
		int yMovement = getPosition().getY() - destination.getY();

		// moving backwards
		if ((isWhite && yMovement > 0) || (!isWhite && yMovement < 0)) {
			return false;
		}
		// moving 1 forward
		if (!capturing && xDiff == 0 && yDiff == 1) {
			return notCheck(destination, pieces);
		}
		// moving 2 forward
		if (!capturing && canDoubleJump && xDiff == 0 && yDiff == 2) {
			return notBlocked(getPosition(), destination, pieces) && notCheck(destination, pieces);
		}
		// capturing diagonally
		if (capturing && yDiff == 1 && xDiff == 1) {
			return canTake(destination, pieces) && notCheck(destination, pieces);
		}
		// En Passant
		if (yDiff == 1 && xDiff == 1) {
			Piece enPassantPiece = getEnPassantPiece(pieces);

			return enPassantPiece != null && destination.getX() == enPassantPiece.getPosition().getX()
					&& notCheck(destination, pieces);
		}
		return false;
	}

	@Override
	public boolean canSupport(Square supportSquare, ArrayList<Piece> pieces) {
		boolean isWhite = getColor() == Color.WHITE ? true : false;

		int xDiff = Math.abs(getPosition().getX() - supportSquare.getX());
		int yDiff = Math.abs(getPosition().getY() - supportSquare.getY());
		int yMovement = getPosition().getY() - supportSquare.getY();

		// moving backwards
		if ((isWhite && yMovement > 0) || (!isWhite && yMovement < 0)) {
			return false;
		}
		// capturing diagonally
		if (yDiff == 1 && xDiff == 1) {
			return notCheck(supportSquare, pieces);
		}
		return false;
	}

	@Override
	public boolean canSee(Square sightSquare, ArrayList<Piece> pieces) {
		boolean isWhite = getColor() == Color.WHITE ? true : false;

		int xDiff = Math.abs(getPosition().getX() - sightSquare.getX());
		int yDiff = Math.abs(getPosition().getY() - sightSquare.getY());
		int yMovement = getPosition().getY() - sightSquare.getY();

		// moving backwards
		if ((isWhite && yMovement > 0) || (!isWhite && yMovement < 0)) {
			return false;
		}
		// capturing diagonally
		if (yDiff == 1 && xDiff == 1) {
			return true;
		}
		return false;
	}

	/**
	 * gets the adjacent pawn that can receive enPassant
	 * 
	 * @param pieces the ArrayList<Piece> of pieces
	 * @return the pawn that can receive enPassant, null otherwise
	 */
	public Piece getEnPassantPiece(ArrayList<Piece> pieces) {
		int x = getPosition().getX();
		int y = getPosition().getY();
		Piece piece = Finder.getPieceOnSquare(x - 1, y, pieces);

		// check left side
		if (piece != null && piece.getType() == Type.PAWN && ((Pawn) piece).getCanGetEnPassant()) {
			return piece;
		}

		// check right side
		piece = Finder.getPieceOnSquare(x + 1, y, pieces);
		if (piece != null && piece.getType() == Type.PAWN && ((Pawn) piece).getCanGetEnPassant()) {
			return piece;
		}
		return null;
	}

	/**
	 * isPassedPawn determines if this pawn is a passed pawn (has no enemy pawns
	 * blocking it or threatening to take it before promoting)
	 * 
	 * @param pieces the ArrayList of pieces
	 * @return true, if this is a passed pawn. false otherwise
	 */
	public boolean isPassedPawn(ArrayList<Piece> pieces) {
		Piece.Color color = getColor();
		for (Piece p : pieces) {
			// skip non-pawns and same color pawns
			if (p.getType() != Piece.Type.PAWN || p.getColor() == color) {
				continue;
			}
			// skip pawns not within 1 column (x)
			if (Math.abs(p.getPosition().getX() - getPosition().getX()) > 1) {
				continue;
			}
			// for white pawns, blocking pawns have greater y position
			if (color == Color.WHITE && p.getPosition().getY() > getPosition().getY()) {
				return false;
			}
			// for black pawns, blocking pawns have lesser y position
			if (color == Color.BLACK && p.getPosition().getY() < getPosition().getY()) {
				return false;
			}
		}

		return true;
	}

	// getters and setters
	public void setCanGetEnPassant(boolean ep) {
		canGetEnPassant = ep;
	}

	public boolean getCanGetEnPassant() {
		return canGetEnPassant;
	}
}
