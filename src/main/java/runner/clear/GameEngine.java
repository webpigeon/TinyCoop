package runner.clear;

import java.util.List;
import java.util.concurrent.Callable;

import api.Action;
import api.GameState;
import api.controller.Controller;
import api.controller.GameObservation;
import gamesrc.SimpleGame;
import runner.tinycoop.GameSetup;

@Deprecated
public class GameEngine implements Callable<GameRecord> {
	private final GameSetup setup;
	private final int maxTicks;
	private final GameRecord record;

	public GameEngine(GameSetup setup, int maxTicks, GameRecord record) {
		this.setup = setup;
		this.maxTicks = maxTicks;
		this.record = record;
	}

	@Override
	public GameRecord call() {
		if (1==1) {
			return null;
		}
		
		try {
			// work on copies to avoid problems
			SimpleGame runner = null;
			Controller p1Real = null;
			Controller p2Real = null;

			// phase 0: initialisation
			int ticks = 0;
			p1Real.startGame(GameState.PLAYER_0, GameState.PLAYER_1);
			p2Real.startGame(GameState.PLAYER_1, GameState.PLAYER_0);
			record.gameStarted();

			// phase 1: running game
			while (!Thread.interrupted() && ticks < maxTicks) {

				Action p1Action = getLegalAction(runner.getObservationFor(GameState.PLAYER_0), GameState.PLAYER_0,
						p1Real);
				Action p2Action = getLegalAction(runner.getObservationFor(GameState.PLAYER_1), GameState.PLAYER_1,
						p2Real);

				runner.update(p1Action, p2Action);
				record.recordState(ticks, runner, p1Action, p2Action);
				record.recordAction(ticks, GameState.PLAYER_0, p1Action);
				record.recordAction(ticks, GameState.PLAYER_1, p2Action);

				// phase 2a: game is over (won)
				if (runner.hasWon()) {
					record.recordResult(ticks, runner.getScore(), Result.WON);
					return record;
				}

				ticks++;
			}

			// phase 2b: game is over (timeout)
			record.recordResult(ticks, runner.getScore(), Result.TIMEOUT);
			return record;
		} catch (Exception ex) {
			// if something goes wrong, report the crash and kill the thread
			ex.printStackTrace();
			System.err.println("[ex] game runner: " + ex);
			record.recordResult(-1, 0.0, Result.CRASH);
			return record;
		}
	}

	private Action getAction(GameObservation state, int pid, Controller p) {
		return p.getAction(state);
	}

	private Action getLegalAction(GameObservation state, int pid, Controller p) {
		List<Action> legalActions = state.getLegalActions(pid);
		Action action = p.getAction(state);

		if (!legalActions.contains(action)) {
			throw new IllegalArgumentException("controller cheated! " + action + " is not legal for " + pid);
		}

		return action;
	}

}
