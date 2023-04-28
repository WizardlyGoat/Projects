import java.util.ArrayList;
import java.util.Random;

/**
 * @author John Hoffmann
 * 
 *         Computer contains all of the methods for playing against the computer
 * 
 *         TODO: computer can't tell draws are happening because past positions is changed
 *
 */
public class Computer
{
	private static Random random = new Random ( );
	private Piece.Color color; // color the computer is player as (null if not playing)
	private Piece.Color oppColor; // color the opponent is playing as
	private ComSettings difficulty; // computer difficulty (null if computer is not playing)
	private ComputerMove highestScoreMove; // the move that the computer decides upon
	private ComputerMove highestScoreMove2; // the 2nd place highest scoring move
	private ComputerMove highestScoreMove3; // the 3rd place highest scoring move
	// indexes
	private int firstPiece; // index of computer's first piece
	private int lastPiece; // index of computer's last piece
	private int oppFirstPiece; // index of the opponent's first piece
	private int oppLastPiece; // index of the opponent's last piece

	// default constructor for parent computer only
	public Computer()
	{
		color = null;
		difficulty = null;
		highestScoreMove = null;
		highestScoreMove2 = null;
		highestScoreMove3 = null;
	} // end default constructor

	/************************************************************************************
	 * analyzeBoard gets all possible moves, analyzes them, then sets highestScoreMove to the best scoring move
	 * 
	 * @param game
	 *           the game that is being played
	 * @return nothing
	 */
	public void analyzeBoard( Game game )
	{
		// retrieve the current state of the game
		Piece[ ] pieces = game.getPieces ( );
		ComputerMove[ ] possibleMoves = getAllPossibleMoves ( pieces );
		analyzePossibleMoves ( pieces, possibleMoves, game );
		setHighestScoringMoves ( possibleMoves );
	} // end analyzeBoard

	/************************************************************************************
	 * getAllPossibleMoves makes an array of all of the possible ComputerMoves
	 * 
	 * @param pieces
	 *           the array of all the pieces
	 * @return possibleMoves: the array of all possible ComputerMoves
	 */
	public ComputerMove[ ] getAllPossibleMoves( Piece[ ] pieces )
	{
		// arrayList of possible moves
		ArrayList<ComputerMove> allPossibleMovesList = new ArrayList<ComputerMove> ( );
		Square destinationSquare = new Square ( ); // the destination square

		// look at each piece and add all legal moves to allPossibleMoves
		for ( int i = firstPiece; i <= lastPiece; i++ )
		{
			// make sure the piece is not removed
			if ( !pieces[i].getPosition ( ).sameSquare ( Square.removed ) )
			{
				// check each column
				for ( int j = 1; j < 9; j++ )
				{
					// check each row
					for ( int k = 1; k < 9; k++ )
					{
						destinationSquare.setPosition ( j, k );
						// make sure the piece has a legal move
						if ( pieces[i].hasLegalMove ( destinationSquare, pieces ) )
						{
							// add the ComputerMove to the ArrayList using the valid piece and destination
							allPossibleMovesList.add ( new ComputerMove ( pieces[i], new Square ( j, k ) ) );
						}
					}
				}
			}
		}
		// turn the arrayList into just an array
		ComputerMove[ ] allPossibleMoves = new ComputerMove[allPossibleMovesList.size ( )];
		allPossibleMovesList.toArray ( allPossibleMoves );
		return allPossibleMoves;
	} // end getAllPossibleMoves

