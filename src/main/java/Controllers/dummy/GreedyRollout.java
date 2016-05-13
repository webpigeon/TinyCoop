package Controllers.dummy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Controllers.PiersController;
import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.GameState;

/**
 * Performs random rollouts and selects the move which had the highest payout
 * across all rollouts.
 */
public class GreedyRollout extends PiersController {
	public static final Integer NUM_ROLLOUTS = 500;
	public static final Integer MAX_ROLLOUT_DEPTH = 25;

	protected Random random;
	private int myID;
	private int partnerID;

	protected int numRollouts;
	protected int[] visits;
	protected double[] scores;

	public GreedyRollout() {
		this.random = new Random();
	}

	@Override
	public Action get(GameState game) {

		List<Action> legalActions = game.getLegalActions(myID);

		visits = new int[legalActions.size()];
		scores = new double[legalActions.size()];

		for (int i = 0; i < NUM_ROLLOUTS; i++) {
			GameState current = game.getClone();

			// simulate 1 move ahead and perform rollouts
			int actionID = selectAction(legalActions);
			simulateMove(current, legalActions.get(actionID));

			visits[actionID]++;
			scores[actionID] += rollout(current);
			numRollouts++;
		}

		double bestScore = -Double.MAX_VALUE;
		List<Integer> actions = new ArrayList<Integer>();
		for (int i = 0; i < legalActions.size(); i++) {

			double score = scores[i] / visits[i];
			if (score > bestScore) {
				bestScore = score;
				actions.clear();
				actions.add(i);
			} else if (score == bestScore) {
				actions.add(i);
			}
		}

		if (actions.isEmpty()) {
			return getRandomAction(0, game);
		}

		System.out.println("best score: " + bestScore + " " + actions.size());
		int randomSelect = random.nextInt(actions.size());
		return legalActions.get(actions.get(randomSelect));
	}

	public Action getPartnerMove(GameState state) {
		return getRandomAction(partnerID, state);
	}

	public Action getRandomAction(int pid, GameState state) {
		List<Action> legalActions = state.getLegalActions(pid);
		int id = random.nextInt(legalActions.size());
		return legalActions.get(id);
	}

	public double rollout(GameState start) {

		GameState current = start.getClone();
		int depth = 0;

		while (!current.hasWon() && depth < MAX_ROLLOUT_DEPTH) {
			Action ourMove = getRandomAction(myID, current);
			simulateMove(current, ourMove);
			depth++;
		}

		return current.getScore();
	}

	public int selectAction(List<Action> legalActions) {
		int actionID = random.nextInt(legalActions.size());
		return actionID;
	}

	public void simulateMove(GameState state, Action move) {
		if (myID == GameState.PLAYER_0) {
			state.update(move, getPartnerMove(state));
		} else {
			state.update(getPartnerMove(state), move);
		}
	}

	@Override
	public void startGame(int agentID) {
		super.startGame(agentID);
		this.myID = agentID;
		this.partnerID = myID == GameState.PLAYER_0 ? GameState.PLAYER_1 : GameState.PLAYER_0;
	}

}
