package pieces;

import java.util.ArrayList;

import ai.ComSettings;
import ai.ComputerMove;
import game.Square;
import game.StandardGame;

/**
 * @author John Hoffmann
 * 
 *         Piece establishes key parts that every type of piece has
 */
public abstract class Piece implements Comparable<Piece> {

	// enumeration for white and black pieces
	public static enum Color {
		WHITE("white"), BLACK("black");

		public String name; // the color in string form

		Color(String name) {
			this.name = name;
		}

		public static Color getOppColor(Color color) {
			return (color == WHITE) ? BLACK : WHITE;
		}
	}

	// enumeration for types of pieces
	public static enum Type {
		KING("king", 0.0), QUEEN("queen", 9.0), ROOK("rook", 5.0), BISHOP("bishop", 3.0), KNIGHT("knight", 3.0),
		PAWN("pawn", 1.0);

		public String name; // first initial (Knight is "N" so that it's different than King, "K")
		public double worth; // the piece's initial point worth

		Type(String name, double worth) {
			this.name = name;
			this.worth = worth;
		}
	}

	private String name; // represented by two letters i.e "WK" for white king
	private Type type; // type of piece
	private Color color; // must be white or black
	private Square position; // coordinates (x, y)
	private String imagePath; // the path to the piece's image
	// (for computer) the point worth of the piece based on it's position and what
	// empty squares it can see
	private double positionScore;
	// (for computer) the point worth of the piece based on what pieces it is
	// supporting and threatening
	private double observationScore;

	// default constructor
	public Piece() {
		setPosition(null);
		color = null;
		type = null;
		name = "!!";
		positionScore = 0.0;
		observationScore = 0.0;
	}

	/*************************************************************************************
	 * Piece constructor
	 * 
	 * @param x     the x coordinate position (1 - 8) shown as (a - h)
	 * @param y     the y coordinate position (1 - 8)
	 * @param color the color of the piece (WHITE or BLACK)
	 * @param type  the type of piece (KING - PAWN)
	 */
	public Piece(int x, int y, Color color, Type type) {
		position = new Square(x, y);
		this.color = color;
		this.type = type;
		this.name = ((color == Color.WHITE) ? "W" : "B") + type.name.substring(0, 1).toUpperCase();
		imagePath = "resources\\" + color.name + type.name + ".png";
		positionScore = type.worth;
		observationScore = 0.0;
	} // end Piece constructor

	/************************************************************************************
	 * Piece constructor
	 * 
	 * @param pieceCopy the piece that is being copied
	 */
	public Piece(Piece pieceCopy) {
		name = pieceCopy.getName();
		type = pieceCopy.getType();
		color = pieceCopy.getColor();
		position = new Square(pieceCopy.getPosition());
		positionScore = pieceCopy.getPositionScore();
		observationScore = pieceCopy.getObservationScore();
	} // end Piece constructor

	/*****************************************************************************************
	 * notCheck makes sure that a desired move doesn't put the player's king in
	 * danger
	 * 
	 * @param destinationSquare the square this piece is wanting to go to
	 * @param pieces            the arrayList of all pieces
	 * @return true if the move does not put the king in danger, false otherwise
	 */
	public boolean notCheck(Square destinationSquare, ArrayList<Piece> pieces) {
		// get the enemy king
		Piece enemyKing = null;
		for (Piece p : pieces) {
			if (p.type == Type.KING && p.color != color) {
				enemyKing = p;
			}
		}
		// copy the board so that the desired move can be tested
		ArrayList<Piece> futurePieces = StandardGame.copyBoard(pieces);
		// test the move using a copy of the current board
		StandardGame.testMove(StandardGame.getPieceOnSquare(position, futurePieces), destinationSquare, futurePieces);
		// if the enemy king would be captured, it would be legal
		if (!futurePieces.contains(enemyKing)) {
			return true;
		}
		// see if the desired move puts the king in danger
		if (StandardGame.inCheck(futurePieces, color)) {
			return false;
		}
		return true;
	} // end notCheck

	/*************************************************************************************
	 * canTake determines if the piece on destinationSquare is of the same color or
	 * not
	 * 
	 * @param destinationSquare the square the piece is trying to get to
	 * @param pieces            the arrayList of all the pieces
	 * @return true if there is either no piece on destinationSquare or it has an
	 *         opposite color, false otherwise
	 */
	public boolean canTake(Square destinationSquare, ArrayList<Piece> pieces) {
		Piece capturePiece = StandardGame.getPieceOnSquare(destinationSquare, pieces);
		if (capturePiece != null && capturePiece.getColor() == color) {
			return false;
		}
		return true;
	} // end canTake

