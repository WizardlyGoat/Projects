package gui.nodes;

import javafx.scene.control.Button;
import pieces.Piece;

/**
 * 
 * @author John Hoffmann
 * 
 *         ImageButton is a special Button that keeps track of the piece type of
 *         the image it holds
 */
public class ImageButton extends Button {

	private Piece.Type type;

	public ImageButton(Piece.Type type) {
		this.type = type;
	}

	public Piece.Type getType() {
		return type;
	}

}
