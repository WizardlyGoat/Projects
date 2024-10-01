package ai;

import java.util.ArrayList;

import game.Move;
import game.Square;
import pieces.Piece;

/**
 * 
 * @author John Hoffmann
 * 
 *         AIMove adds a score and Logger to give reasons to points awarded to
 *         moves
 *
 */
public class AIMove extends Move implements Comparable<AIMove> {

	private double score; // the move's score
	Logger logger; // logger that logs reasoning for the move

	/**
	 * @param piece       the piece being moved
	 * @param destination the piece's valid destination
	 * @param pieces      the ArrayList<Piece> of pieces
	 */
	public AIMove(Piece piece, Square destination, ArrayList<Piece> pieces) {
		super(piece, destination, pieces);
		score = 0.0;
		logger = new Logger();
	}

	/**
	 * adds given points to the score without logging it
	 * 
	 * @param points the offset amount
	 */
	public void offSetScore(double points) {
		score += points;
	}

	/**
	 * adds points to the current score and logs any amounts
	 * 
	 * @param points the points being added to score
	 * @param reason the reason for the points being added
	 */
	public void addToScore(double points, String reason) {
		score += points;
		logger.addToScore(points, reason, Logger.Difference.ANY);
	}

	/**
	 * adds points to the current score and logs any amounts. Includes an
	 * additional, more detailed sub logger
	 * 
	 * @param points     the points being added to score
	 * @param reason     the reason for the points being added
	 * @param difference the difference in points required for this to be logged
	 * @param logger     the sub logger that should also be logged
	 */
	public void addToScore(double points, String reason, Logger.Difference difference, Logger logger) {
		score += points;
		this.logger.addToScore(points, reason, difference, logger);
	}

	/**
	 * adds an indent followed by the given label to stringWriter
	 * 
	 * @param label the label being added
	 */
	public void labelReasoning(String label) {
		logger.labelReasoning(label);
	}

	/**
	 * labels and prints everything written in reasoning
	 */
	public void printReasoning() {
		System.out.println("\n" + this);
		System.out.println(logger.getStringWriter());
	}

	// getters and setters
	public double getScore() {
		return score;
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public int compareTo(AIMove o) {
		int score = (int) (this.score * 100);
		int otherScore = (int) (o.getScore() * 100);
		return score - otherScore;
	}

	@Override
	public String toString() {
		return String.format("[%s, %.2f]", super.toString(), score);
	}

}
