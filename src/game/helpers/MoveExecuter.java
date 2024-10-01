package game.helpers;

import java.util.ArrayList;

import ai.AI;
import game.Move;
import game.StandardGame;
import gui.ChessDriver;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;
import pieces.Piece.Color;
import pieces.Piece.Type;

/**
 * 
 * @author John Hoffmann
 * 
 *         MoveExecuter executes given moves and updates the GUI if asked to
 *
 */
public interface MoveExecuter {

	/**
	 * executes a move on the given board and updates the GUI accordingly, then
	 * resorts the pieces
	 * 
	 * @param move   the move being executed
	 * @param pieces the ArrayList<Piece> of pieces
	 * @param ai     the AI that may or may not be playing (needed for pawn
	 *               promotion decisions)
	 * @param gui    the graphics
	 */
	public static void movePiece(Move move, ArrayList<Piece> pieces, AI ai, ChessDriver gui) {
		movePiece(move, pieces, ai, gui, true);
	}

	/**
	 * executes a move on the given board without updating the GUI, then resorts the
	 * pieces
	 * 
	 * @param move   the move being executed
	 * @param pieces the ArrayList<Piece> of pieces
	 */
	public static void testMove(Move move, ArrayList<Piece> pieces) {
		movePiece(move, pieces, null, null, false);
	}

	/**
	 * executes a move on the given board and updates the GUI if updateGUI is true
	 * 
	 * @param move      the move being executed
	 * @param pieces    the ArrayList<Piece> of pieces
	 * @param ai        the AI that may or may not be playing (needed for pawn
	 *                  promotion decisions)
	 * @param gui       the graphics
	 * @param updateGUI true if graphics are updated, false otherwise
	 */
	private static void movePiece(Move move, ArrayList<Piece> pieces, AI ai, ChessDriver gui, boolean updateGUI) {

		if (updateGUI) {
			FXCommander.clearPastMoveEffects(gui);
		}

		Piece piece = Finder.getPieceOnSquare(move.getPiece().getPosition(), pieces);

		// take captured piece off the board
		if (move.getCapturePiece() != null) {
			Piece capturePiece = Finder.getPieceOnSquare(move.getCapturePiece().getPosition(), pieces);
			pieces.remove(capturePiece);

			if (updateGUI) {
				FXCommander.removeImageAtPosition(capturePiece.getPosition(), gui);
			}
		}

		switch (piece.getType()) {
		case PAWN:
			int y = move.getDestination().getY(); // new y position

			// turn canGetOnPassant on if pawn jumped two spaces
			if (Math.abs(y - piece.getPosition().getY()) == 2) {
				((Pawn) piece).setCanGetEnPassant(true);
			}
			// promote pawns on either back rank (row 1 or 8)
			else if (y == 1 || y == 8) {
				if (updateGUI) {
					piece = promotePawn(piece, pieces, ai, gui);
					FXCommander.updateImage(piece.getPosition(), piece.getType(), piece.getColor(), gui);
				} else {
					pieces.remove(piece);
					// for AI analysis, a pawn is always promoted to queen
					piece = new Queen(piece.getPosition().getX(), piece.getPosition().getY(), piece.getColor());
					pieces.add(piece);
				}
			}
			break;
		case KING:
			((King) piece).setCanCastle(false);
			break;
		case ROOK:
			((Rook) piece).setCanCastle(false);
			break;
		default:
			// do nothing
		}

		// move the rook if the king castled
		if (move.getRookJump() != null) {
			Piece rook = Finder.getPieceOnSquare(move.getRookJump().getPiece().getPosition(), pieces);
			rook.setPosition(move.getRookJump().getDestination());

			if (updateGUI) {
				FXCommander.movePiece(move.getRookJump(), gui);
			}
		}

		// move the piece
		piece.setPosition(move.getDestination());
		if (updateGUI) {
			FXCommander.movePiece(move, gui);
		}
	}

	/**
	 * promotes a given pawn to either a queen, rook, bishop, or knight
	 * 
	 * @param pawn   the pawn piece that is being promoted
	 * @param pieces the ArrayList<Piece> of all pieces
	 * @param ai     the AI that may or may not be playing
	 * @param gui    the graphics associated with this game
	 * @return the new piece that is made
	 */
	private static Piece promotePawn(Piece pawn, ArrayList<Piece> pieces, AI ai, ChessDriver gui) {
		int x = pawn.getPosition().getX(); // x position
		int y = pawn.getPosition().getY(); // y position
		Color color = pawn.getColor(); // the piece's color
		Type newType; // the new type of piece

		// computer promoting pawn
		if (ai.getColor() == color) {
			newType = Type.QUEEN;
		}

		// get input from user as to which piece to promote to (GUI)
		else {
			FXCommander.addPromotePawnBox(color, gui);
			StandardGame.waitForGUI(gui);
			newType = gui.getCommunication().getPromoteTo();
		}

		// promote the pawn
		pieces.remove(pawn);
		switch (newType) {
		case QUEEN:
			pieces.add(new Queen(x, y, color));
			break;
		case ROOK:
			pieces.add(new Rook(x, y, color));
			break;
		case BISHOP:
			pieces.add(new Bishop(x, y, color));
			break;
		case KNIGHT:
			pieces.add(new Knight(x, y, color));
		default:
			break;
		}
		return pieces.get(pieces.size() - 1);
	}
}