	/************************************************************************************
	 * analyzePossibleMoves analyzes all possible moves and assigns a score to each move
	 * 
	 * @param pieces
	 *           the array of all the pieces
	 * @param possibleMoves
	 *           the array of all possible ComputerMoves
	 * @return nothing
	 */
	public void analyzePossibleMoves( Piece[ ] pieces, ComputerMove[ ] possibleMoves, Game game )
	{
		/*
		 * 1: offSet all scores
		 * 
		 * 2: award or punish hesitation
		 * 
		 * 3: award captures
		 *
		 * 4: award piece betterment
		 * 
		 * 5: punish putting pieces in danger
		 * 
		 * 6: award or punish ending the game
		 */
		double capturePoints; // point worth of any piece being captured (0.0 if none)
		// 1: calculate and set each piece's score (removed pieces are 0.0)
		// 2: store the current point standing to compare each tested move to the board right now
		double pointStandingBefore = pointStanding ( pieces, new ComputerMove ( ) );
		double pointStandingAfter; // the point standing after each move is tested
		double pieceBetterment; // how much the pieces improve after a move
		// offset scores
		offSetScores ( possibleMoves );

		// cycle through each move
		for ( ComputerMove c : possibleMoves )
		{
			c.getAddReason ( ).println ( c ); // label the move reasoning
			// award or punish hesitation
			c.getAddReason ( ).println ( "\t(hesitation)" ); // label the reasoning
			c.addToScore ( c.getPiece ( ).getPieceHesitation ( game, c, difficulty ) );
			// get the capture points
			capturePoints = awardCapturePoints ( pieces, c );
			c.addToScore ( capturePoints );

			// test the move
			Piece[ ] futurePieces = testMove ( c, pieces );

			// get the new point standing
			pointStandingAfter = pointStanding ( futurePieces, c );
			// add the difference between pointStandingBefore and pointStandingAfter
			// also subtract any capturePoints since pointStanding will see captures as well
			pieceBetterment = ( pointStandingAfter - pointStandingBefore - capturePoints )
					* difficulty.getPieceBettermentCare ( );
			c.addToScore ( pieceBetterment );
			c.getAddReason ( )
					.println ( Math.round ( pieceBetterment * 100.0 ) / 100.0 + " points awarded for piece betterment" );
			// sense danger levels of each piece
			double[ ] losses = getDangerLevels ( futurePieces, c );
			c.addToScore ( getWorstDanger ( losses, c ) ); // this will be 0.0 or a negative number
			// award points for ending the game
			c.addToScore ( awardEndGame ( futurePieces, pointStandingBefore, c, game ) );
			c.getAddReason ( ).println ( "--Total Score After Analysis: " + c.getScore ( ) );
		}
		System.out.println ( "\nHere are the possible moves" );
		Test.printMoves ( possibleMoves );

	} // end analyzePossibleMoves

	/*************************************************************************************
	 * setHighestScoringMoves sets highestScoreMove to the move with the highest score
	 * 
	 * @param possibleMoves
	 *           the array of all possible ComputerMoves
	 * @return nothing
	 */
	public void setHighestScoringMoves( ComputerMove[ ] possibleMoves )
	{
		// find the lowest score move so that highest scoring moves can originally be set to it
		ComputerMove lowestScoreMove = possibleMoves[0];
		for ( ComputerMove c : possibleMoves )
		{
			if ( c.getScore ( ) < lowestScoreMove.getScore ( ) )
			{
				lowestScoreMove = c;
			}
		}
		// set all highestScoreMoves to the lowest scoring move
		highestScoreMove = lowestScoreMove; // 1st place move
		highestScoreMove2 = lowestScoreMove; // 2nd place move
		highestScoreMove3 = lowestScoreMove; // 3rd place move

		// get the highest scoring move
		for ( ComputerMove c : possibleMoves )
		{
			if ( c.getScore ( ) > highestScoreMove.getScore ( ) )
			{
				highestScoreMove = c;
			}
		}
		// get the 2nd highest scoring move
		if ( possibleMoves.length > 1 )
		{
			for ( ComputerMove c : possibleMoves )
			{
				if ( c.getScore ( ) > highestScoreMove2.getScore ( ) && !c.equals ( highestScoreMove ) )
				{
					highestScoreMove2 = c;
				}
			}
		}
		else
		{
			highestScoreMove2 = null;
		}
		// get the 3rd highest scoring move
		if ( possibleMoves.length > 2 )
		{
			for ( ComputerMove c : possibleMoves )
			{
				if ( c.getScore ( ) > highestScoreMove3.getScore ( ) && !c.equals ( highestScoreMove )
						&& !c.equals ( highestScoreMove2 ) )
				{
					highestScoreMove3 = c;
				}
			}
		}
		else
		{
			highestScoreMove3 = null;
		}
	} // end setHighestScoringMoves

