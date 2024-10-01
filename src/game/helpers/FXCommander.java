package game.helpers;

import game.Move;
import game.Square;

import java.util.ArrayList;

import game.EndGameCheck.Ending;
import gui.ChessDriver;
import javafx.application.Platform;
import pieces.Piece;
import pieces.Piece.Color;
import pieces.Piece.Type;

/**
 * 
 * @author John Hoffmann
 * 
 *         FXCommander supplies methods that run commands on the FX Thread
 *         started in ChessDriver
 *
 */
public interface FXCommander {

	/**
	 * adds the given pieces to the gui
	 * 
	 * @param pieces the pices being added to the gui
	 * @param gui
	 */
	public static void addStartingPieces(ArrayList<Piece> pieces, ChessDriver gui) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.addStartingPieces(pieces);
			}
		});
	}

	/**
	 * flips the board to give the given color the POV
	 * 
	 * @param playerTurn the color who is gaining POV
	 * @param gui
	 */
	public static void setPerspective(Color playerTurn, ChessDriver gui) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.setPerspective(playerTurn);
			}
		});
	}

	/**
	 * moves a piece in the gui
	 * 
	 * @param move the move being executed
	 * @param gui
	 */
	public static void movePiece(Move move, ChessDriver gui) {
		Square s = new Square(move.getPiece().getPosition());
		Square d = new Square(move.getDestination());
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.movePiece(s, d);
			}
		});
	}

	/**
	 * removes the image at the given position (used for enPassant)
	 * 
	 * @param position the square whose image is being removed
	 * @param gui
	 */
	public static void removeImageAtPosition(Square position, ChessDriver gui) {
		Square p = new Square(position);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.removeImageAtPosition(p);
			}
		});
	}

	/**
	 * highlights all legal moves and legal takes in the gui
	 * 
	 * @param legalMoves the squares which the piece can move to
	 * @param legalTakes the squares which the piece can take on
	 * @param gui
	 */
	public static void addLegalMoveEffects(ArrayList<Square> legalMoves, ArrayList<Square> legalTakes,
			ChessDriver gui) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.addLegalMoveEffects(legalMoves);
				gui.addLegalTakeEffects(legalTakes);
				// legalTakes must come after because enPassant captures will show up under
				// legalMovs as well
			}
		});
	}

	/**
	 * removes all highlights from previously highlighted squares (except past move
	 * squares)
	 * 
	 * @param gui
	 */
	public static void clearLegalMoveEffects(ChessDriver gui) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.clearLegalMoveEffects();
			}
		});
	}

	/**
	 * removes all highlights from past move squares
	 * 
	 * @param gui
	 */
	public static void clearPastMoveEffects(ChessDriver gui) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.clearPastMoveEffects();
			}
		});
	}

	/**
	 * adds a pawn promotion box so that the player can choose which piece to
	 * promote to
	 * 
	 * @param color the color that the piece buttons will show up as
	 * @param gui
	 */
	public static void addPromotePawnBox(Color color, ChessDriver gui) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.addPromotePawnBox(color);
			}
		});
	}

	/**
	 * updates a pieces image (used for pawn promotion)
	 * 
	 * @param position the pawn's position
	 * @param type     the type of piece that the pawn is turing into
	 * @param color    the color of the pawn
	 * @param gui
	 */
	public static void updateImage(Square position, Type type, Color color, ChessDriver gui) {
		Square p = new Square(position);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				gui.updateImage(p, type, color);
			}
		});
	}

	/**
	 * displays the end game message
	 * 
	 * @param ending the type of ending
	 * @param gui
	 */
	public static void displayEndGame(Ending ending, ChessDriver gui) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// clear all effects
				gui.clearLegalMoveEffects();
				gui.clearPastMoveEffects();
				// display end game message
				gui.displayEndGame(ending);
			}
		});
	}

}
