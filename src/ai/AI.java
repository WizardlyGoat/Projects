package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import ai.analyzer.MoveAnalyzer;
import ai.difficulty.BestBot;
import ai.difficulty.Difficulty.DifficultyLevel;
import ai.difficulty.NoobBot;
import game.Move;
import game.Square;
import game.helpers.Printer;
import pieces.Piece;
import pieces.Piece.Color;

/**
 * @author John Hoffmann
 * 
 *         AI is responsible for analyzing a board and producing the best move
 *         in response
 *
 */
public class AI {
	private Color color;
	private ai.difficulty.Difficulty settings;
	private MoveAnalyzer moveAnalyzer;
	private ArrayList<AIMove> possibleMoves;

	public AI() {
		color = null;
		settings = null;
		moveAnalyzer = null;
		possibleMoves = null;
	}

	/**
	 * makes a move after analyzing the given game
	 * 
	 * @param pieces  the ArrayList<Piece> of pieces
	 * @param history the LinkedList of past moves
	 * @return the highest scoring AIMove
	 */
	public AIMove makeMove(ArrayList<Piece> pieces, LinkedList<Move> history) {
		possibleMoves = getAllPossibleMoves(pieces);

		moveAnalyzer.analyzeMoves(pieces, possibleMoves, history);

		Collections.sort(possibleMoves);
		Collections.reverse(possibleMoves);

		Printer.printPossibleMoves(possibleMoves);
		return possibleMoves.get(0);
	}

	/**
	 * makes an ArrayList<Piece> of all of the possible AIMoves
	 * 
	 * @param pieces the ArrayList<Piece> of pieces
	 * @return the ArrayList<Piece> of possible AIMoves
	 */
	private ArrayList<AIMove> getAllPossibleMoves(ArrayList<Piece> pieces) {
		ArrayList<AIMove> possibleMoves = new ArrayList<AIMove>();
		Square destination = new Square();

		// skip opponent's pieces
		for (Piece p : pieces) {
			if (p.getColor() != color) {
				continue;
			}
			// check every square for possible moves
			for (int c = 1; c < 9; c++) {
				for (int r = 1; r < 9; r++) {
					destination.setPosition(c, r);

					if (p.hasLegalMove(destination, pieces)) {
						possibleMoves.add(new AIMove(p, destination, pieces));
					}
				}
			}
		}
		return possibleMoves;
	}

	// getters and setters
	public void initialize(Color color, DifficultyLevel diff) {
		this.color = color;

		// create the correct difficulty
		if (diff == DifficultyLevel.BESTBOT) {
			settings = new BestBot();
		} else if (diff == DifficultyLevel.NOOBBOT) {
			settings = new NoobBot();
		}
		// this is not great, but last case scenario, a best bot is created #best coder
		else {
			System.out.println("DIFFICULTY UNKNOWN!! Creating best bot instead. ~AI.java");
			settings = new BestBot();
		}

		moveAnalyzer = new MoveAnalyzer(settings);
	}

	public Color getColor() {
		return color;
	}

	public ArrayList<AIMove> getPossibleMoves() {
		return possibleMoves;
	}
}
