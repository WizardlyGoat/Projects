package gui;

import pieces.Piece;

public interface Resources {

	public static final String FILE = "resources\\images\\pieces\\";
	public static final String PNG = ".png";

	public static String getImagePath(Piece.Color color, Piece.Type type) {
		return FILE + color + type + PNG;
	}

}
