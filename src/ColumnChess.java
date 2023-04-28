import javafx.application.Platform;

public class ColumnChess extends Game
{
	@Override
	public void endMove( Piece[ ] pieces )
	{
		if ( getPlayerTurn ( ) == white )
		{
			setPlayerTurn ( black );
		}
		else
		{
			setPlayerTurn ( white );
			// shift all pieces to the left
			shiftToLeft ( pieces );
		}
	}

	/**************************************************************************
	 * shiftToLeft shifts all pieces one to the left (flipping to the other side)
	 * 
	 * @param pieces
	 *           the array of all the pieces
	 * @return nothing
	 */
	public void shiftToLeft( Piece[ ] pieces )
	{
		int newX; // each piece's new x coordinate
		for ( int i = 0; i < pieces.length; i++ )
		{
			newX = pieces[i].getPosition ( ).getX ( ) - 1;
			if ( newX == 0 )
			{
				newX = 8;
			}
			// update position on the GUI
			String oldPosition = pieces[i].getPosition ( ).getStringPosition ( );
			pieces[i].setPosition ( newX, pieces[i].getPosition ( ).getY ( ) ); // set the piece's new position
			String newPosition = pieces[i].getPosition ( ).getStringPosition ( );
			Platform.runLater ( new Runnable ( )
			{
				@Override
				public void run( )
				{
					ChessDriver.movePiece ( oldPosition, newPosition );
				}
			} );
		}
	} // end shiftToLeft
}
