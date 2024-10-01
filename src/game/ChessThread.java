package game;

import game.helpers.FXCommander;
import gui.ChessDriver;

/**
 * @author John Hoffmann
 * 
 *         ChessThread handles everything outside of an actual game (like
 *         picking which type of game and the aftermath)
 */
public class ChessThread extends Thread {

	private ChessDriver gui;

	/**
	 * @param gui the GUI associated with this game
	 */
	public ChessThread(ChessDriver gui) {
		this.gui = gui;
	}

	public void run() {
		Thread.currentThread().setName("Chess Thread");
		// assume a regular game is being played
		StandardGame game = new StandardGame(gui);
		game.startGame();

		// the game is over
		FXCommander.displayEndGame(game.getEnding(), gui);
	}
}
