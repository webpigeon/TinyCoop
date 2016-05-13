package Controllers.enhanced;

import java.util.List;
import java.util.Random;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.GameState;

public class RandomPredictor implements Predictor {

	private Random random;

	public RandomPredictor() {
		this.random = new Random();
	}

	public RandomPredictor(long seed) {
		this.random = new Random(seed);
	}

	protected Action getRandomAction(int playerID, GameState state) {
		List<Action> legalActions = state.getLegalActions(playerID);
		int id = random.nextInt(legalActions.size());
		return legalActions.get(id);
	}

	@Override
	public void init(int agentID) {
	}

	@Override
	public void observe(int pid, Action... history) {

	}

	@Override
	public Action predict(int pid, GameState state) {
		return getRandomAction(pid, state);
	}

	@Override
	public String toString() {
		return "Random";
	}

}
