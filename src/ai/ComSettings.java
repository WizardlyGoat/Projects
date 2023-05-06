package ai;

public class ComSettings
{

	private int difficulty; // the difficulty level
	// amount of points: mid (100 -> 2 points) extreme (900 -> 18 points)
	private int offSet; // random amount of points (times 0.01) given to each possible move

	// amount of points: no hesitation (0.0) eagerness (positive) hesitation (negative)
	// how much each piece hesitates to move
	private double pawnHesitation;
	private double knightHesitation;
	private double bishopHesitation;
	private double rookHesitation;
	private double queenHesitation;
	private double kingHesitation;
	private int queenHesitateTillTurn; // the turn that the queen stops hesitating
	private double queenPerTurnHesitation; // the amount the queen hesitates until queenHesitateTillTurn
	private double desireToCastle; // the king's desire to castle (positive) or to never castle (negative)

	// multiplier: 1.0 gives full weight to each piece
	private double desireToCapture; // how much the computer cares about capturing pieces
	private double desireToTakePawn;
	private double desireToTakeKnight;
	private double desireToTakeBishop;
	private double desireToTakeRook;
	private double desireToTakeQueen;

	// multiplier: none (0.0) to full (1.0)
	private double pieceBettermentCare; // how much the computer cares about improving it's pieces
	// amount of points: none (0.0) extreme (0.1) since it is multiplied by (0 - 6) per square
	private double centerMagnet; // the tendency for all pieces to want to see squares towards the center of the board
	// amount of points a pawn is additionally worth on back rank (or pass pawns divided by squares left)
	private double pawnPromoteDesire;

	// multiplier: 1.0 gives full weight to each piece
	private double dangerAwareness; // how much the computer can tell it's pieces are in danger of being captured
	private double pawnDangerAwareness;
	private double knightDangerAwareness;
	private double bishopDangerAwareness;
	private double rookDangerAwareness;
	private double queenDangerAwareness;

	// amount of points: none (0.0) extreme (40.0)
	private double checkMateAwareness; // points given to moves that deliver check mate
	// amount of points: none (0.0) extreme (10.0)
	private double drawAwareness; // points subtracted (if winning) or points added (if losing) when a move gives a draw

	/**************************************************************************************
	 * ComSettings contains all of the key differences between each computer difficulty
	 * 
	 * tweak how the computer plays by changing values in each difficulty
	 * 
	 * @param difficulty
	 *           the difficulty level of the computer
	 * @return nothing
	 */
	public ComSettings(int difficulty)
	{
		this.difficulty = difficulty;
		switch ( difficulty )
		{
			// easy
			case 2:
				setEasy ( );
				break;
			// for random moves
			default:
				setRandom ( );
		}
	} // end ComSettings constructor

	// methods for each difficulty
	public void setEasy( )
	{
		offSet = 10;
		// hesitation
		pawnHesitation = -0.10;
		knightHesitation = 0.0;
		bishopHesitation = 0.0;
		rookHesitation = 0.0;
		queenHesitation = 0.0;
		kingHesitation = -0.20;
		queenHesitateTillTurn = 7;
		queenPerTurnHesitation = 0.5;
		desireToCastle = 1.0;
		// desire to capture
		desireToCapture = 1.0;
		desireToTakePawn = 1.0;
		desireToTakeKnight = 1.0;
		desireToTakeBishop = 1.0;
		desireToTakeRook = 1.0;
		desireToTakeQueen = 1.0;
		// piece betterment
		pieceBettermentCare = 1.0;
		centerMagnet = 0.02;
		pawnPromoteDesire = 8.0;
		// danger awareness
		dangerAwareness = 1.0;
		pawnDangerAwareness = 1.0;
		knightDangerAwareness = 1.0;
		bishopDangerAwareness = 1.0;
		rookDangerAwareness = 1.0;
		queenDangerAwareness = 1.0;
		// end game
		checkMateAwareness = 5.0;
		drawAwareness = 2.0;
	}

	public void setRandom( )
	{
		offSet = 900;
		// hesitation
		pawnHesitation = 0.0;
		knightHesitation = 0.0;
		bishopHesitation = 0.0;
		rookHesitation = 0.0;
		queenHesitation = 0.0;
		kingHesitation = 0.0;
		queenHesitateTillTurn = 0;
		queenPerTurnHesitation = 0.0;
		desireToCastle = 0.0;
		// desire to capture
		desireToCapture = 0.0;
		desireToTakePawn = 0.0;
		desireToTakeKnight = 0.0;
		desireToTakeBishop = 0.0;
		desireToTakeRook = 0.0;
		desireToTakeQueen = 0.0;
		// piece betterment
		pieceBettermentCare = 0.0;
		centerMagnet = 0.0;
		pawnPromoteDesire = 0.0;
		// danger awareness
		dangerAwareness = 0.0;
		pawnDangerAwareness = 0.0;
		knightDangerAwareness = 0.0;
		bishopDangerAwareness = 0.0;
		rookDangerAwareness = 0.0;
		queenDangerAwareness = 0.0;
		// end game
		checkMateAwareness = 0.0;
		drawAwareness = 0.0;
	}

	// getters
	public double getCheckMateAwareness( )
	{
		return checkMateAwareness;
	}

	public int getOffSet( )
	{
		return offSet;
	}

	public int getDifficulty( )
	{
		return difficulty;
	}

	public double getPawnHesitation( )
	{
		return pawnHesitation;
	}

	public double getKnightHesitation( )
	{
		return knightHesitation;
	}

	public double getBishopHesitation( )
	{
		return bishopHesitation;
	}

	public double getRookHesitation( )
	{
		return rookHesitation;
	}

	public double getQueenHesitation( )
	{
		return queenHesitation;
	}

	public int getQueenHesitateTillTurn( )
	{
		return queenHesitateTillTurn;
	}

	public double getQueenPerTurnHesitation( )
	{
		return queenPerTurnHesitation;
	}

	public double getKingHesitation( )
	{
		return kingHesitation;
	}

	public double getDesireToCastle( )
	{
		return desireToCastle;
	}

	public double getCenterMagnet( )
	{
		return centerMagnet;
	}

	public double getPieceBettermentCare( )
	{
		return pieceBettermentCare;
	}

	public double getPawnPromoteDesire( )
	{
		return pawnPromoteDesire;
	}

	public double getDangerAwareness( )
	{
		return dangerAwareness;
	}

	public double getPawnDangerAwareness( )
	{
		return pawnDangerAwareness;
	}

	public double getKnightDangerAwareness( )
	{
		return knightDangerAwareness;
	}

	public double getBishopDangerAwareness( )
	{
		return bishopDangerAwareness;
	}

	public double getRookDangerAwareness( )
	{
		return rookDangerAwareness;
	}

	public double getQueenDangerAwareness( )
	{
		return queenDangerAwareness;
	}

	public double getDrawAwareness( )
	{
		return drawAwareness;
	}

	public double getDesireToCapture( )
	{
		return desireToCapture;
	}

	public double getDesireToTakePawn( )
	{
		return desireToTakePawn;
	}

	public double getDesireToTakeKnight( )
	{
		return desireToTakeKnight;
	}

	public double getDesireToTakeBishop( )
	{
		return desireToTakeBishop;
	}

	public double getDesireToTakeRook( )
	{
		return desireToTakeRook;
	}

	public double getDesireToTakeQueen( )
	{
		return desireToTakeQueen;
	}
}
