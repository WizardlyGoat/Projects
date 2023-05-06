package gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import game.ChessThread;
import game.Square;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import pieces.Piece;
import test.Test;

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
	// sizes
	private static final int REC_LENGTH = 65; // length of each square
	private static final double PIECE_SIZE = 0.95; // how the piece fits in a square
	// colors
	private static final Color LIGHT = Color.FLORALWHITE; // light squares
	private static final Color DARK = Color.DIMGREY; // dark squares
	private static final Color BACKGROUND = Color.DARKSLATEGREY; // for any background
	private static final Color PAST_MOVE = Color.ROYALBLUE; // for the two squares that make up the last move
	private static final Color LEGAL_MOVE = Color.SEAGREEN; // all squares a piece can move to
	private static final Color LEGAL_TAKE = Color.DARKRED; // all squares a piece can take on
	// effects
	private static final Glow BRIGHT_GLOW = new Glow(1.0); // 0.0 - 1.0
	private static final Glow MEDIUM_GLOW = new Glow(0.7);
	private static final Glow DULL_GLOW = new Glow(0.5);
	private static final InnerShadow HOVER = new InnerShadow();
	// a list of rectangles that make up the past move
	private static ArrayList<String> pastMoveIds = new ArrayList<String>();
	// lists of rectangles that make up possible moves
	private static ArrayList<String> possibleMoveIds = new ArrayList<String>();
	private static ArrayList<String> possibleTakeIds = new ArrayList<String>();
	// communication with chess thread
	private static Square clickedSquare = new Square();
	private static String difficulty = "";
	private static String playAs;
	private static String promoteTo;
	// panes and scenes
	private static VBox menuRoot = new VBox();
	private static Scene menuScene = new Scene(menuRoot, 500, 300);
	private static StackPane boardRoot = new StackPane();
	private static GridPane boardGrid = new GridPane();
	private static HBox pawnPromoteBox = new HBox();
	private static HBox endGameBox = new HBox();
	private static Scene boardScene;
	// buttons for pawn promotion
	private static Button queenButton = new Button();
	private static Button rookButton = new Button();
	private static Button bishopButton = new Button();
	private static Button knightButton = new Button();
	// label for the end of the game message
	private static Label endGameLabel = new Label();
	// event handlers
	private static EventHandler<MouseEvent> boardHandler;

	public static void main(String[] args) {
		launch(args); // calls start
	} // end main

	@Override
	public void start(Stage primaryStage) {
		Thread.currentThread().setName("FX Thread");
		ChessThread chessThread = new ChessThread();
		primaryStage.setTitle("Chess"); // set the application's title
		// border pane for the computer menu screen
		VBox comRoot = new VBox();
		// grid pane for the chess board
		boardRoot.getChildren().add(boardGrid);
		int boardLength = (REC_LENGTH * 8) + (REC_LENGTH * 2); // stage dimensions
		boardScene = new Scene(boardRoot, boardLength, boardLength);

		// setup the menu
		Button play = new Button("Play"); // play against human
		play.setPrefSize(REC_LENGTH, REC_LENGTH / 2);
		play.setFont(Font.font(STYLESHEET_CASPIAN, FontWeight.BOLD, 14));
		Button playCom = new Button("Play against the Computer");
		playCom.setPrefSize(REC_LENGTH * 3, REC_LENGTH / 2);
		playCom.setFont(Font.font(STYLESHEET_CASPIAN, FontWeight.BOLD, 14));
		menuRoot.setAlignment(Pos.CENTER);
		menuRoot.setSpacing(20);
		menuRoot.setBackground(Background.fill(BACKGROUND));
		menuRoot.getChildren().addAll(play, playCom);

		// setup the computer settings menu
		HBox playBox = new HBox();
		Button playAsWhite = new Button("Play as White");
		playAsWhite.setFont(Font.font(STYLESHEET_CASPIAN, FontWeight.BOLD, 14));
		Button playAsBlack = new Button("Play as Black");
		playBox.getChildren().addAll(playAsWhite, playAsBlack);
		playAsBlack.setFont(Font.font(STYLESHEET_CASPIAN, FontWeight.BOLD, 14));
		playBox.setSpacing(20);
		playBox.setAlignment(Pos.CENTER);
		HBox difficultyBox = new HBox();
		RadioButton random = new RadioButton("Random");
		RadioButton easy = new RadioButton("Easy");
		ToggleGroup difficultyToggle = new ToggleGroup();
		random.setToggleGroup(difficultyToggle);
		random.setFont(Font.font(STYLESHEET_CASPIAN, FontWeight.BOLD, 14));
		easy.setToggleGroup(difficultyToggle);
		easy.setFont(Font.font(STYLESHEET_CASPIAN, FontWeight.BOLD, 14));
		difficultyBox.getChildren().addAll(random, easy);
		difficultyBox.setSpacing(20);
		difficultyBox.setAlignment(Pos.CENTER);
		comRoot.setAlignment(Pos.CENTER);
		comRoot.setSpacing(20);
		comRoot.setBackground(Background.fill(BACKGROUND));
		comRoot.getChildren().addAll(playBox, difficultyBox);

		// event handler for the play button
		EventHandler<ActionEvent> playHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				primaryStage.setScene(boardScene);
				// notify chessThread that the game is starting
				synchronized (clickedSquare) {
					clickedSquare.notify();
				}
			}
		};
		// event handler for the playCom button
		EventHandler<ActionEvent> playComHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				menuScene.setRoot(comRoot);
			}
		};
		// event handler for the playCom button
		EventHandler<ActionEvent> playAsColorHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				if (difficultyToggle.getSelectedToggle() != null) {
					difficulty = ((RadioButton) difficultyToggle.getSelectedToggle()).getText();
					playAs = ((Button) ae.getTarget()).getText();
					primaryStage.setScene(boardScene);
					// notify chessThread that the game is starting
					synchronized (clickedSquare) {
						clickedSquare.notify();
					}
				}
			}
		};
		play.setOnAction(playHandler);
		playCom.setOnAction(playComHandler);
		playAsWhite.setOnAction(playAsColorHandler);
		playAsBlack.setOnAction(playAsColorHandler);

		// one event handler for the board, getting the square location of any click
		boardHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				// get the x and y coordinates from the rectangle's id
				String id = ((Node) me.getTarget()).getId();
				int x = Integer.parseInt(id.substring(0, 1));
				int y = Integer.parseInt(id.substring(1));
				clickedSquare.setPosition(x, y);
				// notify chess thread that a square was clicked
				synchronized (clickedSquare) {
					clickedSquare.notify();
				}
			}
		};
		// handler for when the mouse enters a square
		EventHandler<MouseEvent> hoverRec = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				((Rectangle) ((StackPane) me.getTarget()).getChildren().get(0)).setEffect(HOVER);
			}
		};
		// handler for when the mouse exits a square
		EventHandler<MouseEvent> exitRec = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				Rectangle rec = (Rectangle) ((StackPane) me.getTarget()).getChildren().get(0);
				int x = Integer.parseInt(rec.getId().substring(0, 1));
				int y = Integer.parseInt(rec.getId().substring(1));
				rec.setFill(getSquareColor(x, y));
				rec.setEffect(null);
				// look at the list of current past moves
				for (int i = 0; i < pastMoveIds.size(); i++) {
					// give past move effects back to current past moves
					if (rec.getId().equals(pastMoveIds.get(i))) {
						addPastMoveEffects(rec);
					}
				}
				// make sure legal moves still have their effects
				addLegalMoveEffects(possibleMoveIds);
				addLegalTakeEffects(possibleTakeIds);
			}
		};
		// set the background color
		boardGrid.setBackground(Background.fill(BACKGROUND));
		// put the board in the center of the scene
		boardGrid.setAlignment(Pos.CENTER);
		// cycle through each row, adding the rectangles and labeling each row
		for (int r = 1; r < 9; r++) {
			Label rowLabel = new Label("" + r); // label for each row
			rowLabel.setFont(Font.font(STYLESHEET_CASPIAN, FontWeight.BOLD, 14));
			BorderPane labelPane = new BorderPane(rowLabel); // put the label in a border pane
			// set the dimensions of the border pane
			labelPane.setPrefHeight(REC_LENGTH);
			labelPane.setPrefWidth(REC_LENGTH / 2);
			BorderPane.setAlignment(labelPane, Pos.CENTER); // position the label within the border pane
			labelPane.setId("0" + r);
			boardGrid.getChildren().add(labelPane); // add the border pane to the board
			// cycle through each column
			for (int c = 1; c < 9; c++) {
				Rectangle rec = new Rectangle(REC_LENGTH, REC_LENGTH); // create a square
				rec.setOnMouseClicked(boardHandler);
				// make the square light or dark
				rec.setFill(getSquareColor(c, r));
				StackPane pane = new StackPane(rec); // put the square in a stack pane
				pane.setOnMouseEntered(hoverRec);
				pane.setOnMouseExited(exitRec);
				GridPane.setConstraints(pane, c, r); // position stack pane within the grid pane
				// set the rectangle and pane id to it's position (xy)
				rec.setId(c + "" + r);
				pane.setId(c + "" + r);
				boardGrid.getChildren().add(pane); // add the stack pane to the board
			}
		}
		// label the columns
		for (int c = 1; c < 9; c++) {
			// label for each column (char a through h)
			Label columnLabel = new Label("" + ((char) ('`' + c)));
			columnLabel.setFont(Font.font(STYLESHEET_CASPIAN, FontWeight.BOLD, 14));
			BorderPane labelPane = new BorderPane(columnLabel); // put the label in a border pane
			// set dimensions for the border pane
			labelPane.setPrefHeight(REC_LENGTH / 2);
			labelPane.setPrefWidth(REC_LENGTH);
			BorderPane.setAlignment(columnLabel, Pos.CENTER); // position the label within the border pane
			// GridPane.setConstraints ( labelPane, c, 9 ); // position the border pane
			// within the grid pane
			labelPane.setId(c + "" + 9);
			boardGrid.getChildren().add(labelPane); // add the border pane to the board
		}
		// event handler for all buttons in pawn promote box
		EventHandler<MouseEvent> pawnPromoteHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent ae) {
				promoteTo = ((Button) ae.getSource()).getId();
				// remove the pawnPromoteBox (the HBox)
				for (int i = 0; i < boardRoot.getChildren().size(); i++) {
					if (boardRoot.getChildren().get(i) instanceof HBox) {
						boardRoot.getChildren().remove(i);
						i--;
					}
				}
				// notify chessThread that an option was selected
				synchronized (clickedSquare) {
					clickedSquare.notify();
				}
			}
		};
		// set up the pawn promotion HBox
		queenButton.setOnMouseClicked(pawnPromoteHandler);
		queenButton.setPrefSize(REC_LENGTH, REC_LENGTH);
		queenButton.setId("Queen");
		rookButton.setOnMouseClicked(pawnPromoteHandler);
		rookButton.setPrefSize(REC_LENGTH, REC_LENGTH);
		rookButton.setId("Rook");
		bishopButton.setOnMouseClicked(pawnPromoteHandler);
		bishopButton.setPrefSize(REC_LENGTH, REC_LENGTH);
		bishopButton.setId("Bishop");
		knightButton.setOnMouseClicked(pawnPromoteHandler);
		knightButton.setPrefSize(REC_LENGTH, REC_LENGTH);
		knightButton.setId("Knight");
		pawnPromoteBox.getChildren().addAll(queenButton, rookButton, bishopButton, knightButton);
		// setup the end game box
		endGameLabel.setAlignment(Pos.CENTER);
		endGameLabel.setFont(Font.font(STYLESHEET_CASPIAN, FontWeight.BOLD, 24));
		endGameLabel.setTextFill(LEGAL_TAKE);
		endGameLabel.setEffect(DULL_GLOW);
		endGameBox.getChildren().add(endGameLabel);
		// set the scene to the menu scene and show it
		primaryStage.setScene(menuScene);
		primaryStage.show();
		// start the chess thread
		chessThread.start();
	} // end start

	/****************************************************************************
	 * dispalyEndGame displays the end game message
	 * 
	 * @param end the message saying how the game ended
	 * @return nothing
	 */
	public static void displayEndGame(String end) {
		endGameLabel.setText(end);
		endGameBox.setAlignment(Pos.TOP_CENTER);
		boardRoot.getChildren().add(endGameBox);
	} // end displayEndGame

	/*****************************************************************************
	 * getSquareColor returns the color LIGHT or DARK depending on the square
	 * 
	 * @param x the x coordinate of the square
	 * @param y the y coordinate of the square
	 * @return LIGHT if the square is light, DARK otherwise
	 */
	public static Color getSquareColor(int x, int y) {
		if (new Square(x, y).isDark()) {
			return DARK;
		} else {
			return LIGHT;
		}
	} // end getSquareColor

	/*******************************************************************************
	 * updateImage removes the piece's image and replaces it with it's correct image
	 * 
	 * @param position  the position of the piece in string form
	 * @param imagePath the imagePath for the new image
	 * @return nothing
	 */
	public static void updateImage(String position, String imagePath) {
		StackPane pane = getPaneAtPosition(position);
		if (pane != null) {
			// remove any images on the square
			for (int i = 0; i < pane.getChildren().size(); i++) {
				if (pane.getChildren().get(i) instanceof ImageView) {
					pane.getChildren().remove(i);
				}
			}
			// get the new image
			try {
				Image image = new Image(new FileInputStream(imagePath));
				ImageView imageView = new ImageView(image);
				imageView.setFitHeight(REC_LENGTH * PIECE_SIZE);
				imageView.setFitWidth(REC_LENGTH * PIECE_SIZE);
				StackPane.setAlignment(imageView, Pos.CENTER);
				imageView.setId(position);
				// assign the image to the board handler event handler
				imageView.setOnMouseClicked(boardHandler);
				// add the image to the rectangle
				pane.getChildren().add(imageView);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

	} // end updateImage

	/****************************************************************************
	 * promotePawn shows the pawnPromoteBox
	 * 
	 * @param playerTurn the player's turn white or black
	 * @return nothing
	 */
	public static void addPromotePawnBox(Piece.Color playerTurn) {
		try {
			// get the 4 images
			String imagePath = "resources\\" + playerTurn.name() + "queen.png";
			Image queen = new Image(new FileInputStream(imagePath));
			imagePath = "resources\\" + playerTurn.name() + "rook.png";
			Image rook = new Image(new FileInputStream(imagePath));
			imagePath = "resources\\" + playerTurn.name() + "bishop.png";
			Image bishop = new Image(new FileInputStream(imagePath));
			imagePath = "resources\\" + playerTurn.name() + "knight.png";
			Image knight = new Image(new FileInputStream(imagePath));
			// put the 4 images into image views
			ImageView queenView = new ImageView(queen);
			queenView.setFitHeight(REC_LENGTH * PIECE_SIZE);
			queenView.setFitWidth(REC_LENGTH * PIECE_SIZE);
			ImageView rookView = new ImageView(rook);
			rookView.setFitHeight(REC_LENGTH * PIECE_SIZE);
			rookView.setFitWidth(REC_LENGTH * PIECE_SIZE);
			ImageView bishopView = new ImageView(bishop);
			bishopView.setFitHeight(REC_LENGTH * PIECE_SIZE);
			bishopView.setFitWidth(REC_LENGTH * PIECE_SIZE);
			ImageView knightView = new ImageView(knight);
			knightView.setFitHeight(REC_LENGTH * PIECE_SIZE);
			knightView.setFitWidth(REC_LENGTH * PIECE_SIZE);
			// add the image views into the buttons
			queenButton.setGraphic(queenView);
			rookButton.setGraphic(rookView);
			bishopButton.setGraphic(bishopView);
			knightButton.setGraphic(knightView);
			// add the pawnPromoteBox to the board
			pawnPromoteBox.setAlignment(Pos.TOP_CENTER);
			boardRoot.getChildren().add(pawnPromoteBox);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	} // end promotePawn

	/******************************************************************************
	 * movePiece moves a piece's image to destination square
	 * 
	 * @param startingPostion the piece's current position
	 * @param destination     the piece's destination square
	 * @return nothing
	 */
	public static void movePiece(String startingPosition, String destination) {
		ImageView imageView; // the piece's image
		StackPane startingPane; // the current position of the piece
		StackPane destinationPane; // the new position of the piece
		startingPane = getPaneAtPosition(startingPosition);
		if (startingPane != null) {
			addPastMoveEffects((Rectangle) startingPane.getChildren().get(0));
			// retrieve the piece's image (which is the second child)
			imageView = (ImageView) startingPane.getChildren().get(1);
			destinationPane = getPaneAtPosition(destination);
			if (destinationPane != null) {
				addPastMoveEffects((Rectangle) destinationPane.getChildren().get(0));
				// add the image to the destination pane
				destinationPane.getChildren().add(imageView);
				imageView.setId(destination); // reset the image's ID
			}
		}
	} // end movePiece

	/******************************************************************************
	 * clearLegalMoveEffects clears all effects and resets the color of given
	 * rectangle
	 * 
	 * @param rec the rectangle being reset
	 * @return nothing
	 */
	public static void clearLegalMoveEffects() {
		StackPane pane; // current stack pane
		Rectangle rec; // rectangle in stack pane
		int x; // x coordinate of rec
		int y; // y coordinate of rec
		for (int i = 0; i < boardGrid.getChildren().size(); i++) {
			// check all stack panes
			if (boardGrid.getChildren().get(i) instanceof StackPane) {
				pane = (StackPane) boardGrid.getChildren().get(i);
				rec = (Rectangle) pane.getChildren().get(0);
				// clear rectangles of legal move effects
				if (rec.getFill() == LEGAL_MOVE || rec.getFill() == LEGAL_TAKE) {
					x = Integer.parseInt(rec.getId().substring(0, 1));
					y = Integer.parseInt(rec.getId().substring(1));
					rec.setFill(getSquareColor(x, y));
					rec.setEffect(null);
					// look at the list of current past moves
					for (int j = 0; j < pastMoveIds.size(); j++) {
						// give past move effects back to current past moves
						if (rec.getId().equals(pastMoveIds.get(j))) {
							addPastMoveEffects(rec);
						}
					}
				}
			}
		}
		// remove all possible moves from the list
		possibleMoveIds.clear();
		possibleTakeIds.clear();
	} // end clearLegalMoveEffects

	/******************************************************************************
	 * clearPastMoveEffects clears all effects and resets the color of given
	 * rectangle
	 * 
	 * @param rec the rectangle being reset
	 * @return nothing
	 */
	public static void clearPastMoveEffects() {
		StackPane pane; // current stack pane
		Rectangle rec; // rectangle in stack pane
		int x; // x coordinate of rec
		int y; // y coordinate of rec
		for (int i = 0; i < boardGrid.getChildren().size(); i++) {
			// check all stack panes
			if (boardGrid.getChildren().get(i) instanceof StackPane) {
				pane = (StackPane) boardGrid.getChildren().get(i);
				rec = (Rectangle) pane.getChildren().get(0);
				// clear rectangles of past move effects
				if (rec.getFill() == PAST_MOVE) {
					x = Integer.parseInt(rec.getId().substring(0, 1));
					y = Integer.parseInt(rec.getId().substring(1));
					rec.setFill(getSquareColor(x, y));
					rec.setEffect(null);
				}
			}
		}
		pastMoveIds.clear(); // remove all past move squares from the list
	} // end clearPastMoveEffects

	/*****************************************************************************
	 * addPastMoveEffects makes given rectangle blue and glowing
	 * 
	 * @param rec the rectangle being modified
	 * @return nothing
	 */
	public static void addPastMoveEffects(Rectangle rec) {
		rec.setFill(PAST_MOVE);
		rec.setEffect(BRIGHT_GLOW);
		String id = rec.getId();
		// only add the id if it is not already present
		boolean alreadyPresent = false;
		for (int i = 0; i < pastMoveIds.size(); i++) {
			if (id.equals(pastMoveIds.get(i))) {
				alreadyPresent = true;
			}
		}
		if (!alreadyPresent) {
			pastMoveIds.add(id);
		}
	} // end addPastMoveEffects

	/*****************************************************************************
	 * addLegalTakeEffects makes given rectangles red (piece can take there)
	 * 
	 * @param rec the rectangle being modified
	 * @return nothing
	 */
	public static void addLegalTakeEffects(ArrayList<String> positions) {
		StackPane pane; // the stack pane at the position
		boolean alreadyPresent; // already present in the array list of possible move ids
		for (String s : positions) {
			// only add the id if it is not already present
			alreadyPresent = false;
			for (int i = 0; i < possibleTakeIds.size(); i++) {
				if (s.equals(possibleTakeIds.get(i))) {
					alreadyPresent = true;
				}
			}
			if (!alreadyPresent) {
				possibleTakeIds.add(s);
			}
			pane = getPaneAtPosition(s);
			((Rectangle) pane.getChildren().get(0)).setFill(LEGAL_TAKE);
			((Rectangle) pane.getChildren().get(0)).setEffect(MEDIUM_GLOW);
		}
	} // end addIllegalPieceEffects

	/*****************************************************************************
	 * addLegalMoveEffects makes given rectangles green (piece can move there)
	 * 
	 * @param rec the rectangle being modified
	 * @return nothing
	 */
	public static void addLegalMoveEffects(ArrayList<String> positions) {
		StackPane pane; // the stack pane at the position
		boolean alreadyPresent; // already present in the array list of possible move ids
		for (String s : positions) {
			// only add the id if it is not already present
			alreadyPresent = false;
			for (int i = 0; i < possibleMoveIds.size(); i++) {
				if (s.equals(possibleMoveIds.get(i))) {
					alreadyPresent = true;
				}
			}
			if (!alreadyPresent) {
				possibleMoveIds.add(s);
			}
			pane = getPaneAtPosition(s);
			((Rectangle) pane.getChildren().get(0)).setFill(LEGAL_MOVE);
			((Rectangle) pane.getChildren().get(0)).setEffect(DULL_GLOW);
		}
	} // end addLegalMoveEffects

	/****************************************************************************
	 * getRecAtPosition returns the rectangle from the stack pane with the Id
	 * position
	 * 
	 * @param position the Id of the stack pane being searched for
	 * @return nothing
	 */
	public static StackPane getPaneAtPosition(String position) {
		StackPane pane = null;
		for (int i = 0; i < boardGrid.getChildren().size(); i++) {
			if (position.equals(boardGrid.getChildren().get(i).getId())) {
				pane = (StackPane) boardGrid.getChildren().get(i);
			}
		}
		return pane;
	} // end getRecAtPosition

	/******************************************************************************
	 * removePiece removes a piece's image from the board
	 * 
	 * @param position the position of the piece in string form
	 * @return nothing
	 */
	public static void removePiece(String position) {
		StackPane pane = getPaneAtPosition(position);
		if (pane != null) {
			// remove the image (which is the second child)
			pane.getChildren().remove(1);
		}
	} // end removePiece

	/*********************************************************************************
	 * addStartingPieces adds each starting piece to the GUI on their correct
	 * squares
	 * 
	 * @param pieces the arrayList of all pieces
	 * @return nothing
	 */
	public static void addStartingPieces(ArrayList<Piece> pieces) {
		String position; // the position of a piece in string form
		StackPane pane; // the Stack Pane with the given position
		Image image; // the image used for a piece
		ImageView imageView; // the image view of the image
		// cycle through the pieces, adding each one to their respective rectangles
		for (Piece p : pieces) {
			position = p.getPosition().getStringPosition();
			pane = getPaneAtPosition(position);
			if (pane != null) {
				try {
					// get the image
					image = new Image(new FileInputStream(p.getImagePath()));
					imageView = new ImageView(image);
					imageView.setFitHeight(REC_LENGTH * PIECE_SIZE);
					imageView.setFitWidth(REC_LENGTH * PIECE_SIZE);
					StackPane.setAlignment(imageView, Pos.CENTER);
					// assign the image to the board handler event handler
					imageView.setId(position);
					imageView.setOnMouseClicked(boardHandler);
					// add the image to the rectangle
					pane.getChildren().add(imageView);
				} catch (FileNotFoundException e) {
					System.out.println(e.getMessage());
//					System.out.println("The image file is not found for the following piece:");
//					Test.printPieceInfo(p);
//					System.out.println(p.getImagePath());
				}
			}
		}
	} // end addPieces

	/************************************************************************************
	 * setPerspective sets constraints on each node in boardRoot according to
	 * playerTurn
	 * 
	 * @param playerTurn the player's turn (white or black)
	 * @return nothing
	 */
	public static void setPerspective(Piece.Color playerTurn) {
		String id; // the id of the node being looked at
		int x; // x coordinate on the board
		int y; // y coordinate on the board
		int reverseX; // x coordinate reversed (Math.abs(x - 8) + 1)
		int reverseY; // y coordinate reversed
		for (int i = 0; i < boardGrid.getChildren().size(); i++) {
			id = boardGrid.getChildren().get(i).getId();
			x = Integer.parseInt(id.substring(0, 1));
			y = Integer.parseInt(id.substring(1));
			reverseX = Math.abs(x - 8) + 1;
			reverseY = Math.abs(y - 8) + 1;
			// labels for the rows
			if (x == 0) {
				GridPane.setConstraints(boardGrid.getChildren().get(i), 0,
						(playerTurn == Piece.Color.WHITE) ? reverseY : y);
			}
			// labels for the columns
			else if (y == 9) {
				GridPane.setConstraints(boardGrid.getChildren().get(i),
						(playerTurn == Piece.Color.WHITE) ? x : reverseX, 9);
			}
			// all rectangles (white perspective)
			else if (playerTurn == Piece.Color.WHITE) {
				GridPane.setConstraints(boardGrid.getChildren().get(i), x, reverseY);
			}
			// all rectangles (black perspective)
			else {
				GridPane.setConstraints(boardGrid.getChildren().get(i), reverseX, y);
			}
		}
	} // end setPerspective

	public static Square getClickedSquare() {
		return clickedSquare;
	}

	public static String getPlayAs() {
		return playAs;
	}

	public static String getDifficulty() {
		return difficulty;
	}

	public static String getPromoteTo() {
		return promoteTo;
	}
}
