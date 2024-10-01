package ai.analyzer;

import java.util.ArrayList;
import java.util.LinkedList;
import ai.AIMove;
import ai.Logger;
import ai.difficulty.Difficulty;
import game.EndGameCheck;
import game.Move;
import game.EndGameCheck.Ending;
import game.helpers.Copier;
import game.helpers.Finder;
import game.helpers.MoveExecuter;
import pieces.Piece;
import pieces.Piece.Color;

/**
 * 
 * @author John Hoffmann
 * 
 *         BoardAnalyzer gathers information about the board's state, like...
 * 
 *         - How much material each player has, in points
 * 
 *         - How much the material is worth, based on its position
 * 
 *         - What state the game is in (Opening, Mid, Late game)
 * 
 *         BoardAnalyzer also tests a given move, gathering some information
 *         about that board's state.
 * 
 *         - Are the pieces in danger of being captured
 *
 */
public class BoardAnalyzer {

	// enum for game state
	public enum GameState {
		OPENING, MIDGAME, LATEGAME
	}

	// basic information about the board
	private Piece.Color friend;
	private ArrayList<Piece> pieces;
	private Difficulty settings;
	private AIMove move;
	private Piece pieceAfterMoving;

	// loggers
	private Logger hesitationLogger = new Logger();
	private Logger captureLogger = new Logger();
	private Logger dangerLogger = new Logger();
	private Logger endGameLogger = new Logger();

	// material and position point worth
	private PointTotal currPointTotal;
	private PointTotal futurePointTotal;

	// game state (time-wise)
	private GameState gameState;
	private int unmovedMinorPieces;

	// future pieces
	private ArrayList<Piece> futurePieces;
	private Ending ending;
	private double worstDanger;

	/**
	 * @param pieces   the ArrayList<Piece> of pieces
	 * @param history  the LinkedList of past moves
	 * @param settings the Difficulty
	 * @param logger   a logger to make more detailed analysis
	 */
	public BoardAnalyzer(ArrayList<Piece> pieces, LinkedList<Move> history, AIMove move, Difficulty settings) {
		friend = move.getPiece().getColor();
		this.pieces = pieces;
		this.settings = settings;
		this.move = move;
		initializeGameState();

		// get the current state of the board
		currPointTotal = new PointTotal(pieces);

		// test the move
		futurePieces = Copier.copyBoard(pieces);
		MoveExecuter.testMove(move, futurePieces);
		pieceAfterMoving = Finder.getPieceOnSquare(move.getDestination(), futurePieces);

		// get the new state of the board
		futurePointTotal = new PointTotal(futurePieces);
		initializeDangerLevels();

		// see if the game has ended with this move
		history.add(move);
		EndGameCheck endGameCheck = new EndGameCheck(futurePieces, history, Color.getOppColor(friend));
		history.removeLast();
		ending = endGameCheck.getEnding();
	}

	/**
	 * PointTotal adds up the material and position worth of each piece. It also
	 * logs how each piece's position score has changed
	 */
	public class PointTotal {
		// literal point worth of pieces
		private double aiMaterial = 0.0;
		private double oppMaterial = 0.0;

		// extra points for good position
		private double aiPosition = 0.0;
		private double oppPosition = 0.0;

		// logger for how position scores have changed
		private Logger pieceBetterment = new Logger();

		public PointTotal(ArrayList<Piece> pieces) {
			double worthBefore;

			for (Piece p : pieces) {
				// save the piece's previous position score
				worthBefore = p.positionWorth;

				// update the piece's position score
				p.positionWorth = settings.getPieceControl(p, pieces, BoardAnalyzer.this);

				if (p.getColor() == friend) {
					aiMaterial += p.getType().worth;
					aiPosition += p.positionWorth;
				} else {
					oppMaterial += p.getType().worth;
					oppPosition += p.positionWorth;
				}
				pieceBetterment.addClarification(p.positionWorth - worthBefore, p + "", Logger.Difference.MINUTE);
			}
		}

