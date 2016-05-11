package Controllers.planning;

import api.GameState;

/**
 * Heuristic which returns the current state's value according to the game.
 *
 * This isn't really a heuristic, it just returns what the current score is.
 */
public class StateScoreHeuristic implements Heuristic {

	@Override
	public double getScore(GameState state) {
		return state.getScore();
	}

	@Override
	public void init() {

	}

}
