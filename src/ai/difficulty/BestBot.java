package ai.difficulty;

import java.util.ArrayList;
import ai.AIMove;
import ai.Logger;
import ai.analyzer.BoardAnalyzer;
import ai.analyzer.BoardAnalyzer.GameState;
import ai.analyzer.Exchange;
import game.Square;
import game.EndGameCheck.Ending;
import pieces.Pawn;
import pieces.Piece;

public class BestBot extends Difficulty {

	// offset by 0.05 points
	@Override
	public double getOffSet() {
		return super.getOffSet(0.05);
	}

	@Override
	public double getPieceHesitation(BoardAnalyzer boardAnalyzer) {
		double score = 0.0;

		AIMove move = boardAnalyzer.getMove();
		Piece piece = move.getPiece();
		Logger logger = boardAnalyzer.getHesitationLogger();
		boolean canCastleStill = Inquiry.canCastleStill(boardAnalyzer.getPieces(), piece.getColor());
		boolean isCapturing = move.getCapturePiece() != null;
		GameState gameState = boardAnalyzer.getGameState();

		switch (piece.getType()) {
		case KING:
			// king hesitates to move before castling
			if (canCastleStill && move.getRookJump() == null && gameState != GameState.LATEGAME) {
				score += logger.addClarification(-3.0, "King waiting for possible castle", Logger.Difference.ANY);
			}
			// king hesitates to move off the first rank before the end game
			if (!isCapturing && !move.getDestination().isFirstRank(piece.getColor())
					&& gameState != GameState.LATEGAME) {
				score += logger.addClarification(-2.0, "King staying on first rank", Logger.Difference.ANY);
			}
			// king wants to castle
			if (move.getRookJump() != null) {
				score += logger.addClarification(1.0, "King wants to castle", Logger.Difference.ANY);
			}
			break;

		case QUEEN:
			// queen hesitates to move before minor pieces are developed (-4 to 0)
			if (!isCapturing) {
				score += logger.addClarification(-boardAnalyzer.getUnmovedMinorsPieces(),
						"Queen waiting for Minor development", Logger.Difference.MINUTE);
			}
			// queen hesitates to move during the opening
			if (gameState == GameState.OPENING) {
				score += logger.addClarification(-0.5, "Queen waiting for mid game", Logger.Difference.ANY);
			}
			break;

		case ROOK:
			// rook hesitates to move before castling
			if (canCastleStill && gameState != GameState.LATEGAME) {
				score += logger.addClarification(-3.0, "Rook waiting for possible castle", Logger.Difference.ANY);
			}
			// rook hesitates to move if it isn't to an edge rank
			if (!isCapturing && gameState != GameState.LATEGAME && !move.getDestination().isEdgeRank()) {
				score += logger.addClarification(-2.0, "Rook staying on edge ranks", Logger.Difference.ANY);
			}
			break;

		case PAWN:
			// pawn hesitates to jump 2 on edges
			int x = piece.getPosition().getX();
			int y = piece.getPosition().getY();
			int destY = move.getDestination().getY();
			if (Math.abs(y - destY) == 2 && (x == 1 || x == 8) && gameState != GameState.LATEGAME) {
				score += logger.addClarification(-0.25, "Pawn staying defensive", Logger.Difference.ANY);
			}
			// pawn is passed pawn
			if (((Pawn) piece).isPassedPawn(boardAnalyzer.getFuturePieces())) {
				score += logger.addClarification(0.50, "Pawn is passed pawn", Logger.Difference.ANY);
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
		double centerValue;
		ArrayList<Square> controlledSquares = SquareValue.getControlledSquares(piece, pieces);

		// adding worth for controlling center squares
		for (Square s : controlledSquares) {
			centerValue = SquareValue.getCenterValue(s); // 0 - 6
			centerValue /= 100.0; // 0.00 - 0.06
			score += centerValue;
		}

		// adding worth for check / blocking king's options during end game
		if (boardAnalyzer.getGameState() == GameState.LATEGAME) {
			double checkValue;

			for (Square s : controlledSquares) {
				checkValue = SquareValue.getCheckValue(s, boardAnalyzer.getFriendlyColor(), pieces); // 0 - 2
				checkValue /= 20.0; // 0.00, 0.05, 0.1
				score += checkValue;
			}
		}

		return score;
	}

	@Override
	public double getDangerAwareness(BoardAnalyzer boardAnalyzer, Exchange exchange) {
		return exchange.getLoss();
	}

	@Override
	public double getEndGameDesire(BoardAnalyzer boardAnalyzer) {
		Logger logger = boardAnalyzer.getEndGameLogger();
		double score = 0.0;

		// this move ends in checkmate
		if (boardAnalyzer.getEnding() == Ending.CHECKMATE) {
			score += logger.addClarification(20.0, "This move delivers checkmate", Logger.Difference.ANY);
		}

		// this move ends in a draw
		else if (boardAnalyzer.getEnding().isDraw) {
			// add points equal to the amount ai is losing OR
			// subtract points equal to the amount ai is winning
			score += logger.addClarification(-boardAnalyzer.getCurrentScore(), "This move forces a draw",
					Logger.Difference.ANY);
		}

		return score;
	}

}