		public double getDifference() {
			return (aiMaterial + aiPosition) - (oppMaterial + oppPosition);
		}
	}

	/**
	 * initializeGameState determines how far the players are into the game
	 */
	private void initializeGameState() {
		int aiNumPieces = 0;
		int oppNumPieces = 0;

		// Count the amount of Major and Minor pieces
		for (Piece p : pieces) {
			if (p.getColor() == friend && p.isMajorMinorPiece())
				aiNumPieces++;
			else if (p.isMajorMinorPiece())
				oppNumPieces++;

			// Count friendly, undeveloped minor pieces that haven't developed
			if (p.getColor() == friend && p.isMinorPiece() && p.getPosition().isFirstRank(friend)) {
				unmovedMinorPieces++;
			}
		}

		// There are 14 Major and Minor pieces to start with
		if (aiNumPieces + oppNumPieces >= 12) {
			// only up to 2 have been lost in total
			gameState = GameState.OPENING;
		} else if (aiNumPieces <= 3 || oppNumPieces <= 3) {
			// only 3 or fewer remain for at least one player
			gameState = GameState.LATEGAME;
		} else {
			gameState = GameState.MIDGAME;
		}
	}

	/**
	 * initializeDangerLevels determines what the worst danger is
	 */
	public void initializeDangerLevels() {
		worstDanger = 0.0;
		double loss;

		for (Piece p : futurePieces) {
			if (p.getColor() != friend) {
				continue;
			}

			Exchange exchange = new Exchange(futurePieces, p, dangerLogger);
			loss = exchange.getLoss();

			// see if ai is aware of this loss
			if (!exchange.isEmpty()) {
				loss = settings.getDangerAwareness(this, exchange);
				dangerLogger.print("\n");
			}
			// see if this is the worst loss
			if (loss < worstDanger) {
				worstDanger = loss;
			}
		}
	}

	/**
	 * getMoveScore returns the difference between the board after the move is
	 * tested, and the board currently
	 * 
	 * @return futurePointTotal - currPointTotal (positive number = improvement in
	 *         material / position worth)
	 */
	public double getMoveScore() {
		return futurePointTotal.getDifference() - currPointTotal.getDifference();
	}

	/**
	 * getCurrentScore returns the point difference between the ai's material worth
	 * and opponent's material worth
	 * 
	 * @return the material, position difference (positive number = better pieces)
	 */
	public double getCurrentScore() {
		return currPointTotal.getDifference();
	}

	/**
	 * getFutureScore returns the point difference between the ai's material worth
	 * and opponent's material worth, if the move were to be executed
	 * 
	 * @return the possible material, position difference if the move is done
	 *         (positive number = will have better pieces)
	 */
	public double getFutureScore() {
		return futurePointTotal.getDifference();
	}

	// getters and setters
	public Piece.Color getFriendlyColor() {
		return friend;
	}

	public ArrayList<Piece> getPieces() {
		return pieces;
	}

	public ArrayList<Piece> getFuturePieces() {
		return futurePieces;
	}

	public Piece getPieceAfterMoving() {
		return pieceAfterMoving;
	}

	public AIMove getMove() {
		return move;
	}

	public Logger getPieceBetterment() {
		return futurePointTotal.pieceBetterment;
	}

	public Logger getHesitationLogger() {
		return hesitationLogger;
	}

	public Logger getCaptureLogger() {
		return captureLogger;
	}

	public Logger getEndGameLogger() {
		return endGameLogger;
	}

	public Logger getDangerLogger() {
		return dangerLogger;
	}

	public double getWorstDanger() {
		return worstDanger;
	}

	public int getUnmovedMinorsPieces() {
		return unmovedMinorPieces;
	}

	public Ending getEnding() {
		return ending;
	}

	public GameState getGameState() {
		return gameState;
	}
}
