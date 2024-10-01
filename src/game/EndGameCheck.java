package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import game.helpers.Copier;
import game.helpers.Finder;
import pieces.Piece;
import pieces.Piece.Color;
import pieces.Piece.Type;

/**
 * 
 * @author John Hoffmann
 * 
 *         EndGameCheck determines if the game is over, and if so, how it is
 *         over
 *
 */
public class EndGameCheck {

	// enum for how a game can end
	public static enum Ending {
		NOT_OVER("0-0", false), CHECKMATE("Check mate!", false), STALEMATE("Draw by Stalemate", true),
		THREE_FOLD_REPETITION("Draw by three-fold repetition", true),
		INSUFFICIENT_MATERIAL("Draw by insufficient material", true), FIFTY_MOVE_RULE("Draw by 50-move Rule", true);

		public String message;
		public boolean isDraw; // this ending is a type of draw

		Ending(String message, boolean isDraw) {
			this.message = message;
			this.isDraw = isDraw;
		}
	}

	private Ending ending;

	/**
	 * @param pieces     the ArrayList<Piece> of pieces
	 * @param history    the LinkedList of past moves
	 * @param playerTurn the color of whose turn it is about to be (if the game
	 *                   isn't over)
	 */
	public EndGameCheck(ArrayList<Piece> pieces, LinkedList<Move> history, Color playerTurn) {
		boolean hasLegalMove = false;
		Square currSquare = new Square(); // current square
		// check all of player's pieces for a legal move
		for (Piece p : pieces) {
			if (p.getColor() != playerTurn) {
				continue; // skip opponent's pieces
			}
			// check each column
			for (int c = 1; c < 9; c++) {
				// check each row
				for (int r = 1; r < 9; r++) {
					currSquare.setPosition(c, r);
					if (p.hasLegalMove(currSquare, pieces)) {
						hasLegalMove = true;
					}
				}
			}
		}

		// see if this is the third time this position has occurred
		if (hasLegalMove && amountOfRepetitions(pieces, history) >= 3) {
			// technically, at this point a player may CLAIM a draw, but it is not forced
			ending = Ending.THREE_FOLD_REPETITION;
		}
		// no captures or pawn moves in the last 100 positions (50 moves)
		else if (hasLegalMove && turnsSinceLastPawnOrCapture(history) >= 100) {
			// technically, at this point a player may CLAIM a draw, but it is not forced
			ending = Ending.FIFTY_MOVE_RULE;
		}
		// not enough material to deliver checkmate
		else if (hasLegalMove && !sufficientMaterial(pieces)) {
			ending = Ending.INSUFFICIENT_MATERIAL;
		}
		// no legal moves and not in check
		else if (!hasLegalMove && !inCheck(pieces, playerTurn)) {
			ending = Ending.STALEMATE;
		}
		// no legal moves and in check
		else if (!hasLegalMove) {
			ending = Ending.CHECKMATE;
		} else {
			ending = Ending.NOT_OVER;
		}
	}

