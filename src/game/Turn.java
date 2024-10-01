package game;


import java.util.ArrayList;

import game.helpers.FXCommander;
import game.helpers.Finder;
import gui.ChessDriver;
import pieces.Piece;
import pieces.Piece.Color;
import pieces.Piece.Type;

/**
 * 
 * @author John Hoffmann
 * 
 *         Turn gets a piece and destination from the player
 *
 */
public class Turn {

	private Move move;

	/**
	 * @param pieces      the ArrayList<Piece> of all pieces
	 * @param playerColor the player's color
	 * @param gui
	 */
	public Turn(ArrayList<Piece> pieces, Color playerColor, ChessDriver gui) {
		FXCommander.setPerspective(playerColor, gui);
		Piece currPiece = null;
		Square destination = null;

		do {
			FXCommander.clearLegalMoveEffects(gui);
			// add legal move effects if there is a piece selected
			if (currPiece != null) {
				FXCommander.addLegalMoveEffects(getAllLegalMoves(pieces, currPiece), getAllLegalTakes(pieces, currPiece),
						gui);
			}

			// receive a new square as input
			Square currSquare = getValidSquare(gui);
			Piece pieceOnSquare = Finder.getPieceOnSquare(currSquare, pieces);

			// deselect the piece if it was already selected
			if (currPiece != null && currPiece.getPosition().equals(currSquare)) {
				currPiece = null;
			}
			// get a friendly piece if chosen
			else if (pieceOnSquare != null && pieceOnSquare.getColor() == playerColor) {
				currPiece = pieceOnSquare;
			}
			// get destination once a piece is chosen and can make it to selected square
			else if (currPiece != null && currPiece.hasLegalMove(currSquare, pieces)) {
				destination = currSquare;
			}
			// deselect the piece
			else {
				currPiece = null;
			}
			// continue once the piece and destination are selected
		} while (destination == null);
		move = new Move(currPiece, destination, pieces);
	}

	/**
	 * gets a Square from the user
	 * 
	 * @return the square that the user clicks
	 */
	private Square getValidSquare(ChessDriver gui) {
		Square square = new Square();
		do {
			try {
				synchronized (gui.getCommunication()) {
					gui.getCommunication().wait();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			square = gui.getCommunication().getClickedSquare();
		} while (square.getX() < 1 || square.getX() > 8 || square.getY() < 1 || square.getY() > 8);
		return square;
	}

	/**
	 * gets all of the positions the piece can capture on
	 * 
	 * @param pieces the ArrayList<Piece> of all pieces
	 * @return an ArrayList<Piece> of all square the piece can take to
	 */
	private ArrayList<Square> getAllLegalTakes(ArrayList<Piece> pieces, Piece currPiece) {
		boolean hasLegalMove;
		Square currSquare = new Square();
		ArrayList<Square> allLegalTakes = new ArrayList<Square>();
		// cycle through all rows
		for (int r = 1; r < 9; r++) {
			// cycle through all columns
			for (int c = 1; c < 9; c++) {
				currSquare.setPosition(c, r);
				hasLegalMove = currPiece.hasLegalMove(currSquare, pieces);
				// add all legal captures to the list
				if (hasLegalMove && Finder.isPieceOnSquare(currSquare, pieces)) {
					allLegalTakes.add(new Square(c, r));
				}
				// in enPassant, pawns don't take on an occupied square, however, pawns can only
				// capture diagonally (the x coordinate will be different)
				else if (hasLegalMove && currPiece.getType() == Type.PAWN
						&& currPiece.getPosition().getX() != currSquare.getX()) {
					allLegalTakes.add(new Square(c, r));
				}
			}
		}
		return allLegalTakes;
	}

	/**
	 * gets all of the positions the piece can move (excluding captures)
	 * 
	 * the array will also include the piece's position since this is for the GUI
	 * 
	 * @param pieces the ArrayList<Piece> of all pieces
	 * @return an ArrayList<Piece> of all positions the piece can move to without capturing
	 */
	private ArrayList<Square> getAllLegalMoves(ArrayList<Piece> pieces, Piece currPiece) {
		Square currSquare = new Square();
		ArrayList<Square> allLegalMoves = new ArrayList<Square>();
		// add the current piece's position first
		allLegalMoves.add(currPiece.getPosition());
		// cycle through all rows
		for (int r = 1; r < 9; r++) {
			// cycle through all columns
			for (int c = 1; c < 9; c++) {
				currSquare.setPosition(c, r);
				// add all legal moves to the list (not including capture moves)
				if (currPiece.hasLegalMove(currSquare, pieces) && !Finder.isPieceOnSquare(currSquare, pieces)) {
					allLegalMoves.add(new Square(c, r));
				}
			}
		}
		return allLegalMoves;
	}

	// getters and setters
	public Move getMove() {
		return move;
	}

}
