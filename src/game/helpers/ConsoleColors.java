package game.helpers;

import pieces.Piece;

/**
 * 
 * @author LesbianGoat
 * 
 *         ConsoleColors deals with anything concerning coloring the console
 *         output
 *
 */
public interface ConsoleColors {

	// colors for console
	public static final String RESET = "\u001B[0m";
	public static final String CYAN = "\u001B[36m";
	public static final String MAGENTA = "\u001B[35m";
	public static final String YELLOW = "\u001B[33m";
	public static final String RED = "\u001B[31m";
	public static final String BLUE = "\u001B[34m";
	public static final String GREEN = "\u001B[32m";

	/**
	 * returns the piece's type with it's appropriate color within paranthesis
	 * 
	 * @param piece the piece being printed
	 * @return _(TYPE) -> but in magenta (black) or cyan (white)
	 */
	public static String toColorPiece(Piece piece) {
		if (piece.getColor() == Piece.Color.WHITE) {
			return " (" + Piece.CYAN + piece.getType() + Piece.RESET + ")";
		}
		return " (" + Piece.MAGENTA + piece.getType() + Piece.RESET + ")";
	}

}
