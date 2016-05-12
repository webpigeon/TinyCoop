package Controllers.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Controllers.PiersController;
import Controllers.enhanced.Predictor;
import api.Action;
import api.GameState;

/**
 * Basic hill climber.
 *
 * Kinda HSPish but not really.
 */
public class HillClimber extends PiersController {
	private final Heuristic herustic;
	private final Predictor predictor;
	private final Random random;

	private int playerID;
	private int oppID;

	public HillClimber(Heuristic heuristic, Predictor predictor) {
		this.herustic = heuristic;
		this.predictor = predictor;
		this.random = new Random();
	}

	public void init(int playerID) {
		herustic.init();
		predictor.init(playerID);
		this.playerID = playerID;
		this.oppID = playerID == GameState.PLAYER_0 ? GameState.PLAYER_1 : GameState.PLAYER_0;
	}

	public Action nextAction(GameState current) {

		GameState active = current.getClone();
		while (current.hasWon()) {
			Action nextMove = selectMove(active);
			// active.update(nextMove, );
		}

		return null;
	}

	protected Action selectMove(GameState current) {
		double best = -Double.MAX_VALUE;
		List<Action> bestActions = new ArrayList<Action>();

		List<Action> legalMoves = current.getLegalActions(playerID);
		for (Action legalMove : legalMoves) {
			GameState state = current.getClone();
			state.update(legalMove, predictor.predict(oppID, state));
			double score = herustic.getScore(state);

			if (best < score) {
				bestActions.clear();
				best = score;
			} else if (best == score) {
				bestActions.add(legalMove);
			}
		}

		return bestActions.get(random.nextInt(bestActions.size()));
	}
}