	/*****************************************************************************
	 * awardCapturePoints gets to amount of points the piece is worth on destination square
	 * 
	 * @param pieces
	 *           the array of all the pieces
	 * @param currMove
	 *           the computerMove with the destination square
	 * @return capturePoints: the amount of points the captured piece was worth
	 */
	public double awardCapturePoints( Piece[ ] pieces, ComputerMove currMove )
	{
		currMove.getAddReason ( ).println ( "\t(captures)" ); // label awardCapturePoints
		double capturePoints = 0.0; // assume nothing is being captured
		// get the piece on destination square
		Piece capturePiece = Game.getPieceOnSquare ( currMove.getDestinationSquare ( ), pieces );
		if ( capturePiece != null )
		{
			// award points equal to that piece's worth
			capturePoints = capturePiece.getPointWorth ( );
			// multiply by type desire to take
			switch ( capturePiece.getType ( ) )
			{
				case PAWN:
					capturePoints *= difficulty.getDesireToTakePawn ( );
					break;
				case KNIGHT:
					capturePoints *= difficulty.getDesireToTakeKnight ( );
					break;
				case BISHOP:
					capturePoints *= difficulty.getDesireToTakeBishop ( );
					break;
				case ROOK:
					capturePoints *= difficulty.getDesireToTakeRook ( );
					break;
				case QUEEN:
					capturePoints *= difficulty.getDesireToTakeQueen ( );
					break;
				default:
					// do nothing
			}
			capturePoints = Math.round ( capturePoints * difficulty.getDesireToCapture ( ) * 100.0 ) / 100.0;
		}
		// add to reasoning if capturePoints is not 0.0
		if ( Math.abs ( capturePoints ) > 0.01 )
		{
			currMove.getAddReason ( ).println ( capturePoints + " points awarded for taking the " + capturePiece );
		}
		return capturePoints;
	} // end awardCapturePoints

	/******************************************************************************
	 * pointStanding calculates the value of each piece on the board and returns the difference between players
	 * 
	 * positive means the computer is winning, negative means the enemy is winning
	 * 
	 * @param pieces
	 *           the array of all pieces
	 * @return pointStanding: the difference in player's total piece value
	 */
	public double pointStanding( Piece[ ] pieces, ComputerMove currMove )
	{
		double difference = 0.0;
		// calculate each piece's worth based on it's position
		currMove.getAddReason ( ).println ( "\t(position differences)" ); // label the reasoning
		for ( int i = 0; i < 32; i++ )
		{
			pieces[i].calculatePositionScore ( pieces, difficulty, currMove );
		}
		// calculate each piece's worth based on what pieces it supports and threatens
		currMove.getAddReason ( ).println ( "\t(observation differences)" ); // label the reasoning
		for ( int i = 0; i < 32; i++ )
		{
			pieces[i].calculateObservationScore ( pieces, difficulty, currMove );
		}
		// add the point total of the computer's pieces
		for ( int i = firstPiece; i < lastPiece; i++ )
		{
			difference += pieces[i].getPointWorth ( );
		}
		// subtract the point total of the opponent's pieces
		for ( int i = oppFirstPiece; i < oppLastPiece; i++ )
		{
			difference -= pieces[i].getPointWorth ( );
		}
		// round the number and adjust it according to difficulty
		difference = Math.round ( difference * 100.0 ) / 100.0;
		return ( difference * difficulty.getPieceBettermentCare ( ) );
	} // end pointStanding

	/*******************************************************************************
	 * awardCheckMate determines if the board delivers check mate to the opponent
	 * 
	 * @param pieces
	 *           the array of all the pieces
	 * @return points: if it's check mate, points is checkMateAwareness. Otherwise 0.0
	 */
	public double awardEndGame( Piece[ ] pieces, double pointStandingBefore, ComputerMove currMove, Game game )
	{
		currMove.getAddReason ( ).println ( "\t(end game)" ); // label the reasoning
		double points = 0;
		int endGame = game.endGameCheck ( );
		// this move delivers check mate
		if ( endGame == 1 )
		{
			points = difficulty.getCheckMateAwareness ( );
			currMove.getAddReason ( ).println ( points + " points awared for checkmate" );
		}
		// this move ends in a draw, and the computer is losing
		else if ( endGame > 1 && pointStandingBefore < 0 )
		{
			// add points, a draw is good when losing
			points = difficulty.getDrawAwareness ( );
			currMove.getAddReason ( ).println ( points + " points awarded for draw" );
		}
		// this move ends in a draw, and the computer is winning or tied
		else if ( endGame > 1 )
		{
			// subtract points, a draw is bad when winning
			points = -difficulty.getDrawAwareness ( );
			currMove.getAddReason ( ).println ( points + " points lost for draw" );
		}
		return points;
	} // end awardCheckMate

