package gui.nodes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import gui.Board;
import gui.Style;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * 
 * @author John Hoffmann
 * 
 *         Square is a StackPane that has an ImageView (for a piece on the
 *         square), Rectangle (the actual square), and it's coordinates
 *
 */
public class Square extends StackPane implements Comparable<game.Square> {

	private ImageView imageView; // the piece's image (child)
	private Rectangle rec; // the shape node (child)
	private int c; // column
	private int r; // row

	/**
	 * @param x the column
	 * @param y the row
	 */
	public Square(int x, int y) {
		imageView = new ImageView();
		imageView.setFitHeight(Style.SQUARE_LENGTH * Style.PIECE_SIZE);
		imageView.setFitWidth(Style.SQUARE_LENGTH * Style.PIECE_SIZE);
		setAlignment(imageView, Pos.CENTER);

		rec = new Rectangle(Style.SQUARE_LENGTH, Style.SQUARE_LENGTH);
		this.c = x;
		this.r = y;
		getChildren().addAll(rec, imageView);
	}

	/**
	 * applies the hover effect to the square
	 */
	public void applyHover() {
		rec.setEffect(Style.HOVER);
	}

	/**
	 * removes the hover effect from the square
	 * 
	 * @param board the board this square belongs to
	 */
	public void removeHover(Board board) {
		if (board.getPossibleTakes().contains(this)) {
			applyLegalTake();
		} else if (board.getPossibleMoves().contains(this)) {
			applyLegalMove();
		} else if (board.getPastMoves().contains(this)) {
			applyPastMove();
		} else {
			applyInitEffects();
		}
	}

	/**
	 * applies the past move effects to the square
	 */
	public void applyPastMove() {
		rec.setFill(Style.PAST_MOVE);
		rec.setEffect(Style.BRIGHT_GLOW);
	}

	/**
	 * applies the legal move effects to the square
	 */
	public void applyLegalMove() {
		rec.setFill(Style.LEGAL_MOVE);
		rec.setEffect(Style.DULL_GLOW);
	}

	/**
	 * applies the legal take effects to the square
	 */
	public void applyLegalTake() {
		rec.setFill(Style.LEGAL_TAKE);
		rec.setEffect(Style.MEDIUM_GLOW);
	}

	/**
	 * applies the normal effects to the square
	 */
	public void applyInitEffects() {
		rec.setFill(getSquareColor());
		rec.setEffect(null);
	}

	/**
	 * removes the image from the square
	 */
	public void clearImage() {
		imageView.setImage(null);
	}

	/**
	 * returns the color LIGHT or DARK depending on the square
	 * 
	 * @return LIGHT if the square is light, DARK otherwise
	 */
	public Color getSquareColor() {
		if ((c + r) % 2 == 0) {
			return Style.DARK;
		} else {
			return Style.LIGHT;
		}
	}

	/**
	 * getValue returns a unique value for the square (used in compareTo and equals)
	 * 
	 * @return (x * 10) + y
	 */
	public int getValue() {
		return (c * 10) + r;
	}

	// getters and setters
	public void setImageView(ImageView imageView) {
		this.imageView.setImage(imageView.getImage());
	}

	public void setImageView(String imagePath) throws FileNotFoundException {
		Image image = new Image(new FileInputStream(imagePath));
		imageView.setImage(image);
	}

	public ImageView getImageView() {
		return imageView;
	}

	public Rectangle getRec() {
		return rec;
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
	public boolean equals(Object o) {
		if (o instanceof Square) {
			Square s = (Square) o;
			if (getValue() == s.getValue()) {
				return true;
			}
		} else if (o instanceof game.Square) {
			game.Square s = (game.Square) o;
			if (getValue() == s.getValue()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(game.Square square) {
		return getValue() - square.getValue();
	}

	@Override
	public String toString() {
		return "GUI Square " + c + "" + r;
	}
}