	/**
	 * determines if the given player's king is in danger
	 * 
	 * @param pieces     the ArrayList<Piece> of all the pieces
	 * @param playerTurn the color of the current player
	 * @return true if the player's king is in check, false otherwise
	 */
	public static boolean inCheck(ArrayList<Piece> pieces, Color playerTurn) {
		Square kingSquare = null;
		for (Piece p : pieces) {
			if (p.getType() == Type.KING && p.getColor() == playerTurn) {
				kingSquare = p.getPosition();
				break;
			}
		}
		// check other player's ability to target the kingSquare
		for (Piece p : pieces) {
			if (p.getColor() != playerTurn && p.canSee(kingSquare, pieces)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * gets the amount of moves that have happened since the last pawn move or piece
	 * capture
	 * 
	 * @param history the LinkedList of past moves
	 * @return the amount of turns
	 */
	private int turnsSinceLastPawnOrCapture(LinkedList<Move> history) {
		Iterator<Move> iterator = history.descendingIterator();
		int turns = 0;
		Move move;
		while (iterator.hasNext()) {
			move = iterator.next();
			if (move.isPawnOrCapture()) {
				break;
			}
			turns++;
		}
		return turns;
	}

	/**
	 * returns the amount of times this game has had this exact postion
	 * 
	 * @param pieces  the ArrayList<Piece> of all pieces (the current position)
	 * @param history the LinkedList of past Moves
	 * @return the amount of exact repetitions
	 */
	private int amountOfRepetitions(ArrayList<Piece> pieces, LinkedList<Move> history) {
		Iterator<Move> iterator = history.descendingIterator();
		ArrayList<Piece> pastPieces = pieces;
		int repetitions = 1; // this position is identical to itself
		Move move;

		while (iterator.hasNext()) {
			move = iterator.next();
			// pawn moves and captures ensure a different position
			if (move.isPawnOrCapture()) {
				break;
			}
			pastPieces = undoMove(pastPieces, move);
			if (identicalBoard(pieces, pastPieces)) {
				repetitions++;
			}
		}
		return repetitions;
	}

	/**
	 * undoes the given move
	 * 
	 * @param pieces the ArrayList<Piece> of all pieces
	 * @param move   the move that is being undone
	 * @return pastPieces, the ArrayList<Piece> of all pieces before the move
	 */
	private ArrayList<Piece> undoMove(ArrayList<Piece> pieces, Move move) {
		ArrayList<Piece> pastPieces = Copier.copyBoard(pieces);

		// move the moved piece back to where it came from
		Piece movedPiece = Finder.getPieceOnSquare(move.getDestination(), pastPieces);
		movedPiece.setPosition(move.getPiece().getPosition());

		// add the captured piece
		if (move.getCapturePiece() != null) {
			pastPieces.add(move.getCapturePiece());
		}

		// undo castling as well
		if (move.getRookJump() != null) {
			// no rookJump will have a rook jump in it, so this is only recursive once
			pastPieces = undoMove(pastPieces, move.getRookJump());
		}

		return pastPieces;
	}

	/**
	 * determines if two boards are identical or not
	 * 
	 * @param pieces1 the first board being compared
	 * @param pieces2 the second board being compared
	 * @return true if they are the same position, false otherwise
	 */
	private boolean identicalBoard(ArrayList<Piece> pieces1, ArrayList<Piece> pieces2) {
		// false if different number of pieces
		if (pieces1.size() != pieces2.size()) {
			return false;
		}
		// sort all pieces
		Collections.sort(pieces1);
		Collections.sort(pieces2);

		for (int i = 0; i < pieces1.size(); i++) {
			// compare all pieces
			if (!pieces1.get(i).equals(pieces2.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * determines if there are enough pieces to deliver checkmate in the future
	 * 
	 * @param pieces the ArrayList<Piece> of pieces
	 * @return true, if there is sufficient material. false otherwise
	 */
	private boolean sufficientMaterial(ArrayList<Piece> pieces) {
		int blackBishops = 0;
		int whiteBishops = 0;
		boolean blackKnights = false;
		boolean whiteKnights = false;
		Piece.Type type;
		Piece.Color color;

		for (Piece p : pieces) {
			type = p.getType();

			// if there's a queen, rook, or pawn, then there's enough material
			if (type == Piece.Type.QUEEN || type == Piece.Type.ROOK || type == Piece.Type.PAWN) {
				return true;
			}

			color = p.getColor();
			// white bishops
			if (type == Piece.Type.BISHOP && color == Piece.Color.WHITE) {
				whiteBishops++;
			}
			// black bishops
			if (type == Piece.Type.BISHOP && color == Piece.Color.BLACK) {
				blackBishops++;
			}
			// white knights
			if (type == Piece.Type.KNIGHT && color == Piece.Color.WHITE) {
				whiteKnights = true;
			}
			// black knights
			if (type == Piece.Type.KNIGHT && color == Piece.Color.BLACK) {
				blackKnights = true;
			}
		}
		// 1+ bishop & 1+ knight, OR 2 bishops
		if ((whiteBishops >= 1 && whiteKnights) || (whiteBishops == 2) || (blackBishops >= 1 && blackKnights)
				|| (blackBishops == 2)) {
			return true;
		}

		// for this program, 2 knights will count as NOT ENOUGH material
		return false;
	}

	// getters and setters
	public Ending getEnding() {
		return ending;
	}
}
