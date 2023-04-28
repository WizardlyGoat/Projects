
/**
 * @author John Hoffmann
 * 
 *         ChessThread handles everything outside of the actual game (like picking which type of game)
 */
public class ChessThread extends Thread

{
	public void run( )
	{
		Thread.currentThread ( ).setName ( "Chess Thread" );
		// assume a regular game is being played
		Game game = new Game ( );
		game.startGame ( );
	} // end run

	// methods for printing the board in the console

	// /*****************************************************************************
	// * printBoard prints or reprints the chess board
	// *
	// * @param pieces
	// * the array of all pieces
	// * @param playerTurn
	// * the player's turn (WHITE or BLACK)
	// * @return nothing
	// */
	// public static void printBoard( Piece[ ] pieces, Piece.Color playerTurn )
	// {
	// System.out.println ( );
	// for ( int row = 8; row > 0; row-- )
	// {
	// printLine ( );
	// printDottedLine ( row );
	// printSpaces ( pieces, row, playerTurn );
	// printDottedLine ( row );
	// }
	// printLine ( );
	// printColumnLetters ( playerTurn );
	// } // end printBoard
	//
	// /*****************************************************************************
	// * printSpaces for printBoard, prints the lines with piece names or blanks if unoccupied
	// *
	// * @param pieces
	// * the array of all pieces
	// * @param row
	// * the row number (1-8)
	// * @return nothing
	// */
	// public static void printSpaces( Piece[ ] pieces, int row, Piece.Color playerTurn )
	// {
	// // flip board if it's black's turn
	// if ( playerTurn == black )
	// {
	// row = Math.abs ( row - 9 );
	// }
	// int column; // the column (a - h) represented as (1 - 8)
	// Square currSquare = new Square ( ); // square being examined
	// Piece currPiece; // the piece on currSquare
	// String shadeSpace; // light or dark square
	//
	// System.out.print ( " " + row + " " );
	// for ( int c = 1; c < 9; c++ )
	// {
	// // flip board if it's black's turn
	// if ( playerTurn == black )
	// {
	// column = Math.abs ( ( c - 9 ) );
	// }
	// else
	// {
	// column = c;
	// }
	// String name = "";
	// currSquare.setPosition ( column, row ); // current square
	// shadeSpace = getShadeSpace ( currSquare ); // light or dark square
	//
	// // get name of currPiece
	// currPiece = getPieceOnSquare ( currSquare, pieces );
	// if ( currPiece != null )
	// {
	// name = currPiece.getName ( );
	// }
	// System.out.printf ( "%s%s %2s %s", BORDER, shadeSpace, name, shadeSpace );
	// }
	// System.out.println ( BORDER );
	// } // end printSpaces
	//
	// /*****************************************************************************
	// * printLine for printBoard, prints solid lines between rows
	// *
	// * @param nothing
	// * @return nothing
	// */
	// public static void printLine( )
	// {
	// System.out.print ( " " );
	// for ( int i = 0; i < 57; i++ )
	// {
	// System.out.print ( BORDER );
	// }
	// System.out.println ( );
	// } // end printLine
	//
	// /*****************************************************************************
	// * printDottedLine for printBoard, prints the dotted lines between lines and piece names also prints shade
	// *
	// * @param row
	// * the row number (1 - 8)
	// * @return nothing
	// */
	// public static void printDottedLine( int row )
	// {
	// Square currSquare = new Square ( ); // the current square
	// String shadeSpace; // light or dark squares
	// System.out.print ( " " + BORDER );
	// for ( int column = 1; column < 9; column++ )
	// {
	// currSquare.setPosition ( column, row );
	// shadeSpace = getShadeSpace ( currSquare );
	// for ( int i = 0; i < 6; i++ )
	// {
	// System.out.print ( shadeSpace );
	// }
	// System.out.print ( BORDER );
	// }
	// System.out.println ( );
	// } // end printDottedLine
	//
	// /*****************************************************************************
	// * printColumnLetters for printBoard, prints the column letters at the bottom of the board
	// *
	// * @param playerTurn
	// * the player's turn (WHITE or BLACK)
	// * @return nothing
	// */
	// public static void printColumnLetters( Piece.Color playerTurn )
	// {
	// System.out.print ( " " );
	// if ( playerTurn == white )
	// {
	// for ( char c = 'a'; c < 'i'; c++ )
	// {
	// System.out.printf ( "%5c ", c );
	// }
	// }
	// else
	// {
	// for ( char c = 'h'; c > '`'; c-- )
	// {
	// System.out.printf ( "%5c ", c );
	// }
	// }
	// System.out.println ( );
	// } // end printColumnLetters
	//
	// /********************************************************************************
	// * getShadeSpace for printing the board, determines if the square is light or dark
	// *
	// * @param square
	// * the coordinates of the current square
	// * @return shadeSpace: a string SHADE or " "
	// */
	// public static String getShadeSpace( Square square )
	// {
	// String shadeSpace;
	// if ( square.isDark ( ) )
	// {
	// shadeSpace = SHADE;
	// }
	// else
	// {
	// shadeSpace = " ";
	// }
	// return shadeSpace;
	// } // end getShadeSpace
	//
	// public Piece.Color getPlayerTurn( )
	// {
	// return playerTurn;
	// }
}
