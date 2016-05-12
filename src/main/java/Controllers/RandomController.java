package Controllers;

import java.util.List;
import java.util.Random;

import api.Action;
import api.GameState;

/**
 * Created by pwillic on 30/06/2015.
 */
public class RandomController extends PiersController {
	private Random random;

	public RandomController() {
		this.random = new Random();
	}

	public RandomController(long seed) {
		this.random = new Random(seed);
	}

	@Override
	public Action get(GameState game) {
		return getRandomAction(0, game);
	}

	protected Action getRandomAction(int playerID, GameState state) {
		List<Action> legalActions = state.getLegalActions(playerID);
		int id = random.nextInt(legalActions.size());
		return legalActions.get(id);
	}

}