	/**********************************************************************************
	 * getWorstDanger finds the worst possible exchange, returning it's point loss value
	 * 
	 * @param losses
	 *           the array of each piece's senseDanger value
	 * @param move
	 *           the move that is played (used to update that piece's senseDanger value)
	 * @param pieces
	 *           the array of all the pieces
	 * @return worstDanger: 0.0 if there is no danger, or a negative number representing the point value of the piece
	 *         that can be taken
	 */
	public double getWorstDanger( double[ ] losses, ComputerMove currMove )
	{
		double worstDanger = losses[0];
		for ( double d : losses )
		{
			if ( worstDanger > d )
			{
				worstDanger = d;
			}
		}
		// make sure the danger is 0.0 or less
		if ( worstDanger > 0.0 )
		{
			worstDanger = 0.0;
		}
		// add to the reasoning
		currMove.getAddReason ( )
				.println ( Math.round ( worstDanger * 100.0 ) / 100.0 + " points lost for worst danger level" );
		return worstDanger;
	} // end getWorstDanger

	/*********************************************************************************
	 * getDangerLevels calls senseDanger for every friendly piece on the board
	 * 
	 * @param pieces
	 *           the array of all pieces
	 * @return losses: the double array filled with each piece's senseDanger value
	 */
	public double[ ] getDangerLevels( Piece[ ] pieces, ComputerMove currMove )
	{
		double[ ] losses = new double[16]; // the amount of pieces owned
		// get correct index of losses if computer is playing as black
		int playingBlack = ( color == Piece.Color.BLACK ) ? 16 : 0;
		currMove.getAddReason ( ).println ( "\t(danger levels)" ); // label the reasoning
		for ( int i = firstPiece; i <= lastPiece; i++ )
		{
			// only check pieces on the board
			if ( pieces[i].getPosition ( ).sameSquare ( Square.removed ) )
			{
				losses[i - playingBlack] = 0.0;
			}
			else
			{
				losses[i - playingBlack] = senseDanger ( pieces, pieces[i], currMove );
				// adjust awareness of danger based on piece type
				switch ( pieces[i].getType ( ) )
				{
					case PAWN:
						losses[i - playingBlack] *= difficulty.getPawnDangerAwareness ( );
						break;
					case KNIGHT:
						losses[i - playingBlack] *= difficulty.getKnightDangerAwareness ( );
						break;
					case BISHOP:
						losses[i - playingBlack] *= difficulty.getBishopDangerAwareness ( );
						break;
					case ROOK:
						losses[i - playingBlack] *= difficulty.getRookDangerAwareness ( );
						break;
					case QUEEN:
						losses[i - playingBlack] *= difficulty.getQueenDangerAwareness ( );
						break;
					default:
						// do nothing
				}
				// adjust awareness of danger based on difficulty
				losses[i - playingBlack] *= difficulty.getDangerAwareness ( );
			}
		}
		return losses;
	} // end getDangerLevels

