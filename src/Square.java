/**
 * @author John Hoffmann
 * 
 *         This class is used to represent squares on the board, a single x and y coordinate
 */
public class Square
{
	public static Square removed = new Square ( 0, 0 ); // a global square for dead pieces
	private int x; // the x coordinate position (1 - 8) shown as (a - h)
	private int y; // the y coordinate position (1 - 8)

	// default constructor
	public Square()
	{
		x = 0;
		y = 0;
	}

	/**************************************************************************************
	 * Square constructor
	 * 
	 * @param x
	 *           the x coordinate position (1 - 8) shown as (a - h)
	 * @param y
	 *           the y coordinate position (1 - 8)
	 */
	public Square(int x, int y)
	{
		this.x = x;
		this.y = y;
	} // end Square constructor

	/**************************************************************************************
	 * Square constructor
	 * 
	 * @param square
	 *           the square being copied
	 */
	public Square(Square square)
	{
		x = square.getX ( );
		y = square.getY ( );
	} // end Square constructor

	/**************************************************************************************
	 * sameSquare determines if the given square is the same as the this square
	 * 
	 * @param square
	 *           the square being compared to
	 * @return true if they are the same square, false otherwise
	 */
	public boolean sameSquare( Square square )
	{
		if ( square.getX ( ) == x && square.getY ( ) == y )
		{
			return true;
		}
		else
		{
			return false;
		}
	} // end sameSquare

	/**************************************************************************************
	 * sameSquare determines if the given coordinates are the same as this square's coordinates
	 * 
	 * @param x
	 *           the x coordinate position (1 - 8) shown as (a - h)
	 * @param y
	 *           the y coordinate position (1 - 8)
	 * @return true if they are the same square, false otherwise
	 */
	public boolean sameSquare( int x, int y )
	{
		if ( this.x == x && this.y == y )
		{
			return true;
		}
		else
		{
			return false;
		}
	} // end sameSquare

	/***************************************************************************************
	 * isDark determines if this square is light or dark
	 * 
	 * @param nothing
	 * @return true if dark, false if light
	 */
	public boolean isDark( )
	{
		if ( ( x + y ) % 2 == 0 )
		{
			return true;
		}
		else
		{
			return false;
		}
	} // end isDark

	// getters and setters
	public void setX( int x )
	{
		this.x = x;
	}

	public void setY( int y )
	{
		this.y = y;
	}

	public void setPosition( int x, int y )
	{
		this.x = x;
		this.y = y;
	}

	public void setPosition( Square square )
	{
		x = square.getX ( );
		y = square.getY ( );
	}

	public int getX( )
	{
		return x;
	}

	public char getXCharacter( int x )
	{
		return (char) ( x + 96 );
	}

	public int getY( )
	{
		return y;
	}

	public String getStringPosition( )
	{
		if ( x == 0 )
		{
			return "removed";
		}
		else
		{
			return x + "" + y;
		}
	}

	@Override
	public String toString( )
	{
		if ( x == 0 )
		{
			return "removed";
		}
		else
		{
			return getXCharacter ( x ) + "" + y;
		}
	}
}
