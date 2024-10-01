package gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import gui.nodes.ImageButton;
import gui.nodes.Square;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pieces.Piece;

/**
 * 
 * @author John Hoffmann
 * 
 *         Style keeps all of the final fields that the GUI uses. It also has
 *         some methods to build some simple nodes
 *
 */
public class Style {
	// sizes
	public static final int SQUARE_LENGTH = 65; // length of each square
	public static final double PIECE_SIZE = 0.95; // how the piece fits in a square
	public static final int BUTTON_HEIGHT = 30;
	// fonts
	public static final String FONT = "CASPIAN";
	// colors
	public static final Color LIGHT = Color.FLORALWHITE; // light squares
	public static final Color DARK = Color.DIMGREY; // dark squares
	public static final Color BACKGROUND = Color.DARKSLATEGREY; // for any background
	public static final Color PAST_MOVE = Color.ROYALBLUE; // for the two squares that make up the last move
	public static final Color LEGAL_MOVE = Color.SEAGREEN; // all squares a piece can move to
	public static final Color LEGAL_TAKE = Color.DARKRED; // all squares a piece can take on
	// effects
	public static final Glow BRIGHT_GLOW = new Glow(1.0); // 0.0 - 1.0
	public static final Glow MEDIUM_GLOW = new Glow(0.7);
	public static final Glow DULL_GLOW = new Glow(0.5);
	public static final InnerShadow HOVER = new InnerShadow();

	public static Square buildSquare(int x, int y) {
		Square square = new Square(x, y);
		square.applyInitEffects();
		return square;
	}

	public static RadioButton buildRadioButton(String text, ToggleGroup toggleGroup) {
		RadioButton radioButton = new RadioButton(text);
		radioButton.setToggleGroup(toggleGroup);
		radioButton.setFont(Font.font(FONT, FontWeight.BOLD, 14));
		return radioButton;
	}

	public static RadioButton buildRadioButton(String text, ToggleGroup toggleGroup, boolean selected) {
		RadioButton radioButton = new RadioButton(text);
		radioButton.setToggleGroup(toggleGroup);
		radioButton.setFont(Font.font(FONT, FontWeight.BOLD, 14));
		radioButton.setSelected(selected);
		return radioButton;
	}

	public static ImageButton buildImageButton(Piece.Color color, Piece.Type type) throws FileNotFoundException {
		ImageButton button = new ImageButton(type);
		button.setPrefSize(SQUARE_LENGTH, SQUARE_LENGTH);
		Image image = new Image(new FileInputStream(Resources.getImagePath(color, type)));
		ImageView imageView = new ImageView(image);
		imageView.setFitHeight(SQUARE_LENGTH * PIECE_SIZE);
		imageView.setFitWidth(SQUARE_LENGTH * PIECE_SIZE);
		button.setGraphic(imageView);
		return button;
	}

	public static Button buildButton(String text) {
		Button button = new Button(text);
		button.setPrefSize(getButtonWidth(button), BUTTON_HEIGHT);
		button.setFont(Font.font(FONT, FontWeight.BOLD, 14));
		return button;
	}

	public static Label buildLabel(String text) {
		Label label = new Label(text);
		label.setFont(Font.font(FONT, FontWeight.BOLD, 14));
		return label;
	}

	private static int getButtonWidth(Button button) {
		return (int) (button.getText().length() * 8);
	}
}
