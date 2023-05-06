package test;

import java.util.ArrayList;

import ai.ComputerMove;
import pieces.Piece;

/**
 * @author John Hoffmann
 * 
 *         Test has methods that print info to the console for debugging
 */
public class Test {

	/*****************************************************************************
	 * removePieces removes all pieces
	 * 
	 * (comment out certain lines to allow those pieces to stay)
	 * 
	 * @param pieces the array of all pieces
	 * @return nothing
	 */
	public static void removePieces(Piece[] pieces) {

	} // end setStartingPosition

	/******************************************************************************
	 * printPointStandingDiff prints the difference between two point standings
	 * 
	 * @param before the point standing before a move is tested
	 * @param after  the point standing after a move is tested
	 * @return nothing
	 */
	public static void printPointStandingDiff(double before, double after) {
		System.out.println("\n\tOriginal point standing: " + Math.round(before * 100.0) / 100.0);
		System.out.println("\tPoint standing after test move: " + Math.round(after * 100.0) / 100.0);
		System.out.println("\tAward: " + Math.round((after - before) * 100.0) / 100.0);
	} // end printPointStandingDiff

	/******************************************************************************
	 * printPositionScores prints each player's total position scores and outputs
	 * the difference
	 * 
	 * @param pieces the array of all the pieces
	 * @return nothing
	 */
	public static void printPositionScores(Piece[] pieces) {
		double whiteTotal = 0.0;
		double blackTotal = 0.0;
		for (int i = 0; i < 16; i++) {
			whiteTotal += pieces[i].getPositionScore();
		}
		for (int i = 16; i < 32; i++) {
			blackTotal += pieces[i].getPositionScore();
		}
		System.out.println("\t\tWhite position score total: " + Math.round(whiteTotal * 100.0) / 100.0);
		System.out.println("\t\tBlack position score total: " + Math.round(blackTotal * 100.0) / 100.0);
		System.out.println("\t\t\tDifference: " + Math.round((whiteTotal - blackTotal) * 100.0) / 100.0);
	} // end printPositionScores

	/******************************************************************************
	 * printObservationScores prints each player's total position scores and outputs
	 * the difference
	 * 
	 * @param pieces the array of all the pieces
	 * @return nothing
	 */
	public static void printObservationScores(Piece[] pieces) {
		double whiteTotal = 0.0;
		double blackTotal = 0.0;
		for (int i = 0; i < 16; i++) {
			whiteTotal += pieces[i].getObservationScore();
		}
		for (int i = 16; i < 32; i++) {
			blackTotal += pieces[i].getObservationScore();
		}
		System.out.println("\t\tWhite observation score total: " + Math.round(whiteTotal * 100.0) / 100.0);
		System.out.println("\t\tBlack observation score total: " + Math.round(blackTotal * 100.0) / 100.0);
		System.out.println("\t\t\tDifference: " + Math.round((whiteTotal - blackTotal) * 100.0) / 100.0);
	} // end printObservationScores

	/******************************************************************************
	 * printPointWorths prints each player's total position scores and outputs the
	 * difference
	 * 
	 * @param pieces the array of all the pieces
	 * @return nothing
	 */
	public static void printPointWorths(Piece[] pieces) {
		double whiteTotal = 0.0;
		double blackTotal = 0.0;
		for (int i = 0; i < 16; i++) {
			whiteTotal += pieces[i].getPointWorth();
		}
		for (int i = 16; i < 32; i++) {
			blackTotal += pieces[i].getPointWorth();
		}
		System.out.println("\t\tWhite point worth score total: " + Math.round(whiteTotal * 100.0) / 100.0);
		System.out.println("\t\tBlack point worth score total: " + Math.round(blackTotal * 100.0) / 100.0);
		System.out.println("\t\t\tDifference: " + Math.round((whiteTotal - blackTotal) * 100.0) / 100.0);
	} // end printPointWorths

	/**********************************************************************************
	 * printMoves prints all of the moves in an array of ComputerMoves
	 * 
	 * @param moves the array of ComputerMoves
	 */
	public static void printMoves(ComputerMove[] moves) {
		for (ComputerMove c : moves) {
			System.out.println(c);
		}
	} // end printMoves

	/*************************************************************************************
	 * pritnMoves prints all of the moves in an arrayList of ComputerMoves
	 * 
	 * @param moves the arrayList of ComputerMoves
	 */
	public static void printMovesList(ArrayList<ComputerMove> moves) {
		for (ComputerMove c : moves) {
			System.out.println(c);
		}
	} // end printMovesList

	/**********************************************************************************
	 * pritnMoves prints the move it is given
	 * 
	 * @param move the ComputerMove that needs to be printed
	 */
	public static void printMove(ComputerMove move) {
		System.out.println(move);
	} // end printMove

	/**********************************************************************************
	 * printPiecesList prints all of the moves in an array of ComputerMoves
	 * 
	 * @param moves the arrayList of Piece arrays (positions)
	 */
	public static void printPiecesList(ArrayList<Piece[]> p) {
		for (int i = 0; i < p.size(); i++) {
			System.out.println("\tPrinting all of position #" + i);
			printPieces(p.get(i));
		}
	} // end PiecesList

	/**********************************************************************************
	 * printPieceList prints all of the moves in an array of ComputerMoves
	 * 
	 * @param moves the arrayList of ComputerMoves
	 */
	public static void printPieceList(ArrayList<Piece> p) {
		for (int i = 0; i < p.size(); i++) {
			printPieceInfo(p.get(i));
		}
	} // end printPieceList

	/***********************************************************************************
	 * printPieces for test, prints all pieces' information
	 * 
	 * does not print removed pieces (pieces with an x coordinate of 0)
	 * 
	 * @param pieces the array of all pieces
	 * @return nothing
	 */
	public static void printPieces(Piece[] pieces) {
		System.out.println("Printing all pieces...");
		for (Piece p : pieces) {
			if (p.getPosition().getX() != 0) {
				System.out.println(p);
			}
		}
	} // end printPieces

	/***********************************************************************************
	 * printPieceInfo prints all piece info
	 * 
	 * @param piece the Piece being examined
	 * @return nothing
	 */
	public static void printPieceInfo(Piece piece) {
		System.out.println(piece);
	} // end printPieceInfo
}
