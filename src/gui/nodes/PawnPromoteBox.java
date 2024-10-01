package gui.nodes;

import java.io.FileNotFoundException;

import gui.Style;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import pieces.Piece.Color;
import pieces.Piece.Type;

/**
 * 
 * @author John Hoffmann
 * 
 *         PawnPromoteBox handles the box that displays when the player gets to
 *         choose what piece their pawn promotes to
 *
 */
public class PawnPromoteBox extends HBox {

	private ImageButton[] buttons;

	public PawnPromoteBox(Color playerTurn) {
		buttons = new ImageButton[4];
		try {
			buttons[0] = (ImageButton) Style.buildImageButton(playerTurn, Type.QUEEN);
			buttons[1] = (ImageButton) Style.buildImageButton(playerTurn, Type.ROOK);
			buttons[2] = (ImageButton) Style.buildImageButton(playerTurn, Type.BISHOP);
			buttons[3] = (ImageButton) Style.buildImageButton(playerTurn, Type.KNIGHT);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		setAlignment(Pos.TOP_CENTER);
		getChildren().addAll(buttons);
	}

	public ImageButton[] getButtons() {
		return buttons;
	}

}
