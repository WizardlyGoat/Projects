package game.helpers;
import java.util.ArrayList;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

/**
 * 
 * @author John Hoffmann
 * 
 *         Copier copies pieces for convenience
 *
 */
public interface Copier {

	/**
	 * clones given piece
	 * 
	 * @param piece the piece being copied
	 * @return the copied piece, or null if there was no piece
	 */
	public static Piece copyPiece(Piece piece) {
		if (piece == null) {
			return null;
		}
		Piece copy = null;
		switch (piece.getType()) {
		case ROOK:
			copy = new Rook(piece);
			break;
		case KNIGHT:
			copy = new Knight(piece);
			break;
		case BISHOP:
			copy = new Bishop(piece);
			break;
		case QUEEN:
			copy = new Queen(piece);
			break;
		case KING:
			copy = new King(piece);
			break;
		case PAWN:
			copy = new Pawn(piece);
		}
		return copy;
	}

	/**
	 * clones an ArrayList<Piece> of pieces
	 * 
	 * @param pieces the ArrayList<Piece> of pieces to copy
	 * @return the copied ArrayList<Piece>
	 */
	public static ArrayList<Piece> copyBoard(ArrayList<Piece> pieces) {
		ArrayList<Piece> piecesCopy = new ArrayList<Piece>();

		for (Piece p : pieces) {
			switch (p.getType()) {
			case ROOK:
				piecesCopy.add(new Rook(p));
				break;
			case KNIGHT:
				piecesCopy.add(new Knight(p));
				break;
			case BISHOP:
				piecesCopy.add(new Bishop(p));
				break;
			case QUEEN:
				piecesCopy.add(new Queen(p));
				break;
			case KING:
				piecesCopy.add(new King(p));
				break;
			case PAWN:
				piecesCopy.add(new Pawn(p));
			}
		}
		return piecesCopy;
	}
}
