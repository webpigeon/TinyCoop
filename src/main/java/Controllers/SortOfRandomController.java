package Controllers;

import java.util.List;
import java.util.Random;

import api.Action;
import api.GameState;
import gamesrc.Filters;

/**
 * Created by pwillic on 30/06/2015.
 */
public class SortOfRandomController extends PiersController {
	private static final Double COMM_CHANCE = 0.75;

	private Random random;
	private int agentID;
	private double commChance;

	public SortOfRandomController() {
		this(COMM_CHANCE);
	}

	public SortOfRandomController(double bais) {
		this.commChance = bais;
	}

	@Override
	public Action get(GameState game) {

		// get only the movement actions
		List<Action> actions = Filters.filterMovement(game.getLegalActions(agentID));

		// half of the time replace the actions with communication actions
		if (random.nextDouble() > commChance) {
			List<Action> talkActions = Filters.filterTalk(game.getLegalActions(agentID));
			if (!talkActions.isEmpty()) {
				actions = talkActions;
			}

		}

		return actions.get(random.nextInt(actions.size()));
	}

	@Override
	public void startGame(int agentID) {
		super.startGame(agentID);
		this.agentID = agentID;
		this.random = new Random();
	}
}
