package runner.tinycoop;

import java.util.concurrent.Callable;

import api.Action;
import api.GameState;
import api.controller.Controller;
import gamesrc.SimpleGame;
import runner.clear.Result;

public class GameExecutor implements Callable<GameResult> {
	private static final Integer MAX_TICKS = 20000;
	
	private final SimpleGame game;
	private final Controller p1;
	private final Controller p2;
	
	public GameExecutor(SimpleGame game, Controller p1, Controller p2) {
		this.game = game;
		this.p1 = p1;
		this.p2 = p2;
	}
	
	@Override
	public GameResult call() throws Exception {
		
		int tick = 0;
		try {
			p1.startGame(GameState.PLAYER_0, GameState.PLAYER_1);
			p2.startGame(GameState.PLAYER_1, GameState.PLAYER_0);
			
			while (!game.hasWon()) {
				
				//prompt each of the controllers for their moves
				Action p1Action = p1.getAction(game.getObservationFor(GameState.PLAYER_0));
				Action p2Action = p2.getAction(game.getObservationFor(GameState.PLAYER_1));
				
				//update the game
				game.update(p1Action, p2Action);
				
				//check for timeout condition
				if (tick >= MAX_TICKS) {
					return new GameResult(Result.TIMEOUT, game.getScore(), tick);
				}
				
				tick++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return new GameResult(Result.CRASH, game.getScore(), tick);
		}
		
		return new GameResult(Result.WON, game.getScore(), tick);
	}

}