	/************************************************************************************************
	 * notBlocked determines if any squares between two given squares are occupied
	 * 
	 * @param startSquare       the startSquare
	 * @param destinationSquare the square the piece is trying to get to
	 * @param pieces            the arrayList of all pieces
	 * @return notBlocked: true if there are no pieces in the way
	 */
	public boolean notBlocked(Square startSquare, Square destinationSquare, ArrayList<Piece> pieces) {
		// setting the directions
		// ( ( destination > start ) ? 1 : -1 ) makes the direction positive or negative
		int xDirection = (destinationSquare.getX() > startSquare.getX()) ? 1 : -1;
		// ( destination == start ) keeps the row / column same if the row / column
		// doesn't change
		if (destinationSquare.getX() == startSquare.getX()) {
			xDirection = 0;
		}
		int yDirection = (destinationSquare.getY() > startSquare.getY()) ? 1 : -1;
		if (destinationSquare.getY() == startSquare.getY()) {
			yDirection = 0;
		}
		// start the search with the next square along search path
		int x = startSquare.getX() + xDirection; // current x position
		int y = startSquare.getY() + yDirection; // current y position

		// look at every square in between
		try {
			while (!destinationSquare.sameSquare(x, y)) {
				if (StandardGame.isPieceOnSquare(x, y, pieces)) {
					return false;
				}
				// look at next square
				x += xDirection;
				y += yDirection;
				// error if search exceeds the board
				if (Math.abs(x) + Math.abs(y) > 16) {
					throw new Exception("Searching outside of board during notBlocked.");
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	} // end notBlocked

	/***********************************************************************************
	 * getControlledSquares returns an arrayList of squares that the piece can reach
	 * 
	 * @param pieces the arrayList of all pieces
	 * @return controlledSquares: the arrayList of all squares the piece can reach
	 */
	public ArrayList<Square> getControlledSquares(ArrayList<Piece> pieces) {
		ArrayList<Square> controlledSquares = new ArrayList<Square>();
		Square currSquare = new Square(); // the current square
		// check each square on the board
		// check each column
		for (int c = 1; c < 9; c++) {
			// check each row
			for (int r = 1; r < 9; r++) {
				currSquare.setPosition(c, r);
				// make sure the piece can see the square
				if (canSee(currSquare, pieces)) {
					// add the controlled square
					controlledSquares.add(new Square(c, r));
				}
			}
		}
		return controlledSquares;
	} // end getControlledSquares

	/***********************************************************************************
	 * calculatePositionScore calculates the piece's worth based on its' position
	 * and what empty squares it sees
	 * 
	 * @param pieces the arrayList of all pieces
	 * @return nothing
	 */
	public void calculatePositionScore(ArrayList<Piece> pieces, ComSettings difficulty, ComputerMove currMove) {
		double worth = 0.0; // assume the piece is not on the board first
		double worthBefore = positionScore; // save what the piece is worth before
		double difference; // the difference between worth and worthBefore

		// start with the piece's starting worth
		worth += type.worth;
		// get the squares this piece can reach
		ArrayList<Square> controlledSquares = getControlledSquares(pieces);
		// look at each square this piece can reach
		double columnCenter; // column length away from the center of the board
		double rowCenter; // row length away from the center of the board
		double offCenter; // how close the square is to the center of the board
		// add worth for every controlled square
		for (Square s : controlledSquares) {
			// get distance from the center (0.5, 1.5, 2.5, or 3.5)
			// adding 0.5 makes the distance 1, 2, 3, or 4
			columnCenter = (Math.abs(s.getX() - 4.5)) + 0.5;
			rowCenter = (Math.abs(s.getY() - 4.5) + 0.5);
			// add the distances to get 2 (close to center) through 8 (far from center)
			// subtracting 8 and taking absolute value makes the distance 0 (far from
			// center) through 6 (close to
			// center)
			offCenter = Math.abs(columnCenter + rowCenter - 8);
			// award points for being closer to the center of the board
			worth += (offCenter * difficulty.getCenterMagnet());
		}
		// add worth for the piece's current position
		worth += getSquareComfort(position, pieces, difficulty);
		// save to reasoning if there is a difference
		difference = Math.round((worth - worthBefore) * 100.0) / 100.0;
		if (difference > 0.01 || difference < -0.01) {
			currMove.getAddReason().println(this + ": " + difference);
		}
		positionScore = Math.round(worth * 100.0) / 100.0;
	} // end calculatePositionScore

	/************************************************************************************
	 * calculateObservationScore calculates the piece's worth based on what pieces
	 * it support or threatens
	 * 
	 * the score based on other pieces is done separately so that it doesn't affect
	 * the total point worth
	 * 
	 * @param pieces the arrayList of all the pieces
	 * @return nothing
	 */
	public void calculateObservationScore(ArrayList<Piece> pieces, ComSettings difficulty, ComputerMove currMove) {
		double worth = 0.0; // assume the piece is not on the board
		double worthBefore = observationScore; // save what the piece is worth before
		double difference; // the difference between worth and worthBefore

		// get the squares this piece can reach
		ArrayList<Square> controlledSquares = getControlledSquares(pieces);
		// look at each square this piece can reach
		Piece observedPiece; // the piece on the square being observed
		for (Square s : controlledSquares) {
			observedPiece = StandardGame.getPieceOnSquare(s, pieces);
			// do nothing if there is no piece on the square
			if (observedPiece == null) {
				// do nothing
			}
			// supporting a piece
			else if (observedPiece.getColor() == getColor()) {
				// award points for supporting a piece (defense)
				worth += 0.0;
			}
			// threatening a piece
			else {
				// award points for threatening a piece (offense)
				worth += 0.0;
			}
		}
		// save to reasoning if there is a difference
		difference = Math.round((worth - worthBefore) * 100.0) / 100.0;
		if (difference > 0.01 || difference < -0.01) {
			currMove.getAddReason().println(this + ": " + difference);
		}
		observationScore = Math.round(worth * 100.0) / 100.0;
	} // end calculateObservationScore

	/*****************************************************************************************
	 * getPointWorth is the position score plus the observation score of the piece
	 * 
	 * @param nothing
	 * @return total score: positionScore + observationScore
	 */
	public double getPointWorth() {
		return (positionScore + observationScore);
	} // end getPointWorth

	@Override
	/**
	 * checks type, color, and position
	 */
	public boolean equals(Object other) {
		if (!(other instanceof Piece)) {
			return false;
		}
		Piece o = (Piece) other;
		if (type == o.type && color == o.color && position.equals(o.position)) {
			return true;
		}
		return false;
	}

	@Override
	/**
	 * compares positions only
	 */
	public int compareTo(Piece piece) {
		return position.compareTo(piece.getPosition());
	}

	/**************************************************************************************
	 * getSquareComfort determines how much the piece wants to be on the square it
	 * is on
	 * 
	 * @param currPosition the square that the piece is considering moving to
	 * @param pieces       the array of all pieces
	 * @return score: the amount of points going to this square awards (or loses)
	 */
	abstract double getSquareComfort(Square currPosition, ArrayList<Piece> pieces, ComSettings difficulty);

	/************************************************************************************
	 * getPieceHesitation determines how much the piece wants to move
	 * 
	 * @param pieces   the array of all pieces
	 * @param currMove the current ComputerMove (with the destination square)
	 * @return hesitation: a positive (wants to move) or negative (doesn't want to
	 *         move) score
	 */
	public abstract double getPieceHesitation(StandardGame game, ComputerMove currMove, ComSettings difficulty);

	/*************************************************************************************
	 * hasLegalMove determines if a piece move to a destination
	 * 
	 * for actual moves
	 * 
	 * @param destinationSquare the square the piece is trying to go
	 * @param pieces            the array of all the pieces
	 * @return true if the piece can move to destinationSquare, false otherwise
	 */
	public abstract boolean hasLegalMove(Square destinationSquare, ArrayList<Piece> pieces);

	/****************************************************************************************
	 * canSupport determines if the piece can reach the supportSquare
	 * 
	 * for determining support and threatening pieces, only squares a piece can
	 * theoretically take on
	 * 
	 * @param supportSquare the square the piece is trying to support
	 * @param pieces        the array of all the pieces
	 * @return true if the piece can reach supportSquare, false otherwise
	 */
	public abstract boolean canSupport(Square supportSquare, ArrayList<Piece> pieces);

	/******************************************************************************************
	 * canSee determines if the piece can see the sightSquare
	 * 
	 * for piece controlled squares, only squares a piece could take on
	 * 
	 * @param sightSquare the square the piece is trying to see
	 * @param pieces      the array of all the pieces
	 * @return true if the piece can reach the sightSquare, false otherwise
	 */
	abstract boolean canSee(Square sightSquare, ArrayList<Piece> pieces);

	// getters and setters
	public String getName() {
		return name;
	}

	public Square getPosition() {
		return position;
	}

	public void setPosition(Square newPosition) {
		position.setX(newPosition.getX());
		position.setY(newPosition.getY());
	}

	public void setPosition(int x, int y) {
		position.setX(x);
		position.setY(y);
	}

	public Color getColor() {
		return color;
	}

	public Type getType() {
		return type;
	}

	public double getPositionScore() {
		return positionScore;
	}

	public double getObservationScore() {
		return observationScore;
	}

	public String getImagePath() {
		return imagePath;
	}

	@Override
	public String toString() {
		return "[" + type + " on " + position + "]";
	}

}
