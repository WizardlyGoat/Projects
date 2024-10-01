package game;

import java.util.ArrayList;
import java.util.LinkedList;
import ai.AI;
import game.EndGameCheck.Ending;
import game.helpers.FXCommander;
import game.helpers.MoveExecuter;
import game.helpers.Printer;
import gui.ChessDriver;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Piece.Color;
import pieces.Queen;
import pieces.Rook;

/**
 * 
 * @author John Hoffmann
 * 
 *         StandardGame handles a standard chess game by starting it and trading
 *         turns
 * 
 */
public class StandardGame {
	// easy access to colors
	public static final Color WHITE = Color.WHITE;
	public static final Color BLACK = Color.BLACK;

	private ChessDriver gui;
	private ArrayList<Piece> pieces;
	private LinkedList<Move> history;
	private AI ai;
	private Ending ending;

	/**
	 * @param gui the GUI displaying the game
	 */
	public StandardGame(ChessDriver gui) {
		this.gui = gui;
		pieces = new ArrayList<Piece>();
		history = new LinkedList<Move>();
		ai = new AI();
		ending = Ending.NOT_OVER;
	}

	/**
	 * starts the game
	 */
	public void startGame() {
		setStartingPositions(pieces);
		FXCommander.addStartingPieces(pieces, gui);

		Color playerTurn = WHITE;
		Move move;
		Turn turn;
		EndGameCheck endGameCheck;

		// wait for a play button to be pressed
		waitForGUI(gui);

		// initialize the ai
		if (gui.getCommunication().isPlayingAI()) {
			ai.initialize(Color.getOppColor(gui.getCommunication().getPlayAs()),
					gui.getCommunication().getDifficulty());
		}

		// take turns moving pieces
		do {
			// computer turn
			if (playerTurn == ai.getColor()) {
				move = new Move(ai.makeMove(pieces, history), pieces);
				Printer.printBestThreeMoves(ai);
			}
			// player turn
			else {
				turn = new Turn(pieces, playerTurn, gui);
				move = turn.getMove();
			}

			// add the move to the game's history
			history.add(move);

			// execute the move
			MoveExecuter.movePiece(move, pieces, ai, gui);

			// switch player turns
			playerTurn = (playerTurn == WHITE) ? BLACK : WHITE;

			// clear new player's pawns of enPassant
			clearEnPassant(playerTurn, pieces);

			// see if the game is over
			endGameCheck = new EndGameCheck(pieces, history, playerTurn);
			ending = endGameCheck.getEnding();

		} while (ending == Ending.NOT_OVER);
	}

	/**
	 * makes this thread wait for communication to be notified
	 */
	public static void waitForGUI(ChessDriver gui) {
		try {
			synchronized (gui.getCommunication()) {
				gui.getCommunication().wait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * creates all starting pieces and adds them to the given ArrayList<Piece>
	 * 
	 * @param pieces the ArrayList<Piece> of pieces
	 */
	private void setStartingPositions(ArrayList<Piece> pieces) {
		// white pieces
		pieces.add(new Rook(1, 1, WHITE));
		pieces.add(new Knight(2, 1, WHITE));
		pieces.add(new Bishop(3, 1, WHITE));
		pieces.add(new Queen(4, 1, WHITE));
		pieces.add(new King(5, 1, WHITE));
		pieces.add(new Bishop(6, 1, WHITE));
		pieces.add(new Knight(7, 1, WHITE));
		pieces.add(new Rook(8, 1, WHITE));
		for (int i = 1; i < 9; i++) {
			pieces.add(new Pawn(i, 2, WHITE));
		}
		// black pieces
		pieces.add(new Rook(1, 8, BLACK));
		pieces.add(new Knight(2, 8, BLACK));
		pieces.add(new Bishop(3, 8, BLACK));
		pieces.add(new Queen(4, 8, BLACK));
		pieces.add(new King(5, 8, BLACK));
		pieces.add(new Bishop(6, 8, BLACK));
		pieces.add(new Knight(7, 8, BLACK));
		pieces.add(new Rook(8, 8, BLACK));
		for (int i = 1; i < 9; i++) {
			pieces.add(new Pawn(i, 7, BLACK));
		}
	}

	/**
	 * sets canGetEnPassant to false for each piece of the given color
	 * 
	 * @param playerTurn the color of pawns that will be affected
	 * @param pieces     the ArrayList<Piece> of pieces
	 */
	public static void clearEnPassant(Color playerTurn, ArrayList<Piece> pieces) {
		for (Piece p : pieces) {
			if (p instanceof Pawn && p.getColor() == playerTurn) {
				((Pawn) p).setCanGetEnPassant(false);
			}
		}
	}

	// getters and setters
	public Ending getEnding() {
		return ending;
	}

	public void setEnding(Ending ending) {
		this.ending = ending;
	}

	public ArrayList<Piece> getPieces() {
		return pieces;
	}

	public LinkedList<Move> getHistory() {
		return history;
	}

}