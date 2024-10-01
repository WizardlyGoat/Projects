package game;


import java.util.ArrayList;

import ai.AIMove;
import game.helpers.Copier;
import game.helpers.Finder;
import pieces.King;
import pieces.Pawn;
import pieces.Piece;
import pieces.Piece.Type;

/**
 * 
 * @author John Hoffmann
 * 
 *         Move holds the key parts of a single move: the piece and it's
 *         destinations square
 *
 */
public class Move {

	private Piece piece; // the piece that is being moved
	private Piece capturePiece; // the piece being captured (if there is one)
	private Square destination; // the piece's valid destination
	private boolean pawnOrCapture; // this move is a pawn move or a capture move
	private Move rookJump; // an additional move (the rook moving during castle)

	/**
	 * @param piece       the piece being moved
	 * @param destination the piece's valid destination
	 * @param pieces      the ArrayList<Piece> of pieces
	 */
	public Move(Piece piece, Square destination, ArrayList<Piece> pieces) {
		this.piece = Copier.copyPiece(piece);
		this.capturePiece = Copier.copyPiece(getCapturePiece(piece, destination, pieces));
		this.destination = new Square(destination);
		pawnOrCapture = isPawnOrCaptureMove(piece, capturePiece);
		rookJump = isRookJump(piece, destination, pieces);
	}

	/**
	 * copies an ai.Move into this game.Move
	 * 
	 * @param aiMove the ai.Move being copied
	 * @param pieces the ArrayList<Piece> of pieces
	 */
	public Move(AIMove aiMove, ArrayList<Piece> pieces) {
		piece = Copier.copyPiece(aiMove.getPiece());
		destination = new Square(aiMove.getDestination());
		capturePiece = Copier.copyPiece(aiMove.getCapturePiece());
		pawnOrCapture = aiMove.isPawnOrCapture();
		rookJump = aiMove.getRookJump();
	}

	/**
	 * returns the piece being captured or null if there is none
	 * 
	 * takes into account that the piece could have been captured through En Passant
	 * 
	 * @param piece       the moving piece
	 * @param destination the piece's destination square
	 * @param pieces      the ArrayList<Piece> of pieces
	 * @return the captured piece, null otherwise
	 */
	private Piece getCapturePiece(Piece piece, Square destination, ArrayList<Piece> pieces) {
		Piece capturePiece = Finder.getPieceOnSquare(destination, pieces);
		if (capturePiece != null) {
			return capturePiece;
		}
		if (piece.getType() == Type.PAWN) {
			Piece enPassantPiece = ((Pawn) piece).getEnPassantPiece(pieces);
			int xDiff = Math.abs(piece.getPosition().getX() - destination.getX());
			int yDiff = Math.abs(piece.getPosition().getY() - destination.getY());

			if (enPassantPiece != null && yDiff == 1 && xDiff == 1
					&& destination.getX() == enPassantPiece.getPosition().getX()) {
				capturePiece = enPassantPiece;
			}
		}
		return capturePiece;
	}

	/**
	 * determines if the move is a pawnOrCaptureMove
	 * 
	 * @param piece        the piece being moved
	 * @param capturePiece the piece being captured (if there is one)
	 * @return ture if the move is a pawn move or capture move, false otherwise
	 */
	private boolean isPawnOrCaptureMove(Piece piece, Piece capturePiece) {
		return (piece.getType() == Type.PAWN || capturePiece != null);
	}

	/**
	 * determines if this move was a castle. If so, then the rook's move is returned
	 * 
	 * @param piece       the piece moving
	 * @param destination the square the piece is moving to
	 * @param pieces      the ArrayList<Piece> of all pieces
	 * @return the rook that the king is castling with, null otherwise
	 */
	private Move isRookJump(Piece piece, Square destination, ArrayList<Piece> pieces) {
		// only kings castle
		if (piece.getType() != Type.KING) {
			return null;
		}
		// castling only occurs when the king moves 2 squares horizontally
		if (Math.abs(piece.getPosition().getX() - destination.getX()) == 2) {
			Piece rook = ((King) piece).getCastleRook(destination, pieces);

			int rookXDestination = (rook.getPosition().getX() == 1) ? 4 : 6;
			Square rookDestination = new Square(rookXDestination, rook.getPosition().getY());

			return new Move(rook, rookDestination, pieces);
		}
		return null;
	}

	// getters and setters
	public Piece getPiece() {
		return piece;
	}

	public Piece getCapturePiece() {
		return capturePiece;
	}

	public void setCapturePiece(Piece capturePiece) {
		this.capturePiece = capturePiece;
	}

	public boolean isPawnOrCapture() {
		return pawnOrCapture;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}

	public Square getDestination() {
		return destination;
	}

	public void setDestination(Square destinationSquare) {
		this.destination = destinationSquare;
	}

	public Move getRookJump() {
		return rookJump;
	}

	@Override
	public String toString() {
		return piece.getType() + " to " + destination;
	}
}
