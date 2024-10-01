package gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pieces.Piece;
import ai.difficulty.Difficulty.DifficultyLevel;
import javafx.geometry.Pos;

/**
 * 
 * @author John Hoffmann
 * 
 *         Menu handles the main menu that is first seen when the program is
 *         started
 */
public class Menu {

	private static final String playHumanText = "Play against a human"; // Driver needs to differentiate play buttons
	private Scene menuScene;
	private Button playHuman;
	private Button playComputer;
	private ToggleGroup difficultyToggle;
	private ToggleGroup playAsToggle;

	/**
	 * @param x width of the menu screen
	 * @param y height of the menu screen
	 */
	public Menu(int x, int y) {
		// play against human
		playHuman = Style.buildButton(playHumanText);
		VBox playHumanBox = new VBox();
		playHumanBox.setAlignment(Pos.CENTER);
		playHumanBox.getChildren().add(playHuman);

		// play against the computer
		playComputer = Style.buildButton("Play against the Computer");

		Label difficultyLabel = Style.buildLabel("Difficulty");

		difficultyToggle = new ToggleGroup();
		RadioButton diff1 = Style.buildRadioButton(DifficultyLevel.BESTBOT.name, difficultyToggle, true);
		RadioButton diff2 = Style.buildRadioButton(DifficultyLevel.NOOBBOT.name, difficultyToggle);
		HBox difficultyBox = new HBox();
		difficultyBox.getChildren().addAll(diff1, diff2);
		difficultyBox.setSpacing(20);
		difficultyBox.setAlignment(Pos.CENTER);

		Label playAsLabel = Style.buildLabel("Play as...");

		playAsToggle = new ToggleGroup();
		RadioButton white = Style.buildRadioButton(Piece.Color.WHITE.name, playAsToggle, true);
		RadioButton black = Style.buildRadioButton(Piece.Color.BLACK.name, playAsToggle);
		HBox playAsBox = new HBox();
		playAsBox.getChildren().addAll(white, black);
		playAsBox.setSpacing(20);
		playAsBox.setAlignment(Pos.CENTER);

		VBox playComputerBox = new VBox();
		playComputerBox.setSpacing(20);
		playComputerBox.setAlignment(Pos.CENTER);
		playComputerBox.getChildren().addAll(playComputer, difficultyLabel, difficultyBox, playAsLabel, playAsBox);

		// configure root
		HBox menuRoot = new HBox();
		menuRoot.setAlignment(Pos.CENTER);
		menuRoot.setSpacing(20);
		menuRoot.setBackground(Background.fill(Style.BACKGROUND));
		menuRoot.getChildren().addAll(playHumanBox, playComputerBox);

		// add the root to the scene
		menuScene = new Scene(menuRoot, x, y);
	}

	// getters and setters
	public Scene getMenuScene() {
		return menuScene;
	}

	public static String getPlayhumantext() {
		return playHumanText;
	}

	public Button getPlay() {
		return playHuman;
	}

	public Button getPlayComputer() {
		return playComputer;
	}

	public ToggleGroup getDifficultyToggle() {
		return difficultyToggle;
	}

	public ToggleGroup getPlayAsToggle() {
		return playAsToggle;
	}

}
