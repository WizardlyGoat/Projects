package ai;

import java.io.PrintWriter;
import java.io.StringWriter;
import game.helpers.ConsoleColors;

/**
 * 
 * @author LesbianGoat
 *
 *         Logger logs all AI reasoning for adding or subtracting points to a
 *         move
 *
 */
public class Logger implements ConsoleColors {

	// enum for what differences should be logged
	public enum Difference {
		ANY(0.00), MINUTE(0.01), SMALL(0.10), QUARTER(0.25);

		public double amount;

		Difference(double difference) {
			this.amount = difference;
		}
	}

	private StringWriter stringWriter;
	private PrintWriter printWriter;

	public Logger() {
		stringWriter = new StringWriter();
		printWriter = new PrintWriter(stringWriter);
	}

	/**
	 * logs the reasoning for an addition of points
	 * 
	 * @param points     the points being added to score
	 * @param reason     the reason for the points being added
	 * @param difference the difference in points required for this to be logged
	 */
	public void addToScore(double points, String reason, Difference difference) {
		if (Math.abs(points) >= difference.amount) {
			printReason(points, reason);
		}
	}

	/**
	 * logs the reasoning for an addition of points with an additional, more
	 * detailed sub logger
	 * 
	 * @param points     the points being added to score
	 * @param reason     the reason for the points being added
	 * @param difference the difference in points required for this to be logged
	 * @param logger     the sub logger that should also be logged with this logger
	 */
	public void addToScore(double points, String reason, Difference difference, Logger logger) {
		if (Math.abs(points) >= difference.amount) {
			printWriter.print(logger.getStringWriter());
			printReason(points, reason);
		}
	}

	/**
	 * clarifies a partial reason why some points were added
	 * 
	 * @param points     the partial amount of points being clarified
	 * @param reason     the reason for the points being added
	 * @param difference the difference in points required for this to be logged
	 * @return points the amount of points clarified
	 */
	public double addClarification(double points, String reason, Difference difference) {
		if (Math.abs(points) >= difference.amount) {
			printClarification(points, reason);
		}
		return points;
	}

	/**
	 * adds an indent followed by the given label to stringWriter
	 * 
	 * @param label the label being added
	 */
	public void labelReasoning(String label) {
		printWriter.println("\t" + YELLOW + "(" + label + ")" + RESET);
	}

	/**
	 * prints the given reason without adding a new line
	 * 
	 * @param reason the partial reason being added
	 */
	public void print(String reason) {
		printWriter.print(reason);
	}

	/**
	 * print formats the points then reason
	 * 
	 * @param points the points being added to score
	 * @param reason the reason for the points being added
	 */
	private void printReason(double points, String reason) {
		printWriter.printf("%5.2f: %s%n", points, reason);
	}

	/**
	 * print formats the reason then points
	 * 
	 * @param points the partial points being added to score
	 * @param reason the reason for the points being added
	 */
	private void printClarification(double points, String reason) {
		printWriter.printf("%s: %.2f%n", reason, points);
	}

	/**
	 * rounds a double to the next hundredth
	 * 
	 * @param num
	 * @return num rounded the the hundredth
	 */
	public static double roundDouble(double num) {
		return Math.round(num * 100.0) / 100.0;
	}

	// getters and setters
	public StringWriter getStringWriter() {
		return stringWriter;
	}

}
