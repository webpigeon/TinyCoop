package uk.me.webpigeon.phd.tinycoop.runner;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import runner.clear.Result;
import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.GameState;
import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;
import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;
import utils.GameTimer;

/**
 * Executes a TinyCoop game.
 *
 * The game terminates after the MAX_TICKS have been reached or the game has been won. 
 */
public class GameExecutor implements Callable<GameResult> {
	private static final Integer MAX_TICKS = 2000;
	private static final Logger LOG = Logger.getLogger(GameExecutor.class.getCanonicalName());

	private final GameSetup setup;
	private final SimpleGame game;
	private final Controller p1;
	private final Controller p2;

	public GameExecutor(GameSetup setup, SimpleGame game, Controller p1, Controller p2) {
		this.setup = setup;
		this.game = game;
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public GameResult call() throws Exception {
		LOG.info("game run started: "+setup);
		
		long wallStartTime = GameTimer.getWallTime();
		long cpuStartTime = GameTimer.getUserTime();
		Result result = Result.WON;
		
		int tick = 0;
		try {
			p1.startGame(GameState.PLAYER_0, GameState.PLAYER_1);
			p2.startGame(GameState.PLAYER_1, GameState.PLAYER_0);
			LOG.fine("game initialised: "+setup);

			while (!game.hasWon()) {

				// prompt each of the controllers for their moves
				Action p1Action = p1.getAction(game.getObservationFor(GameState.PLAYER_0));
				Action p2Action = p2.getAction(game.getObservationFor(GameState.PLAYER_1));

				// update the game
				game.update(p1Action, p2Action);

				// check for timeout condition
				if (tick >= MAX_TICKS) {
					LOG.fine("game timeout: "+setup);
					result = Result.TIMEOUT;
					break;
				}

				tick++;
			}
		} catch (Exception ex) {
			result = Result.CRASH;
			LOG.severe("game run crashed: "+setup+" "+ex);
			ex.printStackTrace();
		}

		LOG.info("game run complete: "+setup+" "+result);
		
		long cpuTime = GameTimer.getUserTime() - cpuStartTime;
		long wallTime = GameTimer.getWallTime() - wallStartTime;
		return new GameResult(setup, result, game.getScore(), tick, wallTime, cpuTime);
	}

}
