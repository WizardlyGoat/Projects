import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 
 * @author John Hoffmann
 * 
 *         ComputerMove holds the key parts of a single move: the piece, it's destinations square, and it's score
 *
 */
public class ComputerMove
{

	private Piece piece; // the piece that is being moved
	private Square destinationSquare; // the movePiece's valid destination
	private double score; // the move's score
	private StringWriter reasoning;
	private PrintWriter addReason;

	// default constructor
	public ComputerMove()
	{
		piece = null;
		destinationSquare = null;
		score = 0.0;
		reasoning = new StringWriter ( );
		addReason = new PrintWriter ( reasoning );
	}

	/***************************************************************************
	 * ComputerMove constructor
	 * 
	 * @param piece
	 *           the piece that is being moved
	 * @param destinationSquare
	 *           the square that piece is being moved to
	 */
	public ComputerMove(Piece piece, Square destinationSquare)
	{
		this.piece = piece;
		this.destinationSquare = destinationSquare;
		score = 0.0;
		reasoning = new StringWriter ( );
		addReason = new PrintWriter ( reasoning );
	} // end ComputerMove constructor

	/***************************************************************************
	 * addToScore adds points to the current score
	 * 
	 * @param points
	 *           the double amount of points being added to score
	 * @return nothing
	 */
	public void addToScore( double points )
	{
		score += points;
		// round the score
		score = Math.round ( score * 100.0 ) / 100.0;
	} // end addToScore

	/****************************************************************************
	 * printReasoning prints everything written in reasoning
	 * 
	 * @param nothing
	 * @return nothing
	 */
	public void printReasoning( )
	{
		System.out.println ( "\n" + reasoning );
	} // end printReasoning

	// setters and getters
	public Piece getPiece( )
	{
		return piece;
	}

	public void setPiece( Piece piece )
	{
		this.piece = piece;
	}

	public Square getDestinationSquare( )
	{
		return destinationSquare;
	}

	public void setDestinationSquare( Square destinationSquare )
	{
		this.destinationSquare = destinationSquare;
	}

	public double getScore( )
	{
		return score;
	}

	public void setScore( double score )
	{
		this.score = score;
	}

	public StringWriter getReasoning( )
	{
		return reasoning;
	}

	public PrintWriter getAddReason( )
	{
		return addReason;
	}

	@Override
	public String toString( )
	{
		return "ComputerMove [" + piece.getType ( ) + " to " + destinationSquare + ", Score=" + score + "]";
	}

}
