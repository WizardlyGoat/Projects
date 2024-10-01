package gui.nodes;

import gui.Style;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * 
 * @author John Hoffmann
 * 
 *         Rank is a BorderPane that holds a label (for the file or rank it
 *         labels) and it's coordinates
 *
 */
public class Rank extends BorderPane {

	private Label fileRank; // label for files and ranks
	int c = 0; // column or files
	int r = 0; // row or ranks

	/**
	 * @param name the character representing the column 'a' through 'h'
	 */
	public Rank(char name) {
		// ASCII value of 'a' is 97
		c = name - 96;
		fileRank = Style.buildLabel("" + name);
		setPrefHeight(Style.SQUARE_LENGTH / 2);
		setPrefWidth(Style.SQUARE_LENGTH);
		setCenter(fileRank);
	}

	/**
	 * @param name the integer representing the row 1 through 8
	 */
	public Rank(int name) {
		// ASCII value of '1' is 49 (just a fun fact)
		r = name;
		fileRank = Style.buildLabel("" + name);
		setPrefHeight(Style.SQUARE_LENGTH);
		setPrefWidth(Style.SQUARE_LENGTH / 2);
		setCenter(fileRank);
	}

	public Label getName() {
		return fileRank;
	}

	public int getC() {
		return c;
	}

	public int getReverseC() {
		return Math.abs(c - 8) + 1;
	}

	public int getR() {
		return r;
	}

	public int getReverseR() {
		return Math.abs(r - 8) + 1;
	}

	@Override
	public String toString() {
		return "Rank " + c + "" + r;
	}
}
