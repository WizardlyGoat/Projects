import java.util.ArrayList;

import javafx.application.Platform;

public class Game
{
	// static fields
	public static final int STARTING_PIECES = 32; // 16 pieces for each player
	public static Piece.Color white = Piece.Color.WHITE; // white pieces
	public static Piece.Color black = Piece.Color.BLACK; // black pieces
	private static Square repick = new Square ( -1, -1 ); // a position meaning the user must repick a piece

	private Piece.Color playerTurn; // player's turn: white or black
	// every position since the start of game
	private ArrayList<Piece[ ]> pastPositions;
	// the pieces in their current state
	protected Piece[ ] pieces;
	// the amount of turns past since the last pawn move or piece capture (for draws)
	private int captureOrPawnMove;
	private String ending;

	// default constructor
	public Game()
	{
		playerTurn = white;
		pastPositions = new ArrayList<Piece[ ]> ( );
		captureOrPawnMove = 0;
		pieces = new Piece[STARTING_PIECES];
		ending = "0-0";
	} // end default constructor

	/***********************************************************************************
	 * startGame is what is called to start the game
	 * 
	 * @param nothing
	 * @return nothing
	 */
	public void startGame( )
	{
		// setup
		Computer computer = new Computer ( ); // the computer player
		Piece currPiece; // piece that is being moved
		Square destinationSquare; // destination that currPiece is moving
		int endGame = 0; // 0: game not over, 1: check mate, 2+: draw
		setStartingPositions ( pieces ); // initialize the starting pieces
		// Test.removePieces ( pieces );

		// add all of the pieces to the board (GUI)
		Platform.runLater ( new Runnable ( )
		{
			@Override
			public void run( )
			{
				ChessDriver.addStartingPieces ( pieces );
			}
		} );
		// start a collection of pieces arrays representing each position throughout the game
		Piece[ ] currPieces;
		currPieces = copyBoard ( pieces ); // make a copy of the current board
		getPastPositions ( ).add ( currPieces ); // add the starting position

		waitForGUI ( );
		if ( !ChessDriver.getDifficulty ( ).equals ( "" ) )
		{
			switch ( ChessDriver.getDifficulty ( ) )
			{
				case "Random":
					computer.setDifficulty ( 1 );
					break;
				case "Easy":
					computer.setDifficulty ( 2 );
					break;
				default:
					computer.setDifficulty ( 0 );
			}
			if ( ChessDriver.getPlayAs ( ).equals ( "Play as White" ) )
			{
				computer.setColor ( black );
			}
			else
			{
				computer.setColor ( white );
			}
		}
		// take turns moving pieces
		do
		{
			// computer turn
			if ( getPlayerTurn ( ) == computer.getColor ( ) )
			{
				// analyze the board for the best move (per difficulty)
				computer.analyzeBoard ( this );
				// set the currPiece and destinationSquare
				currPiece = computer.getHighestScoreMove ( ).getPiece ( );
				destinationSquare = computer.getHighestScoreMove ( ).getDestinationSquare ( );
				// print the 3rd best move if present
				if ( computer.getHighestScoreMove3 ( ) != null )
				{
					System.out.println ( "\n3rd Best Scoring Move..........................................." );
					computer.getHighestScoreMove3 ( ).printReasoning ( );
				}
				// print the 2nd best move if present
				if ( computer.getHighestScoreMove2 ( ) != null )
				{
					System.out.println ( "\n2nd Best Scoring Move..........................................." );
					computer.getHighestScoreMove2 ( ).printReasoning ( );
				}
				// print the best move (the move that was played)
				System.out.println ( "\nBest Scoring Move..............................................." );
				computer.getHighestScoreMove ( ).printReasoning ( );
			}
			// player turn
			else
			{
				// reset currPiece and destinationSquare
				currPiece = null;
				destinationSquare = null;
				// set the board perspective based on who's turn it is (GUI)
				Platform.runLater ( new Runnable ( )
				{
					@Override
					public void run( )
					{
						ChessDriver.setPerspective ( getPlayerTurn ( ) );
					}
				} );
				// get a valid piece and destination from player
				do
				{
					// clear all squares of legal move effects in GUI
					Platform.runLater ( new Runnable ( )
					{
						@Override
						public void run( )
						{
							ChessDriver.clearLegalMoveEffects ( );
						}
					} );
					if ( currPiece != null )
					{
						// add legal move effects in GUI
						ArrayList<String> legalMoves = getAllLegalMoves ( pieces, currPiece );
						ArrayList<String> legalTakes = getAllLegalTakes ( pieces, currPiece );
						Platform.runLater ( new Runnable ( )
						{
							@Override
							public void run( )
							{
								ChessDriver.addLegalMoveEffects ( legalMoves );
								ChessDriver.addLegalTakeEffects ( legalTakes );
							}
						} );
					}
					// get the piece or the destination
					Square currSquare = getValidSquare ( ); // get input square from GUI
					// deselect the piece if it was already selected
					if ( currPiece != null && currPiece.getPosition ( ).sameSquare ( currSquare ) )
					{
						currPiece = null;
					}
					// get a friendly piece if chosen
					else if ( isPieceOnSquare ( currSquare, pieces )
							&& getPieceOnSquare ( currSquare, pieces ).getColor ( ) == getPlayerTurn ( ) )
					{
						currPiece = getPieceOnSquare ( currSquare, pieces );
					}
					// get destination once a piece is chosen and can make it to selected square
					else if ( currPiece != null && currPiece.hasLegalMove ( currSquare, pieces ) )
					{
						destinationSquare = currSquare;
					}
					// deselect the piece
					else
					{
						currPiece = null;
					}
					// continue once the piece and destination are selected
				} while ( currPiece == null || destinationSquare == null );
			}
			// execute the move
			movePiece ( currPiece, destinationSquare, pieces, computer );
			// switch player turns
			endMove ( pieces );
			// clear new player's pawns of enPassant
			clearEnPassant ( getPlayerTurn ( ), pieces );
			// add the current position to pastPositions
			currPieces = copyBoard ( pieces );
			getPastPositions ( ).add ( currPieces );
			// see if the game is over
			endGame = endGameCheck ( );
		} while ( endGame == 0 );
		// the game is over
		String endGameMessage; // what the game displays as the reason for game ending
		switch ( endGame )
		{
			case 1:
				endGameMessage = "Check Mate!";
				break;
			case 2:
				endGameMessage = "Draw by Stalemate";
				break;
			case 3:
				endGameMessage = "Draw by 50-move Rule";
				break;
			case 4:
				endGameMessage = "Draw by Three-Fold Repetition";
				break;
			default:
				endGameMessage = "The game has ended for some reason...";
		}
		Platform.runLater ( new Runnable ( )
		{
			@Override
			public void run( )
			{
				// clear all effects
				ChessDriver.clearLegalMoveEffects ( );
				ChessDriver.clearPastMoveEffects ( );
				// display end game message
				ChessDriver.displayEndGame ( endGameMessage );
			}
		} );
	}

