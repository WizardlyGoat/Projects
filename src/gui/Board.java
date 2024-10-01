package gui;

import java.util.ArrayList;

import gui.nodes.PawnPromoteBox;
import gui.nodes.Rank;
import gui.nodes.Square;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pieces.Piece.Color;

/**
 * 
 * @author John Hoffmann
 * 
 *         Board handles all GUI components dealing with the board
 *
 */
public class Board {

	private StackPane boardRoot;
	private Scene boardScene;
	private Square[] squares;
	private Rank[] ranks;
	// squares with different colors
	private ArrayList<Square> pastMoves = new ArrayList<Square>();
	private ArrayList<Square> possibleMoves = new ArrayList<Square>();
	private ArrayList<Square> possibleTakes = new ArrayList<Square>();

	public Board(int x, int y) {
		boardRoot = new StackPane();
		GridPane boardGrid = new GridPane();
		boardGrid.setAlignment(Pos.CENTER);
		boardGrid.setBackground(Background.fill(Style.BACKGROUND));

		// create all of the squares
		squares = new Square[64];
		int index = 0;
		for (int r = 1; r < 9; r++) {
			for (int c = 1; c < 9; c++) {
				squares[index] = Style.buildSquare(c, r);
				boardGrid.getChildren().add(squares[index]);
				index++;
			}
		}

		// create the column and row labels (ranks)
		ranks = new Rank[16];
		index = 0;
		for (char c = 'a'; c <= 'h'; c++) {
			ranks[index] = new Rank(c);
			boardGrid.getChildren().add(ranks[index]);
			index++;
		}
		for (int r = 1; r < 9; r++) {
			ranks[index] = new Rank(r);
			boardGrid.getChildren().add(ranks[index]);
			index++;
		}

		// add the grid to the scene
		boardRoot.getChildren().add(boardGrid);
		boardScene = new Scene(boardRoot, x, y);
	}

	/**
	 * creates a pawn promotion box
	 * 
	 * @param playerTurn the player's color
	 * @return a PawnPromoteBox with the appropriate colors
	 */
	public PawnPromoteBox createPawnPromoteBox(Color playerTurn) {
		PawnPromoteBox pawnPromote = new PawnPromoteBox(playerTurn);
		boardRoot.getChildren().add(pawnPromote);
		return pawnPromote;
	}

	/**
	 * removes the first instance of a PawnPromoteBox on the board
	 */
	public void removePawnPromoteBox() {
		for (int i = 0; i < boardRoot.getChildren().size(); i++) {
			if (boardRoot.getChildren().get(i) instanceof PawnPromoteBox) {
				boardRoot.getChildren().remove(i);
				break;
			}
		}
	}

	/**
	 * displays an end game label
	 * 
	 * @param message the message the the label should say
	 */
	public void addEndGameLabel(String message) {
		// end game label
		Label endGameLabel = new Label(message);
		endGameLabel.setAlignment(Pos.CENTER);
		endGameLabel.setFont(Font.font("CASPIAN", FontWeight.BOLD, 24));
		endGameLabel.setTextFill(Style.LEGAL_TAKE);
		endGameLabel.setEffect(Style.DULL_GLOW);

		// HBox for the label
		HBox endGameBox = new HBox(endGameLabel);
		endGameBox.setAlignment(Pos.TOP_CENTER);

		// add the HBox to the stack pane
		boardRoot.getChildren().add(endGameBox);
	}

	// getters and setters
	public Scene getBoardScene() {
		return boardScene;
	}

	public Square[] getSquares() {
		return squares;
	}

	public ArrayList<Square> getPastMoves() {
		return pastMoves;
	}

	public void setPastMoves(ArrayList<Square> pastMoves) {
		this.pastMoves = pastMoves;
	}

	public ArrayList<Square> getPossibleMoves() {
		return possibleMoves;
	}

	public void setPossibleMoves(ArrayList<Square> possibleMoves) {
		this.possibleMoves = possibleMoves;
	}

	public ArrayList<Square> getPossibleTakes() {
		return possibleTakes;
	}

	public void setPossibleTakes(ArrayList<Square> possibleTakes) {
		this.possibleTakes = possibleTakes;
	}

	public Rank[] getRanks() {
		return ranks;
	}

}