	/***********************************************************************************
	 * senseDanger analyzes how much danger the given piece is in of being captured
	 * 
	 * @param pieces
	 *           the array of all pieces
	 * @param currPiece
	 *           that piece that is being analyzed
	 * @return bestStop: 0.0 if the piece is in no real danger, or a negative number representing the point value of the
	 *         piece that can be taken
	 */
	public double senseDanger( Piece[ ] pieces, Piece currPiece, ComputerMove currMove )
	{
		double currLoss = 0.0; // the amount of points gained / lost through the piece exchange
		double bestStop = 0.0; // the best stop during the exchange (most positive outcome)
		double nextExchangeLoss;
		// friendly pieces that see currPiece
		ArrayList<Piece> supportPieces;
		// opponent pieces that see currPiece
		ArrayList<Piece> threateningPieces = getThreateningPieces ( pieces, currPiece.getPosition ( ) );

		// subtract points if the piece can be taken
		if ( threateningPieces.size ( ) > 0 )
		{
			// set point loss
			currLoss -= currPiece.getPointWorth ( );
			bestStop = currLoss;
			currMove.getAddReason ( ).print ( currPiece + ": (" + threateningPieces.get ( 0 ).getType ( ) + " threat) "
					+ Math.round ( currLoss * 100.0 ) / 100.0 + " " );
			// test the move using the first threatening piece
			Piece[ ] futurePieces = testMove ( new ComputerMove ( threateningPieces.get ( 0 ), currPiece.getPosition ( ) ),
					pieces );
			// find support pieces
			supportPieces = getSupportPieces ( futurePieces, currPiece.getPosition ( ) );
			// add points if the square can be taken back
			while ( supportPieces.size ( ) > 0 )
			{
				currLoss += threateningPieces.get ( 0 ).getPointWorth ( );
				currMove.getAddReason ( ).print ( "(" + supportPieces.get ( 0 ).getType ( ) + " support) "
						+ Math.round ( currLoss * 100.0 ) / 100.0 + " " );
				// test the move
				// testMove() is not used because there's no longer a need to create new objects
				Game.testMove ( supportPieces.get ( 0 ), currPiece.getPosition ( ), futurePieces );
				// find new threatening pieces (to find hidden threatening pieces since some pieces moved)
				threateningPieces = getThreateningPieces ( futurePieces, currPiece.getPosition ( ) );
				// have opponent check if they are okay with next exchange
				if ( threateningPieces.size ( ) > 0 && supportPieces.size ( ) > 1 )
				{
					// the next exchange is the current loss minus the next support piece plus the next threatening piece
					nextExchangeLoss = currLoss - supportPieces.get ( 0 ).getPointWorth ( )
							+ threateningPieces.get ( 0 ).getPointWorth ( );
					// for testing TODO take out
					if ( nextExchangeLoss > currLoss )
					{
						currMove.getAddReason ( ).print ( "(not worth it " + nextExchangeLoss + ") " );
					}
				}
				else
				{
					// the opponent is okay with the next exchange (if they have a piece)
					// 1.0 is arbitrary, just to ensure nextExchangeLoss is <= currLoss
					nextExchangeLoss = currLoss - 1.0;
				}
				// subtract points if the opponent would want to take again
				if ( threateningPieces.size ( ) > 0 && nextExchangeLoss <= currLoss )
				{
					currLoss -= supportPieces.get ( 0 ).getPointWorth ( );
					currMove.getAddReason ( ).print ( "(" + threateningPieces.get ( 0 ).getType ( ) + " threat) "
							+ Math.round ( currLoss * 100.0 ) / 100.0 + " " );
					// test the move
					Game.testMove ( threateningPieces.get ( 0 ), currPiece.getPosition ( ), futurePieces );
					// find new supporting pieces
					supportPieces = getSupportPieces ( futurePieces, currPiece.getPosition ( ) );
				}
				// get out of loop if exchange is over
				else
				{
					supportPieces.clear ( );
				}
				// store the best move to stop initiating the exchange
				if ( bestStop < currLoss )
				{
					bestStop = currLoss;
				}
			}
			// add danger level to reasoning
			currMove.getAddReason ( ).println ( "(end)" );
		}
		return bestStop;
	} // end senseDanger

	/***********************************************************************************
	 * getSupportPieces gets an ArrayList of pieces that can support given square
	 * 
	 * @param pieces
	 *           the array of all pieces
	 * @param supportSquare
	 *           the square pieces are trying to support
	 * @return suppportPieces: the arrayList of all pieces that can support supportSquare
	 */
	public ArrayList<Piece> getSupportPieces( Piece[ ] pieces, Square supportSquare )
	{
		ArrayList<Piece> supportPieces = new ArrayList<Piece> ( );
		// cycle through all of the player's pieces looking for support pieces
		for ( int i = firstPiece; i <= lastPiece; i++ )
		{
			// add support pieces that are still on the board
			if ( !pieces[i].getPosition ( ).sameSquare ( Square.removed )
					&& pieces[i].canSupport ( supportSquare, pieces ) )
			{
				supportPieces.add ( pieces[i] );
			}
		}
		sortPieceList ( supportPieces );
		return supportPieces;
	} // end getSupportPieces

	/***********************************************************************************
	 * getThreateningPieces gets an ArrayList of pieces that can target given square
	 * 
	 * @param pieces
	 *           the array of all pieces
	 * @param targetSquare
	 *           the square pieces are trying to target
	 * @return threateningPieces: the arrayList of pieces that can target targetSquare
	 */
	public ArrayList<Piece> getThreateningPieces( Piece[ ] pieces, Square targetSquare )
	{
		ArrayList<Piece> threateningPieces = new ArrayList<Piece> ( );
		// cycle through all of the player's pieces looking for support pieces
		for ( int i = oppFirstPiece; i <= oppLastPiece; i++ )
		{
			// add threatening pieces that are still on the board
			if ( !pieces[i].getPosition ( ).sameSquare ( Square.removed )
					&& pieces[i].canSupport ( targetSquare, pieces ) )
			{
				threateningPieces.add ( pieces[i] );
			}
		}
		sortPieceList ( threateningPieces );
		return threateningPieces;
	} // end getThreateningPieces

