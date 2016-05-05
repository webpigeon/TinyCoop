package runner.clear;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import Controllers.Controller;
import api.Action;
import api.GameState;
import gamesrc.SimpleGame;

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
		// work on copies to avoid problems
		SimpleGame runner = setup.buildGame();
		Controller p1Real = setup.getPlayer0();
		Controller p2Real = setup.getPlayer1();

		// phase 0: initialisation
		int ticks = 0;
		p1Real.startGame(GameState.PLAYER_0);
		p2Real.startGame(GameState.PLAYER_1);

		// phase 1: running game
		while (!Thread.interrupted() && ticks < maxTicks) {

			Action p1Action = getLegalAction(runner, GameState.PLAYER_0, p1Real);
			Action p2Action = getLegalAction(runner, GameState.PLAYER_1, p2Real);

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
	}

	private Action getAction(GameState state, int pid, Controller p) {
		return p.get(state.getClone());
	}

	private Action getLegalAction(GameState state, int pid, Controller p) {
		List<Action> legalActions = Arrays.asList(state.getLegalActions(pid));
		Action action = p.get(state.getClone());

		if (!legalActions.contains(action)) {
			throw new IllegalArgumentException("controller cheated! " + action + " is not legal for " + pid);
		}

		return action;
	}

}
