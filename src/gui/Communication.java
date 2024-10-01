package gui;

import ai.difficulty.Difficulty.DifficultyLevel;
import game.Square;
import javafx.scene.control.RadioButton;
import pieces.Piece.Color;
import pieces.Piece.Type;

/**
 * 
 * @author John Hoffmann
 * 
 *         Communication is the bridge between the front end GUI and back end
 *
 */
public class Communication {

	// communication with chess thread
	private boolean playingAI; // true if player is playing against the ai
	private game.Square clickedSquare; // the square that was last clicked
	private DifficultyLevel difficulty; // the computer's difficulty
	private Color playAs; // what color the player is playing as
	private Type promoteTo; // what piece a pawn is promoting to

	public Communication() {
		clickedSquare = new Square(0, 0);
	}

	/**
	 * sets the playAs and difficulty fields to their correct values based on the
	 * menu GUI
	 * 
	 * @param menu the source of information
	 */
	public void initializeAI(Menu menu) {
		// color
		String playAs = ((RadioButton) menu.getPlayAsToggle().getSelectedToggle()).getText();
		for (Color c : Color.values()) {
			if (c.name.equals(playAs)) {
				this.playAs = c;
				break;
			}
		}
		// difficulty
		String difficulty = ((RadioButton) menu.getDifficultyToggle().getSelectedToggle()).getText();
		for (DifficultyLevel d : DifficultyLevel.values()) {
			if (d.name.equals(difficulty)) {
				this.difficulty = d;
				break;
			}
		}
	}

	// getters and setters
	public Square getClickedSquare() {
		return clickedSquare;
	}

	public void setClickedSquare(Square clickedSquare) {
		this.clickedSquare = clickedSquare;
	}

	public DifficultyLevel getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(DifficultyLevel difficulty) {
		this.difficulty = difficulty;
	}

	public Color getPlayAs() {
		return playAs;
	}

	public void setPlayAs(Color playAs) {
		this.playAs = playAs;
	}

	public Type getPromoteTo() {
		return promoteTo;
	}

	public void setPromoteTo(Type promoteTo) {
		this.promoteTo = promoteTo;
	}

	public boolean isPlayingAI() {
		return playingAI;
	}

	public void setPlayingAI(boolean playingAI) {
		this.playingAI = playingAI;
	}

	@Override
	public String toString() {
		return "Communication [playingAI=" + playingAI + ", clickedSquare=" + clickedSquare + ", difficulty="
				+ difficulty + ", playAs=" + playAs + ", promoteTo=" + promoteTo + "]";
	}

}
