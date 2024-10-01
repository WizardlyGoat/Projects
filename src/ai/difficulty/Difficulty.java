package ai.difficulty;

import java.util.ArrayList;
import java.util.Random;
import ai.analyzer.BoardAnalyzer;
import ai.analyzer.Exchange;
import pieces.Piece;

public abstract class Difficulty {

	// enum for difficulties
	public enum DifficultyLevel {
		BESTBOT("Best Bot"), NOOBBOT("Noob Bot");

		public String name;

		DifficultyLevel(String name) {
			this.name = name;
		}
	}

	protected static Random random = new Random();

	/**
	 * HELPER
	 * METHODS------------------------------------------------------------------------------------
	 */

	/**
	 * getOffSet returns a double between negative amount and positive amount
	 * 
	 * @param amount the amount away from 0.0 the offSet can be
	 * @return a number between -amount and +amount
	 */
	protected double getOffSet(double amount) { // 2.52
		int bound = (int) (amount * 100); // 252
		bound = random.nextInt(bound * 2) - bound; // -252 to 252
		return bound / 100.0; // -2.52 to 2.52
	}

	/**
	 * ABSTRACT
	 * METHODS-----------------------------------------------------------------------------------
	 */

	/**
	 * getOffSet offsets the score based on the difficulty
	 * 
	 * - Allows the moves to be semi-random and different each game
	 * 
	 * @return a small, random double (usually between -1.00 to 1.00)
	 */
	public abstract double getOffSet();

	/**
	 * getPieceHesitation gives or subtracts points based on how ready the piece is
	 * to move, based on the difficulty. Deals with...
	 * 
	 * - When pieces want to be moved (i.e. queens waiting for mid game)
	 * 
	 * - Pieces wanting specific conditions being fulfilled (i.e. castling)
	 * 
	 * @param move          the move that the AI is analyzing
	 * @param boardAnalyzer the current analysis of the game
	 * @return a positive score, if the piece wants to move, negative if it doesn't
	 */
	public abstract double getPieceHesitation(BoardAnalyzer boardAnalyzer);

	/**
	 * getCapturePoints returns the score for capturing a piece. Usually the piece's
	 * material worth, 0 otherwise. Deals with...
	 * 
	 * - Adding points for capturing a piece
	 * 
	 * - Adding bonus (or negative) points for taking on specific squares (i.e.
	 * poison pawns)
	 * 
	 * @param boardAnalyzer the current analysis of the game
	 * @return a positive score, based on how good this capture is
	 */
	public abstract double getCapturePoints(BoardAnalyzer boardAnalyzer);

	/**
	 * getPieceControl assigns an amount of points based on where the piece is and
	 * what it can see. Deals with...
	 * 
	 * - Where the pieces want to be (i.e. knights in an outpost)
	 * 
	 * - Desire for center, offensive, or defensive squares
	 * 
	 * @param piece         the piece being examined
	 * @param pieces        the ArrayList<Piece> of pieces
	 * @param boardAnalyzer the current analysis of the game
	 * @return a positive score representing how much control the piece has
	 */
	public abstract double getPieceControl(Piece piece, ArrayList<Piece> pieces, BoardAnalyzer boardAnalyzer);

	/**
	 * getDangerAwareness subtracts points for putting pieces in danger. Deals
	 * with...
	 * 
	 * - What is the potential material loss after complex exchanges?
	 * 
	 * - Are there any potential forks, skewers?
	 * 
	 * - Not noticing certain dangers, based on the difficulty
	 * 
	 * @param boardAnalyzer the current analyzsis of the game
	 * @param exchange      the ArrayList of pieces possibly being lost
	 * @return a negative score representing how much danger the pieces are in, or 0
	 *         if there is no real danger
	 */
	public abstract double getDangerAwareness(BoardAnalyzer boardAnalyzer, Exchange exchange);

	/**
	 * getEndGameDesire gives or subtracts points for ending the game. Deals with...
	 * 
	 * - Being aware of the game's potential end (i.e. seeing checkmate)
	 * 
	 * - Knowing the difference between a good draw and a bad draw
	 * 
	 * @param boardAnalyzer the current analysis of the game
	 * @return a positive or negative score, based on the difficulty
	 */
	public abstract double getEndGameDesire(BoardAnalyzer boardAnalyzer);

}
