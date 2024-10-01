package ai.analyzer;

import java.util.ArrayList;
import java.util.LinkedList;
import ai.AIMove;
import ai.Logger;
import ai.difficulty.Difficulty;
import game.Move;
import pieces.Piece;

/**
 * 
 * @author John Hoffmann
 * 
 *         MoveAnalyzer gives a score to the AIMoves it is given
 * 
 *         1: offSet all scores
 * 
 *         2: award or punish hesitation
 * 
 *         3: award captures
 *
 *         4: award piece control
 * 
 *         5: punish putting pieces in danger
 * 
 *         6: award or punish ending the game
 * 
 */
public class MoveAnalyzer {

	private Difficulty settings;

	public MoveAnalyzer(Difficulty settings) {
		this.settings = settings;
	}

	/**
	 * analyzes all given moves and assigns a score to each one
	 * 
	 * @param pieces  the ArrayList<Piece> of pieces
	 * @param moves   the ArrayList<Piece> of possible AIMoves
	 * @param history the LinkedList of past moves
	 */
	public void analyzeMoves(ArrayList<Piece> pieces, ArrayList<AIMove> moves, LinkedList<Move> history) {
		double capturePoints;

		BoardAnalyzer boardAnalysis;

		for (AIMove m : moves) {
			// get an review of the board, and also review the move
			boardAnalysis = new BoardAnalyzer(pieces, history, m, settings);

			// 1. offset all scores
			m.offSetScore(settings.getOffSet());

			// 2. award or punish hesitation
			m.labelReasoning("hesitation");
			m.addToScore(settings.getPieceHesitation(boardAnalysis), "--Total Hesitation--", Logger.Difference.MINUTE,
					boardAnalysis.getHesitationLogger());

			// 3. award captures
			m.labelReasoning("capture");
			capturePoints = settings.getCapturePoints(boardAnalysis);
			m.addToScore(capturePoints, "Capturing the " + m.getCapturePiece(), Logger.Difference.MINUTE,
					boardAnalysis.getCaptureLogger());

			// 4. award piece control
			m.labelReasoning("piece betterment");
			m.addToScore(boardAnalysis.getMoveScore() - capturePoints, "--Total piece betterment--",
					Logger.Difference.MINUTE, boardAnalysis.getPieceBetterment());

			// 5. punish putting pieces in danger
			m.labelReasoning("danger levels");
			m.addToScore(boardAnalysis.getWorstDanger(), "--Worst danger--", Logger.Difference.ANY,
					boardAnalysis.getDangerLogger());

			// 6. award or punish for ending the game
			m.labelReasoning("end game");
			m.addToScore(settings.getEndGameDesire(boardAnalysis), "--End Game--", Logger.Difference.MINUTE,
					boardAnalysis.getEndGameLogger());
		}
	}
}
