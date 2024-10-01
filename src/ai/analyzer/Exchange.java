package ai.analyzer;

import java.util.ArrayList;
import java.util.Iterator;
import ai.Logger;
import game.Move;
import game.Square;
import game.helpers.ConsoleColors;
import game.helpers.Copier;
import game.helpers.MoveExecuter;
import pieces.Piece;
import pieces.Piece.Color;

/**
 * 
 * @author LesbianGoat
 * 
 *         Exchange is responsible for sensing what pieces can capture given
 *         piece and how many material points would be lost
 *
 */
public class Exchange {

	private ArrayList<Piece> exchange; // pieces that could die
	private double loss; // how bad is the danger (0.0 or less)
	private Piece lastPiece; // last piece in exchange (one that doesn't die)

	private Piece.Color friend;
	private Logger logger;

	/**
	 * @param pieces the ArrayList of pieces
	 * @param piece  the piece possibly in danger
	 * @param logger the danger logger
	 */
	public Exchange(ArrayList<Piece> pieces, Piece piece, Logger logger) {
		exchange = new ArrayList<Piece>();
		friend = piece.getColor();
		this.logger = logger;

		exchange.add(Copier.copyPiece(piece));
		testExchange(Copier.copyBoard(pieces), piece.getPosition(), Color.getOppColor(friend));
		lastPiece = exchange.remove(exchange.size() - 1);

		if (!exchange.isEmpty()) {
			loss = getBestStop();
			logger.print(ConsoleColors.toColorPiece(lastPiece));
		}
	}

	/**
	 * recursively finds pieces that can take on given square and adds them all to
	 * the exchange. Pieces are arranged by alternating color, lowest worth to
	 * highest worth
	 * 
	 * @param pieces the ArrayList<Piece> of pieces
	 * @param square the square this exchange is happening on
	 * @param threat the color of threatening pieces
	 */
	private void testExchange(ArrayList<Piece> pieces, Square square, Color threat) {
		ArrayList<Piece> threats = getThreats(pieces, square, threat);

		if (threats.isEmpty()) {
			return;
		}

		Move move = new Move(threats.get(0), square, pieces);
		exchange.add(Copier.copyPiece(move.getPiece()));

		MoveExecuter.testMove(move, pieces);

		testExchange(pieces, square, Color.getOppColor(threat));
	}

	/**
	 * returns a sorted (by ascending worth) ArrayList<Piece> of pieces of the given
	 * color that can support given square
	 * 
	 * @param pieces  the ArrayList<Piece> of pieces
	 * @param square  the square pieces are trying to support
	 * @param threats the color of threatening pieces
	 * @return an ArrayList<Piece> of threatening pieces
	 */
	private ArrayList<Piece> getThreats(ArrayList<Piece> pieces, Square square, Color threat) {
		ArrayList<Piece> threats = new ArrayList<Piece>();
		for (Piece p : pieces) {
			if (p.getColor() == threat && p.canSupport(square, pieces)) {
				threats.add(p);
			}
		}
		sortPiecesByWorth(threats);
		return threats;
	}

	/**
	 * sorts an ArrayList<Piece> of pieces into ascending order based on worth
	 * 
	 * @param pieces the ArrayList<Piece> of pieces
	 */
	private void sortPiecesByWorth(ArrayList<Piece> pieces) {
		Piece tempPiece;

		for (int i = 0; i + 1 < pieces.size(); i++) {
			if (pieces.get(i).getType().worth > pieces.get(i + 1).getType().worth) {
				tempPiece = pieces.get(i);
				pieces.remove(i);
				pieces.add(tempPiece);
				i--;
			}
		}
	}

	/**
	 * getBestStop tests an exchange of pieces, looking for the best time to stop
	 * exchanging
	 * 
	 * @return the best outcome in points that the ai can acheive
	 */
	private double getBestStop() {
		Iterator<Piece> iterator = exchange.iterator();
		Piece capturePiece = iterator.next();

		loss = -capturePiece.getType().worth;
		logger.print(capturePiece + " " + Logger.roundDouble(loss));

		double bestStop = loss; // can only go up from startLoss

		while (iterator.hasNext()) {
			capturePiece = iterator.next();
			logger.print(ConsoleColors.toColorPiece(capturePiece));

			// friendly piece is being captured
			if (capturePiece.getColor() == friend) {
				loss -= capturePiece.getType().worth;
				logger.print(" " + Logger.roundDouble(loss));
			}
			// opponent piece is being captured
			else {
				loss += capturePiece.getType().worth;
				logger.print(" " + Logger.roundDouble(loss));

				// if there are more pieces, don't record best stop (the other player has a
				// chance to capture back)
				if (iterator.hasNext()) {
					continue;
				}
			}

			// record best stop
			if (bestStop < loss) {
				bestStop = loss;
				logger.print("`");
			}
		}

		return bestStop;
	}

	/**
	 * returns the material worth of the piece immediately in danger
	 * 
	 * @return the material worth of the first piece in the exchange
	 */
	public double getFirstLoss() {
		if (!exchange.isEmpty()) {
			return exchange.get(0).getType().worth;
		}
		return 0.0;
	}

	/**
	 * determines if exchange contains given kind of piece. includes lastPiece
	 * 
	 * @param type  the type of piece being looked for
	 * @param color the color piece being looked for
	 * @return true if the type of piece is a part of the exchange, false otherwise
	 */
	public boolean contains(Piece.Type type, Piece.Color color) {
		for (Piece p : exchange) {
			if (p.getType() == type && p.getColor() == color) {
				return true;
			}
		}
		if (lastPiece.getType() == type && lastPiece.getColor() == color) {
			return true;
		}
		return false;
	}

	/**
	 * determines if exchange contains given piece. includes lastPiece
	 * 
	 * @param piece the piece being examined
	 * @return true if the piece is a part of the exchange, false otherwise
	 */
	public boolean contains(Piece piece) {
		for (Piece p : exchange) {
			if (piece.equals(p)) {
				return true;
			}
		}
		if (piece.equals(lastPiece)) {
			return true;
		}
		return false;
	}

	/**
	 * determines if the exchange is empty or not
	 * 
	 * @return exchange.isEmpty()
	 */
	public boolean isEmpty() {
		return exchange.isEmpty();
	}

	// getters and setters
	public double getLoss() {
		return loss;
	}

	public ArrayList<Piece> getExchange() {
		return exchange;
	}
}