	/************************************************************************************
	 * endMove switches player turns
	 * 
	 * @param pieces
	 *           the array of all the pieces
	 * @return nothing
	 */
	public void endMove( Piece[ ] pieces )
	{
		if ( getPlayerTurn ( ) == white )
		{
			setPlayerTurn ( black );
		}
		else
		{
			setPlayerTurn ( white );
		}
	} // end endMove

	/************************************************************************************
	 * waitForGUI waits for ChessDriver's clickedSquare to be notified
	 * 
	 * @param nothing
	 * @return nothing
	 */
	public static void waitForGUI( )
	{
		try
		{
			synchronized (ChessDriver.getClickedSquare ( ))
			{
				ChessDriver.getClickedSquare ( ).wait ( );
			}
		} catch ( Exception e )
		{
			e.printStackTrace ( );
		}
	} // end waitForGUI

	/*************************************************************************************
	 * endGameCheck determines if the game is over (through check mate or draw)
	 * 
	 * @return 0: game not over, 1: check mate, 2: stale mate, 3: 50-move rule, 4: three fold repetition
	 */
	public int endGameCheck( )
	{
		Piece.Color playerTurn = getPlayerTurn ( ); // player turn white or black
		int endGameCheck = 1; // starting with the assumption that this is check mate
		Square currSquare = new Square ( ); // current square
		int startPiece = 0; // white's first piece
		int endPiece = 15; // white's last piece
		if ( playerTurn == black )
		{
			startPiece = 16; // black's first piece
			endPiece = 31; // black's last piece
		}
		// check all of player's pieces for a legal move
		for ( int i = startPiece; i <= endPiece; i++ )
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
						currSquare.setPosition ( j, k );
						// make sure the piece can stop check
						if ( pieces[i].hasLegalMove ( currSquare, pieces ) )
						{
							endGameCheck = 0; // there is a legal move
						}
					}
				}
			}
		}
		// see if this is the third time this position has occurred
		if ( endGameCheck == 0 && getCaptureOrPawnMove ( ) >= 6 && getCaptureOrPawnMove ( ) < 100 )
		{
			int identicalPositions = 1; // the amount of identical positions (this position is identical to itself)
			// compare to every same color position since the last capture or pawn move
			for ( int i = 2; i <= getCaptureOrPawnMove ( ); i += 2 )
			{
				// compare this position and the position i positions ago
				// i is even so that it only gets positions with the same color player's move
				identicalPositions += compareForIdenticalPosition ( pieces,
						getPastPositions ( ).get ( getPastPositions ( ).size ( ) - 1 - i ) );
			}
			if ( identicalPositions >= 3 )
			{
				endGameCheck = 4; // draw by three fold repetition
			}
		}
		// no captures or pawn moves in the last 100 positions (50 moves)
		else if ( endGameCheck == 0 && getCaptureOrPawnMove ( ) >= 100 )
		{
			endGameCheck = 3; // draw by 50-move rule
		}
		// no legal moves and not in check
		else if ( endGameCheck == 1 && !inCheck ( pieces, playerTurn ) )
		{
			endGameCheck = 2; // draw by stale mate
		}
		return endGameCheck;
	} // end checkMate

	/***************************************************************************************
	 * inCheck determines if the given player's king is in danger
	 * 
	 * @param pieces
	 *           the array of all the pieces
	 * @param playerTurn
	 *           the color of the current player's king
	 * @return true if the player's king is in danger, false otherwise
	 */
	public static boolean inCheck( Piece[ ] pieces, Piece.Color playerTurn )
	{
		boolean check = false;
		int startIndex = 0; // white's first piece
		int endIndex = 15; // white's last piece
		if ( playerTurn == white ) // get opposite player's pieces
		{
			startIndex = 16; // black's first piece
			endIndex = 31; // black's last piece
		}
		// ( ( playerTurn == white ) ? 4 : 20 ) the king is at index 4 for white, and at index 20 for black
		// the king's position
		Square kingSquare = new Square ( pieces[ ( playerTurn == white ) ? 4 : 20].getPosition ( ) );
		// check other player's ability to target the kingSquare
		for ( int i = startIndex; i <= endIndex; i++ )
		{
			if ( ! ( pieces[i].getPosition ( ).sameSquare ( Square.removed ) )
					&& pieces[i].hasLegalMove ( kingSquare, pieces ) )
			{
				check = true;
			}
		}
		return check;
	} // end inCheck

	/*****************************************************************************************
	 * compareForIdenticalPosition compares two positions and determines if they are the same or not
	 * 
	 * @param pieces1
	 *           the first position being compared
	 * @param pieces2
	 *           the second position being compared
	 * @return samePosition 0: not the same position, 1: same positions
	 */
	public static int compareForIdenticalPosition( Piece[ ] pieces1, Piece[ ] pieces2 )
	{
		int samePosition = 1;
		for ( int i = 0; i < STARTING_PIECES; i++ )
		{
			// compare all identical pieces' positions
			if ( !pieces1[i].getPosition ( ).sameSquare ( pieces2[i].getPosition ( ) ) )
			{
				samePosition = 0;
			}
		}
		return samePosition;
	} // end compareForIdenticalPosition

	/***************************************************************************************
	 * movePiece executes a move knowing just the selected piece and destination
	 * 
	 * @param currPiece
	 *           the selected Piece
	 * @param destinationSquare
	 *           the square that the piece is being moved to
	 * @param pieces
	 *           the array of all pieces
	 * @return nothing
	 */
	public void movePiece( Piece currPiece, Square destinationSquare, Piece[ ] pieces, Computer computer )
	{
		Platform.runLater ( new Runnable ( )
		{
			@Override
			public void run( )
			{
				ChessDriver.clearPastMoveEffects ( );
			}
		} );
		int x = destinationSquare.getX ( ); // new x position
		int y = destinationSquare.getY ( ); // new y position
		setCaptureOrPawnMove ( getCaptureOrPawnMove ( ) + 1 ); // moves since last pawn move or capture

		// take captured piece off the board
		Piece capturePiece = getPieceOnSquare ( destinationSquare, pieces );
		if ( capturePiece != null )
		{
			setCaptureOrPawnMove ( 0 ); // 0 moves since last capture or pawn move
			// remove the piece in the GUI
			String position = capturePiece.getPosition ( ).getStringPosition ( );
			Platform.runLater ( new Runnable ( )
			{
				@Override
				public void run( )
				{
					ChessDriver.removePiece ( position );
				}
			} );
			capturePiece.setPosition ( Square.removed );
		}
		// check for canGetEnPassant and take out captures via enPassant
		if ( currPiece.getType ( ) == Piece.Type.PAWN )
		{
			setCaptureOrPawnMove ( 0 ); // 0 moves since last capture or pawn move
			Piece enPassantPiece = ( (Pawn) currPiece ).getEnPassantPiece ( pieces );
			// difference in x movement (must be 1 for capture, 0 for going forward)
			int xDiff = Math.abs ( currPiece.getPosition ( ).getX ( ) - x );
			// difference in y movement (absolute value)
			int yDiff = Math.abs ( currPiece.getPosition ( ).getY ( ) - y );

			// turn canGetOnPassant on if pawn jumped two spaces
			if ( Math.abs ( y - currPiece.getPosition ( ).getY ( ) ) == 2 )
			{
				( (Pawn) currPiece ).setCanGetEnPassant ( true );
			}
			// promote pawns on either back rank (row 1 or 8)
			else if ( y == 1 || y == 8 )
			{
				currPiece = promotePawn ( currPiece, pieces, computer );
			}
			// remove pawn captured via enPassant
			// make sure capturing in the right direction
			else if ( yDiff == 1 && xDiff == 1 && enPassantPiece != null
					&& destinationSquare.getX ( ) == enPassantPiece.getPosition ( ).getX ( ) )
			{
				// remove the piece in the GUI
				String position = enPassantPiece.getPosition ( ).getStringPosition ( );
				Platform.runLater ( new Runnable ( )
				{
					@Override
					public void run( )
					{
						ChessDriver.removePiece ( position );
					}
				} );
				enPassantPiece.setPosition ( Square.removed );
			}
		}
		// disable castling for kings and move rook if the king just castled
		else if ( currPiece.getType ( ) == Piece.Type.KING )
		{
			( (King) currPiece ).setCanCastle ( false );

			// if the king moved 2 spaces (castled)
			if ( Math.abs ( x - currPiece.getPosition ( ).getX ( ) ) == 2 )
			{
				Piece rook = ( (King) currPiece ).getCastleRook ( destinationSquare, pieces );
				// get the rook's new X coordinate (will be 4 or 6)
				int rookXDestination = ( rook.getPosition ( ).getX ( ) == 1 ) ? 4 : 6;
				Square rookDestinationSquare = new Square ( rookXDestination, rook.getPosition ( ).getY ( ) );
				// move the piece in the GUI
				String rookPosition = rook.getPosition ( ).getStringPosition ( );
				String rookDestination = rookDestinationSquare.getStringPosition ( );
				Platform.runLater ( new Runnable ( )
				{
					@Override
					public void run( )
					{
						ChessDriver.movePiece ( rookPosition, rookDestination );
					}
				} );
				// move the rook's x position
				rook.setPosition ( rookDestinationSquare );
			}
		}
		// disable castling for rooks
		else if ( currPiece.getType ( ) == Piece.Type.ROOK )
		{
			( (Rook) currPiece ).setCanCastle ( false );
		}
		// move the piece in the GUI
		String startingPosition = currPiece.getPosition ( ).getStringPosition ( );
		String destination = destinationSquare.getStringPosition ( );
		Platform.runLater ( new Runnable ( )
		{
			@Override
			public void run( )
			{
				ChessDriver.movePiece ( startingPosition, destination );
			}
		} );
		// update new position of currPiece
		currPiece.setPosition ( destinationSquare );
	} // end movePiece

	/***************************************************************************************
	 * testMove executes a move without updating the GUI
	 * 
	 * @param currPiece
	 *           the selected Piece
	 * @param destinationSquare
	 *           the square that the piece is being moved to
	 * @param pieces
	 *           the array of all pieces
	 * @return nothing
	 */
	public static void testMove( Piece currPiece, Square destinationSquare, Piece[ ] pieces )
	{
		int x = destinationSquare.getX ( ); // new x position
		int y = destinationSquare.getY ( ); // new y position

		// take captured piece off the board
		Piece capturePiece = getPieceOnSquare ( destinationSquare, pieces );
		if ( capturePiece != null )
		{
			capturePiece.setPosition ( Square.removed );
		}
		// check for canGetEnPassant and take out captures via enPassant
		if ( currPiece.getType ( ) == Piece.Type.PAWN )
		{
			Piece enPassantPiece = ( (Pawn) currPiece ).getEnPassantPiece ( pieces );
			// difference in x movement (must be 1 for capture, 0 for going forward)
			int xDiff = Math.abs ( currPiece.getPosition ( ).getX ( ) - x );
			// difference in y movement (absolute value)
			int yDiff = Math.abs ( currPiece.getPosition ( ).getY ( ) - y );

			// turn canGetOnPassant on if pawn jumped two spaces
			if ( Math.abs ( y - currPiece.getPosition ( ).getY ( ) ) == 2 )
			{
				( (Pawn) currPiece ).setCanGetEnPassant ( true );
			}
			// remove pawn captured via enPassant
			// make sure capturing in the right direction
			else if ( yDiff == 1 && xDiff == 1 && enPassantPiece != null
					&& destinationSquare.getX ( ) == enPassantPiece.getPosition ( ).getX ( ) )
			{
				enPassantPiece.setPosition ( Square.removed );
			}
		}
		// disable castling for kings and move rook if the king just castled
		else if ( currPiece.getType ( ) == Piece.Type.KING )
		{
			( (King) currPiece ).setCanCastle ( false );

			Piece rook = ( (King) currPiece ).getCastleRook ( destinationSquare, pieces );
			// if the king moved 2 spaces (castled)
			if ( Math.abs ( x - currPiece.getPosition ( ).getX ( ) ) == 2 )
			{
				// move the rook's x position
				if ( rook.getPosition ( ).getX ( ) == 1 )
				{
					rook.setPosition ( 4, rook.getPosition ( ).getY ( ) );
				}
				else if ( rook.getPosition ( ).getX ( ) == 8 )
				{
					rook.setPosition ( 6, rook.getPosition ( ).getY ( ) );
				}
			}
		}
		// disable castling for rooks
		else if ( currPiece.getType ( ) == Piece.Type.ROOK )
		{
			( (Rook) currPiece ).setCanCastle ( false );
		}
		// update new position of currPiece
		currPiece.setPosition ( destinationSquare );
	} // end testMove

	/***********************************************************************************
	 * promotePawn promotes a given pawn to either a queen, rook, bishop, or knight
	 * 
	 * @param pawn
	 *           the pawn piece that is being promoted
	 * @param pieces
	 *           the array of all pieces
	 * @return the new piece that is made
	 */
	public static Piece promotePawn( Piece pawn, Piece[ ] pieces, Computer computer )
	{
		int x = pawn.getPosition ( ).getX ( ); // x position
		int y = pawn.getPosition ( ).getY ( ); // y position
		Piece.Color color = pawn.getColor ( ); // the piece's color
		String newType; // the new type of piece

		// computer promoting pawn
		if ( computer.getColor ( ) == color )
		{
			newType = "Queen";
		}
		// get input from user as to which piece to promote to (GUI)
		else
		{
			// add the promote pawn box
			Platform.runLater ( new Runnable ( )
			{
				@Override
				public void run( )
				{
					ChessDriver.addPromotePawnBox ( pawn.getColor ( ) );
				}
			} );
			// wait for a promote type response
			waitForGUI ( );
			newType = ChessDriver.getPromoteTo ( );
		}
		// promote the pawn
		switch ( newType )
		{
			case "Queen":
				pieces[pawn.getIndex ( )] = new Queen ( x, y, color );
				break;
			case "Rook":
				pieces[pawn.getIndex ( )] = new Rook ( x, y, color );
				break;
			case "Bishop":
				pieces[pawn.getIndex ( )] = new Bishop ( x, y, color );
				break;
			case "Knight":
				pieces[pawn.getIndex ( )] = new Knight ( x, y, color );
		}
		// update the piece's image
		String position = pawn.getPosition ( ).getStringPosition ( );
		String imagePath = pieces[pawn.getIndex ( )].getImagePath ( );
		Platform.runLater ( new Runnable ( )
		{
			@Override
			public void run( )
			{
				ChessDriver.updateImage ( position, imagePath );
			}
		} );
		return pieces[pawn.getIndex ( )];
	} // end promotePawn

	/**************************************************************************************
	 * getValidSquare gets a Square (x, y) from the user.
	 * 
	 * x is input using the letters (a - h) then converted to a number
	 * 
	 * @param nothing
	 * @return the valid square that the user chooses
	 */
	public static Square getValidSquare( )
	{
		Square square = new Square ( ); // the user selected square
		do
		{
			try
			{
				synchronized (ChessDriver.getClickedSquare ( ))
				{
					ChessDriver.getClickedSquare ( ).wait ( );
				}
			} catch ( Exception e )
			{
				e.printStackTrace ( );
			}
			square.setPosition ( ChessDriver.getClickedSquare ( ) );
		} while ( square.getX ( ) < 1 || square.getX ( ) > 8 || square.getY ( ) < 1 || square.getY ( ) > 8 );
		return square;
	} // end getValidSquare

	/*********************************************************************************
	 * findPieceOnSquare looks at all pieces and returns the one on given square
	 * 
	 * @param square
	 *           the square being examined
	 * @param pieces
	 *           the array of all pieces
	 * @return pieceOnSquare: the piece with the same position as square. null if no piece is found
	 */
	public static Piece getPieceOnSquare( Square square, Piece[ ] pieces )
	{
		Piece pieceOnSquare = null;
		for ( Piece p : pieces )
		{
			if ( square.sameSquare ( p.getPosition ( ) ) )
			{
				pieceOnSquare = p;
			}
		}
		return pieceOnSquare;
	} // end findPieceOnSquare

	/*********************************************************************************
	 * findPieceOnSquare looks at all pieces and returns the one on given square
	 * 
	 * @param x
	 *           the x coordinate position (1 - 8) shown as (a - h)
	 * @param y
	 *           the y coordinate position (1 - 8)
	 * @param pieces
	 *           the array of all pieces
	 * @return pieceOnSquare: the piece with the same position as square. null if no piece is found
	 */
	public static Piece getPieceOnSquare( int x, int y, Piece[ ] pieces )
	{
		Piece pieceOnSquare = null;
		for ( Piece p : pieces )
		{
			if ( p.getPosition ( ).sameSquare ( x, y ) )
			{
				pieceOnSquare = p;
			}
		}
		return pieceOnSquare;
	} // end findPieceOnSquare

	/***************************************************************************************
	 * isPieceOnSqure determines if there is a piece on the given square
	 * 
	 * overloaded method (has Square square instead of int x and int y)
	 * 
	 * @param square
	 *           the position in question
	 * @param pieces
	 *           the array of all pieces
	 * @return pieceOnSquare: true if there is a piece with the same position as square, false otherwise
	 */
	public static boolean isPieceOnSquare( Square square, Piece[ ] pieces )
	{
		boolean pieceOnSquare = false;
		for ( Piece p : pieces )
		{
			if ( square.sameSquare ( p.getPosition ( ) ) )
			{
				pieceOnSquare = true;
			}
		}
		return pieceOnSquare;
	} // end isPieceOnSquare

	/***************************************************************************************
	 * isPieceOnSqure determines if there is a piece on the given square
	 * 
	 * overloaded method (has int x and int y instead of Square square)
	 * 
	 * @param x
	 *           the x coordinate position (1 - 8) shown as (a - h)
	 * @param y
	 *           the y coordinate position (1 - 8)
	 * @param pieces
	 *           the array of all pieces
	 * @return pieceOnSquare: true if there is a piece with the same position as square, false otherwise
	 */
	public static boolean isPieceOnSquare( int x, int y, Piece[ ] pieces )
	{
		boolean pieceOnSquare = false;
		for ( Piece p : pieces )
		{
			if ( p.getPosition ( ).sameSquare ( x, y ) )
			{
				pieceOnSquare = true;
			}
		}
		return pieceOnSquare;
	} // end isPieceOnSquare

	/*****************************************************************************
	 * setStartingPosition creates all objects in pieces array, giving each a starting position and color
	 * 
	 * @param pieces
	 *           the array of all pieces
	 * @return nothing
	 */
	public static void setStartingPositions( Piece[ ] pieces )
	{
		// white pieces
		pieces[0] = new Rook ( 1, 1, white );
		pieces[1] = new Knight ( 2, 1, white );
		pieces[2] = new Bishop ( 3, 1, white );
		pieces[3] = new Queen ( 4, 1, white );
		pieces[4] = new King ( 5, 1, white );
		pieces[5] = new Bishop ( 6, 1, white );
		pieces[6] = new Knight ( 7, 1, white );
		pieces[7] = new Rook ( 8, 1, white );
		for ( int i = 8; i < 16; i++ )
		{
			pieces[i] = new Pawn ( i - 7, 2, white, i );
		}
		// black pieces
		pieces[16] = new Rook ( 1, 8, black );
		pieces[17] = new Knight ( 2, 8, black );
		pieces[18] = new Bishop ( 3, 8, black );
		pieces[19] = new Queen ( 4, 8, black );
		pieces[20] = new King ( 5, 8, black );
		pieces[21] = new Bishop ( 6, 8, black );
		pieces[22] = new Knight ( 7, 8, black );
		pieces[23] = new Rook ( 8, 8, black );
		for ( int i = 24; i < 32; i++ )
		{
			pieces[i] = new Pawn ( i - 23, 7, black, i );
		}
	} // end setStartingPosition

	/********************************************************************************
	 * copyBoard makes a new array of pieces that are all clones of their counterparts
	 * 
	 * @param pieces
	 *           the array of all pieces
	 * @return piecesCopy: the array of pieces identical to pieces
	 */
	public static Piece[ ] copyBoard( Piece[ ] pieces )
	{
		Piece[ ] piecesCopy = new Piece[STARTING_PIECES];
		Piece.Type type; // the piece type

		// cycle through all pieces, making a copy of each
		for ( int i = 0; i < pieces.length; i++ )
		{
			type = pieces[i].getType ( );
			// since pawns can promote during the game,
			// a switch is used to ensure the right kind of piece is being copied
			switch ( type )
			{
				case ROOK:
					piecesCopy[i] = new Rook ( pieces[i] );
					break;
				case KNIGHT:
					piecesCopy[i] = new Knight ( pieces[i] );
					break;
				case BISHOP:
					piecesCopy[i] = new Bishop ( pieces[i] );
					break;
				case QUEEN:
					piecesCopy[i] = new Queen ( pieces[i] );
					break;
				case KING:
					piecesCopy[i] = new King ( pieces[i] );
					break;
				case PAWN:
					piecesCopy[i] = new Pawn ( pieces[i] );
			}
		}
		return piecesCopy;
	} // end copyBoard

	/******************************************************************************
	 * clearEnPassant sets canGetEnPassant to false for each piece of the given color
	 * 
	 * @param playerTurn
	 *           the color of pawns that will be affected
	 * @param pieces
	 *           the array of all the pieces
	 * @return nothing
	 */
	public static void clearEnPassant( Piece.Color playerTurn, Piece[ ] pieces )
	{
		// clear WHITE's pawns of enPassant
		if ( playerTurn == white )
		{
			for ( int i = 8; i < 16; i++ )
			{
				if ( pieces[i].getType ( ) == Piece.Type.PAWN )
				{
					( (Pawn) pieces[i] ).setCanGetEnPassant ( false );
				}
			}
		}
		// clear BLACK's pawns of enPassant
		else
		{
			for ( int i = 24; i < 32; i++ )
			{
				if ( pieces[i].getType ( ) == Piece.Type.PAWN )
				{
					( (Pawn) pieces[i] ).setCanGetEnPassant ( false );
				}
			}
		}
	} // end clearEnPassant

	/******************************************************************************
	 * getAllLegalTakes get all of the positions the piece can capture on
	 * 
	 * @param pieces
	 *           the array of all pieces
	 * @return allLegalMoves: an arrayList of all positions the piece can make it to
	 */
	public static ArrayList<String> getAllLegalTakes( Piece[ ] pieces, Piece currPiece )
	{
		Square currSquare = new Square ( );
		ArrayList<String> allLegalTakes = new ArrayList<String> ( );
		// make sure the piece is not null
		if ( currPiece != null )
		{
			// cycle through all rows
			for ( int r = 1; r < 9; r++ )
			{
				// cycle through all columns
				for ( int c = 1; c < 9; c++ )
				{
					currSquare.setPosition ( c, r );
					// add all legal captures to the list
					if ( currPiece.hasLegalMove ( currSquare, pieces ) && isPieceOnSquare ( currSquare, pieces ) )
					{
						allLegalTakes.add ( c + "" + r );
					}
				}
			}
		}
		return allLegalTakes;
	} // end getAllLegalTakes

	/******************************************************************************
	 * getAllLegalMoves get all of the positions the piece can move (excluding captures)
	 * 
	 * the array will also include the piece's position since this is for the GUI
	 * 
	 * @param pieces
	 *           the array of all pieces
	 * @return allLegalMoves: an arrayList of all positions the piece can make it to
	 */
	public static ArrayList<String> getAllLegalMoves( Piece[ ] pieces, Piece currPiece )
	{
		Square currSquare = new Square ( );
		ArrayList<String> allLegalMoves = new ArrayList<String> ( );
		// make sure the piece is not null
		if ( currPiece != null )
		{
			// add the current piece's position first
			allLegalMoves.add ( currPiece.getPosition ( ).getStringPosition ( ) );
			// cycle through all rows
			for ( int r = 1; r < 9; r++ )
			{
				// cycle through all columns
				for ( int c = 1; c < 9; c++ )
				{
					currSquare.setPosition ( c, r );
					// add all legal moves to the list (not including capture moves)
					if ( currPiece.hasLegalMove ( currSquare, pieces ) && !isPieceOnSquare ( currSquare, pieces ) )
					{
						allLegalMoves.add ( c + "" + r );
					}
				}
			}
		}
		return allLegalMoves;
	} // end getAllLegalMoves

	public Piece.Color getPlayerTurn( )
	{
		return playerTurn;
	}

	public void setPlayerTurn( Piece.Color playerTurn )
	{
		this.playerTurn = playerTurn;
	}

	public static Square getRepick( )
	{
		return repick;
	}

	public String getEnding( )
	{
		return ending;
	}

	public void setEnding( String ending )
	{
		this.ending = ending;
	}

	public ArrayList<Piece[ ]> getPastPositions( )
	{
		return pastPositions;
	}

	public int getCaptureOrPawnMove( )
	{
		return captureOrPawnMove;
	}

	public void setCaptureOrPawnMove( int captureOrPawnMove )
	{
		this.captureOrPawnMove = captureOrPawnMove;
	}

	public Piece[ ] getPieces( )
	{
		return pieces;
	}
}