package game.helpers;

import java.util.ArrayList;

import ai.AI;
import ai.AIMove;

/**
 * 
 * @author John Hoffmann
 * 
 *         Printer prints information concerning the AI's reasoning to the
 *         console
 *
 */
public interface Printer {

	/**
	 * prints the ai's best three moves
	 * 
	 * @param ai
	 */
	public static void printBestThreeMoves(AI ai) {
		// print the 3rd best move if present
		if (ai.getPossibleMoves().size() > 2) {
			System.out.println("\n3rd Best Scoring Move...........................................");
			ai.getPossibleMoves().get(2).printReasoning();
		}
		// print the 2nd best move if present
		if (ai.getPossibleMoves().size() > 1) {
			System.out.println("\n2nd Best Scoring Move...........................................");
			ai.getPossibleMoves().get(1).printReasoning();
		}
		// print the best move (the move that was played)
		System.out.println("\nBest Scoring Move...............................................");
		ai.getPossibleMoves().get(0).printReasoning();
	}

	/**
	 * prints all of the given AIMoves
	 * 
	 * @param possibleMoves
	 */
	public static void printPossibleMoves(ArrayList<AIMove> possibleMoves) {
		System.out.println("\nHere are the possible moves...");
		for (AIMove m : possibleMoves) {
			System.out.println(m);
		}
	}
}
