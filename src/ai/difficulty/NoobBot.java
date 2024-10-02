package ai.difficulty;

import java.util.ArrayList;

import ai.AIMove;
import ai.Logger;
import ai.analyzer.BoardAnalyzer;
import ai.analyzer.BoardAnalyzer.GameState;
import ai.analyzer.Exchange;
import game.Square;
import game.helpers.ConsoleColors;
import game.EndGameCheck.Ending;
import pieces.Pawn;
import pieces.Piece;

public class NoobBot extends Difficulty {

	// offset by 0.50 points
	@Override
	public double getOffSet() {
		return super.getOffSet(0.50);
	}

	@Override
	public double getPieceHesitation(BoardAnalyzer boardAnalyzer) {
		double score = 0.0;

		AIMove move = boardAnalyzer.getMove();
		Piece piece = move.getPiece();
		Logger logger = boardAnalyzer.getHesitationLogger();

		switch (piece.getType()) {
		case KING:
			// king hesitates to castle
			if (move.getRookJump() != null) {
				score += logger.addClarification(-0.25, "King doesn't know how to castle", Logger.Difference.ANY);
			}
			break;

		case PAWN:
			// pawn on edges try to get rook free
			int x = piece.getPosition().getX();
			int y = piece.getPosition().getY();
			int destY = move.getDestination().getY();
			if (Math.abs(y - destY) == 2 && (x == 1 || x == 8)) {
				score += logger.addClarification(0.25, "Pawn makes way for rook", Logger.Difference.ANY);
			}
			// pawn is passed pawn
			if (((Pawn) piece).isPassedPawn(boardAnalyzer.getFuturePieces())) {
				score += logger.addClarification(0.10, "Pawn is passed pawn", Logger.Difference.ANY);
			}
			break;
		default:
		}

		return score;
	}

	@Override
	public double getCapturePoints(BoardAnalyzer boardAnalyzer) {
		// Logger logger = boardAnalyzer.getCaptureLogger();
		Piece capturePiece = boardAnalyzer.getMove().getCapturePiece();
		double score = 0.0;

		// material value
		if (capturePiece != null) {
			score += capturePiece.getType().worth; // no need to clarify material value
		}

		return score;
	}

	@Override
	public double getPieceControl(Piece piece, ArrayList<Piece> pieces, BoardAnalyzer boardAnalyzer) {
		double score = 0.0;
		double offensiveValue;

		// Queen, Rook, and Pawn get greater desire for control
		double multiplier = 1.0;
		Piece.Type type = piece.getType();
		switch (type) {
		case QUEEN:
			multiplier = 2;
			break;

		case ROOK:
			multiplier = 2.7;
			break;

		case PAWN:
			multiplier = 1.3;
		default:
		}

		ArrayList<Square> controlledSquares = SquareValue.getControlledSquares(piece, pieces);

		// adding worth for controlling offensive squares
		for (Square s : controlledSquares) {
			offensiveValue = SquareValue.getOffensiveValue(s, piece.getColor()); // 0 - 7
			offensiveValue /= 100.0; // 0.00 - 0.07
			score += (offensiveValue * multiplier);
		}

		// adding worth for check / blocking king's options during end game
		if (boardAnalyzer.getGameState() == GameState.LATEGAME) {
			double checkValue;

			for (Square s : controlledSquares) {
				checkValue = SquareValue.getCheckValue(s, boardAnalyzer.getFriendlyColor(), pieces); // 0 - 2
				checkValue /= 50.0; // 0.00, 0.02, 0.04
				score += checkValue;
			}
		}

		return score;
	}

	@Override
	public double getDangerAwareness(BoardAnalyzer boardAnalyzer, Exchange exchange) {
		Logger logger = boardAnalyzer.getDangerLogger();
		int awareness = random.nextInt(99); // 0 - 99
		boolean realDanger = exchange.getLoss() < 0.0;

		logger.print("  ");

		// exchange involves the piece ai just moved
		if (exchange.contains(boardAnalyzer.getPieceAfterMoving())) {
			awareness += random.nextInt(40) + 20; // increase awareness by 20% to 40%
			logger.print(ConsoleColors.GREEN + "++" + ConsoleColors.RESET);
		} else {
			logger.print("  ");
		}

		// NOT REAL DANGER
		// 20% chance of thinking this piece is in danger
		if (!realDanger && awareness < 20) {
			logger.print(ConsoleColors.RED + "> HYPER AWARE" + ConsoleColors.RESET);
			return -exchange.getLoss();
		}
		// 20% chance of being half aware of this piece's real protection
		if (!realDanger && awareness < 40) {
			logger.print(ConsoleColors.YELLOW + "> TOO AWARE" + ConsoleColors.RESET);
			return -exchange.getLoss() / 2;
		}

		// REAL DANGER
		// 40% chance of completely missing this
		if (realDanger && awareness < 40) {
			logger.print(ConsoleColors.RED + "> NOT AWARE" + ConsoleColors.RESET);
			return 0.0;
		}
		// 30% chance of being half aware of this
		if (realDanger && awareness < 70) {
			logger.print(ConsoleColors.YELLOW + "> HALF AWARE" + ConsoleColors.RESET);
			return exchange.getLoss() / 2;
		}

		logger.print(ConsoleColors.GREEN + "> FULLY AWARE" + ConsoleColors.RESET);
		return exchange.getLoss();
	}

	@Override
	public double getEndGameDesire(BoardAnalyzer boardAnalyzer) {
		Logger logger = boardAnalyzer.getEndGameLogger();
		double score = 0.0;

		// can't tell if it is checkmate
		if (boardAnalyzer.getEnding() == Ending.CHECKMATE) {
			score += logger.addClarification(-.25, "This move delivers checkmate", Logger.Difference.ANY);
		}

		return score;
	}

}
