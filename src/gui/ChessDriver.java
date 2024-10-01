package gui;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import game.ChessThread;
import game.EndGameCheck.Ending;
import gui.nodes.ImageButton;
import gui.nodes.PawnPromoteBox;
import gui.nodes.Rank;
import gui.nodes.Square;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pieces.Piece;
import pieces.Piece.Color;
import pieces.Piece.Type;

/**
 * @author John Hoffmann
 * 
 *         Chess: A strategy game involving 32 pieces on an 8x8 board
 * 
 *         Goal: Keep your king safe while trapping the opponent's king
 * 
 *         This class starts the program and handles the GUI
 * 
 */
public class ChessDriver extends Application {

	// communication with game
	private Communication communication = new Communication();
	private Board board;

	public static void main(String[] args) {
		launch(args); // calls start
	}

	@Override
	public void start(Stage primaryStage) {
		// separate thread for the back end
		Thread.currentThread().setName("FX Thread");
		ChessThread chessThread = new ChessThread(this);

		// set the application's title
		primaryStage.setTitle("Chess");

		// setup the menu and board
		Menu menu = new Menu(500, 300);
		board = new Board((Style.SQUARE_LENGTH * 10), (Style.SQUARE_LENGTH * 10));

		// event handler for the play button
		EventHandler<ActionEvent> playHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				// initialize the AI
				communication.setPlayingAI(
						((Button) ae.getTarget()).getText().equals(Menu.getPlayhumantext()) ? false : true);
				if (communication.isPlayingAI()) {
					communication.initializeAI(menu);
				}
				// set the board scene
				primaryStage.setScene(board.getBoardScene());
				// notify chessThread that the game is starting
				synchronized (communication) {
					communication.notify();
				}
			}
		};
		menu.getPlay().setOnAction(playHandler);
		menu.getPlayComputer().setOnAction(playHandler);

		// handler for the board, getting the square location of any click
		EventHandler<MouseEvent> boardHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				Node node = ((Node) me.getTarget()).getParent();
				// a square was clicked
				if (node instanceof Square) {
					communication.getClickedSquare().setPosition((Square) node);
				}
				// something other than a square was clicked
				else {
					communication.getClickedSquare().setPosition(0, 0);
				}
				// notify chess thread that a square was clicked
				synchronized (communication) {
					communication.notify();
				}
			}
		};
		// handler for when the mouse enters a square
		EventHandler<MouseEvent> hoverHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				Node node = (Node) me.getTarget();
				if (node instanceof Square) {
					((Square) node).applyHover();
				}
			}
		};
		// handler for when the mouse exits a square
		EventHandler<MouseEvent> exitHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				Node node = (Node) me.getTarget();
				if (node instanceof Square) {
					((Square) node).removeHover(board);
				}
			}
		};
		for (Square s : board.getSquares()) {
			s.setOnMouseClicked(boardHandler);
			s.setOnMouseEntered(hoverHandler);
			s.setOnMouseExited(exitHandler);
		}

		// set the scene to the menu scene and show it
		primaryStage.setScene(menu.getMenuScene());
		primaryStage.show();
		// start the chess thread
		chessThread.start();
	}

	/**
	 * essentially converts a game.Square into a gui.Square
	 * 
	 * @param square the game.Square used to find the gui.Square
	 * @return the equivalent gui.Square
	 */
	@SuppressWarnings("unlikely-arg-type")
	public Square getSquareAtPosition(game.Square square) {
		for (Square s : board.getSquares()) {
			if (s.equals(square)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * adds all given pieces to the gui board
	 * 
	 * @param pieces the ArrayList<Piece> of all pieces
	 */
	public void addStartingPieces(ArrayList<Piece> pieces) {
		Square square;
		for (Piece p : pieces) {
			square = getSquareAtPosition(p.getPosition());
			try {
				square.setImageView(Resources.getImagePath(p.getColor(), p.getType()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * sets constraints on each board node according to playerTurn
	 * 
	 * @param playerTurn the player's color
	 */
	public void setPerspective(Color playerTurn) {
		// squares
		for (Square s : board.getSquares()) {
			// white perspective
			if (playerTurn == Color.WHITE) {
				GridPane.setConstraints(s, s.getC(), s.getReverseR());
			}
			// black perspective
			else {
				GridPane.setConstraints(s, s.getReverseC(), s.getR());
			}
		}
		// ranks
		for (Rank r : board.getRanks()) {
			// rows
			if (r.getR() > 0) {
				GridPane.setConstraints(r, 0, (playerTurn == Color.WHITE) ? r.getReverseR() : r.getR());
			}
			// columns
			else if (r.getC() > 0) {
				GridPane.setConstraints(r, (playerTurn == Color.WHITE) ? r.getC() : r.getReverseC(), 9);
			}
		}
	}

	/**
	 * moves a piece's image from startingSquare to destination square
	 * 
	 * @param startingPostion   the piece's current game.Square
	 * @param destinationSquare the piece's destination game.Square
	 */
	public void movePiece(game.Square startingSquare, game.Square destinationSquare) {
		Square start = getSquareAtPosition(startingSquare);
		Square destination = getSquareAtPosition(destinationSquare);

		ImageView imageView = new ImageView(start.getImageView().getImage());
		start.clearImage();
		destination.setImageView(imageView);

		// apply past move effects
		start.applyPastMove();
		board.getPastMoves().add(start);
		destination.applyPastMove();
		board.getPastMoves().add(destination);
	}

	/**
	 * removes a piece's image from the board (used solely for enPassant, movePiece
	 * takes care of usual captures)
	 * 
	 * @param square the game.Square of the piece being removed
	 */
	public void removeImageAtPosition(game.Square square) {
		getSquareAtPosition(square).clearImage();
	}

	/**
	 * applies legal take effects to all equivalent gui.squares
	 * 
	 * @param positions the ArrayList<Piece> of game.Squares
	 */
	public void addLegalTakeEffects(ArrayList<game.Square> positions) {
		Square currSquare;
		for (game.Square s : positions) {
			currSquare = getSquareAtPosition(s);
			currSquare.applyLegalTake();
			board.getPossibleTakes().add(currSquare);
		}
	}

	/**
	 * applies legal move effects to all equivalent gui.squares
	 * 
	 * @param positions the ArrayList<Piece> of game.Squares
	 */
	public void addLegalMoveEffects(ArrayList<game.Square> positions) {
		Square currSquare;
		for (game.Square s : positions) {
			currSquare = getSquareAtPosition(s);
			currSquare.applyLegalMove();
			board.getPossibleMoves().add(currSquare);
		}
	}

	/**
	 * takes away all legal move and legal take effects
	 */
	public void clearLegalMoveEffects() {
		// clear possible moves
		for (Square s : board.getPossibleMoves()) {
			s.applyInitEffects();
		}
		board.getPossibleMoves().clear();

		// clear possible takes
		for (Square s : board.getPossibleTakes()) {
			s.applyInitEffects();
		}
		board.getPossibleTakes().clear();

		// add past move effects back
		for (Square s : board.getPastMoves()) {
			s.applyPastMove();
		}
	}

	/**
	 * clears the current past move effects
	 */
	public void clearPastMoveEffects() {
		for (Square s : board.getPastMoves()) {
			s.applyInitEffects();
		}
		board.getPastMoves().clear();
	}

	/**
	 * creates a pawn promotion box for the user to choose a piece to promote to
	 * 
	 * @param playerTurn the player's color
	 */
	public void addPromotePawnBox(Color playerTurn) {
		// event handler for all buttons in pawn promote box
		EventHandler<MouseEvent> pawnPromoteHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent ae) {
				Node node = (Node) ae.getTarget();
				if (node instanceof ImageButton) {
					communication.setPromoteTo(((ImageButton) node).getType());
					board.removePawnPromoteBox();
				}
				// notify chessThread that an option was selected
				synchronized (communication) {
					communication.notify();
				}
			}
		};
		PawnPromoteBox pawnPromote = board.createPawnPromoteBox(playerTurn);
		for (ImageButton b : pawnPromote.getButtons()) {
			b.setOnMouseClicked(pawnPromoteHandler);
		}
	}

	/**
	 * sets the given square's image to the corresponding piece (used solely for
	 * pawn promotion)
	 * 
	 * @param position the square whose image is updating
	 * @param type     the new image's piece type
	 * @param color    the new image's piece color
	 */
	public void updateImage(game.Square position, Type type, Color color) {
		String imagePath = Resources.getImagePath(color, type);
		Square square = getSquareAtPosition(position);
		try {
			square.setImageView(imagePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * displays the end game message
	 * 
	 * @param ending the ending type for the game
	 */
	public void displayEndGame(Ending ending) {
		board.addEndGameLabel(ending.message);
	}

	// getters and setters
	public Communication getCommunication() {
		return communication;
	}
}
