package ai.difficulty;

import java.util.ArrayList;

import pieces.King;
import pieces.Piece;
import pieces.Rook;
import pieces.Piece.Color;

public interface Inquiry {

	/**
	 * determines if castling is still a future possibility
	 * 
	 * @param pieces the ArrayList<Piece> of pieces
	 * @param color  the color pieces trying to castle
	 * @return true if castling is still possible, false otherwise
	 */
	public static boolean canCastleStill(ArrayList<Piece> pieces, Color color) {
		boolean kingCanCastle = false;
		boolean rookCanCastle = false;

		for (Piece p : pieces) {
			if (((pieces.Piece) p).getType() == Piece.Type.KING && p.getColor() == color && ((King) p).canCastle()) {
				kingCanCastle = true;
			}

			if (p.getType() == Piece.Type.ROOK && p.getColor() == color && ((Rook) p).canCastle()) {
				rookCanCastle = true;
			}
		}
		return kingCanCastle && rookCanCastle;
	}

}