	/**********************************************************************************
	 * sortPiecesList sorts an arrayList of pieces into ascending order based on point worth
	 * 
	 * @param pieces
	 *           the arrayList of pieces being sorted
	 * @return nothing
	 */
	public void sortPieceList( ArrayList<Piece> pieces )
	{
		Piece tempPiece; // temporary piece used for swapping piece places
		// compare each piece to the next piece in the list
		for ( int i = 0; i + 1 < pieces.size ( ); i++ )
		{
			if ( pieces.get ( i ).getPointWorth ( ) > pieces.get ( i + 1 ).getPointWorth ( ) )
			{
				tempPiece = pieces.get ( i ); // temporary reference
				pieces.remove ( i ); // remove the piece with higher point worth
				pieces.add ( tempPiece ); // add it to the end
				i--; // stay on current piece
			}
		}
	} // end sortPiecesList

	/********************************************************************************
	 * testMove returns what the board would look like after a move is played
	 * 
	 * @param move
	 *           the ComputerMove that is being examined
	 * @param currentPieces
	 *           the current state of the board (the array of all pieces)
	 * @return futurePieces: the array of all pieces after the ComputerMove is tested
	 */
	public Piece[ ] testMove( ComputerMove move, Piece[ ] currentPieces )
	{
		Piece[ ] futurePieces = Game.copyBoard ( currentPieces ); // copy the board
		// move the piece in futurePieces that's on the same square as move's piece
		Game.testMove ( Game.getPieceOnSquare ( move.getPiece ( ).getPosition ( ), futurePieces ),
				move.getDestinationSquare ( ), futurePieces );
		return futurePieces;
	} // end getFuturePieces

	/*********************************************************************************
	 * offSetScores offsets each move's score by a random number between offset and -offset
	 * 
	 * the purpose of this is to give the computer the illusion of humanity (more randomness)
	 * 
	 * offset is the amount of points times 0.01, so 100 would offset by up to 1 point
	 * 
	 * @param moves
	 *           the array of all possible moves
	 * @return nothing
	 */
	public void offSetScores( ComputerMove[ ] moves )
	{
		double decimalOffSet; // the offset * 0.01
		if ( difficulty.getOffSet ( ) > 0.0 )
		{
			for ( ComputerMove c : moves )
			{
				// random number range: (2 * offSet), lowest number: -offSet
				decimalOffSet = ( random.nextInt ( 2 * difficulty.getOffSet ( ) ) - difficulty.getOffSet ( ) ) * 0.01;
				c.addToScore ( decimalOffSet );
			}
		}
	} // end offSetScores

	// getters and setters
	public void setColor( Piece.Color color )
	{
		this.color = color;
		// also set opponent's color
		oppColor = ( color == Piece.Color.WHITE ) ? Piece.Color.BLACK : Piece.Color.WHITE;
		// also set the index location of computer's pieces and opponent's pieces
		firstPiece = 0;
		lastPiece = 15;
		oppFirstPiece = 16;
		oppLastPiece = 31;
		if ( color == Piece.Color.BLACK )
		{
			oppFirstPiece = 0;
			oppLastPiece = 15;
			firstPiece = 16;
			lastPiece = 31;
		}
	}

	public void setDifficulty( int difficultyLevel )
	{
		difficulty = new ComSettings ( difficultyLevel );
	}

	public Piece.Color getColor( )
	{
		return color;
	}

	public void setDifficultySettings( ComSettings difficultySettings )
	{
		this.difficulty = difficultySettings;
	}

	public ComSettings getDifficulty( )
	{
		return difficulty;
	}

	public ComputerMove getHighestScoreMove( )
	{
		return highestScoreMove;
	}

	public ComputerMove getHighestScoreMove2( )
	{
		return highestScoreMove2;
	}

	public ComputerMove getHighestScoreMove3( )
	{
		return highestScoreMove3;
	}
}
