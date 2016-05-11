package uk.me.webpigeon.controllers.mcts;

import java.util.List;
import java.util.Random;

import api.Action;
import api.controller.GameObservation;
import gamesrc.controllers.AbstractController;

/**
 * Open Loop MCTS implementatation.
 *
 * This is the same basic code as Pier's MCTS player but tweaked for
 * readability.
 */
public class MCTSAgent extends AbstractController {

	private final Integer iterationLimit;
	private final Integer treeDepth;
	private final Integer rolloutDepth;

	private Random random;
	private MCTSNode root;

	public MCTSAgent(int iterationLimit, int treeDepth, int rolloutDepth) {
		super("mcts");
		this.iterationLimit = iterationLimit;
		this.treeDepth = treeDepth;
		this.rolloutDepth = rolloutDepth;
		this.random = new Random();
	}

	@Override
	public Action getAction(GameObservation state) {
		root = new MCTSNode(this, state.getLegalActions(myID).size());

		for (int i = 0; i < iterationLimit; i++) {
			GameObservation obs = state.getCopy();

			MCTSNode selected = root.select(treeDepth, obs);
			double score = rollout(obs);
			selected.update(score);
		}

		//System.out.println("MCTS done: " + root.getVisits() + " deepest node: " + root.getDeepestNode());

		return root.getBestAction();
	}

	public Action getDefaultMove() {
		System.err.println("invoking default move, this is bad");
		return Action.NOOP;
	}

	public int getMyID() {
		return myID;
	}

	public Action getOppAction(GameObservation obs) {
		return selectRandomly(obs.getLegalActions(theirID));
	}

	public Action getRandomAction(GameObservation obs) {
		return selectRandomly(obs.getLegalActions(myID));
	}

	public double rollout(GameObservation state) {
		GameObservation rollState = state.getCopy();

		for (int depth = 0; depth < rolloutDepth; depth++) {
			rollState.apply(getRandomAction(rollState), getOppAction(rollState));

			// break early if the game is over
			if (rollState.hasWon()) {
				break;
			}
		}

		return rollState.getScore();
	}

	public <T> T selectRandomly(List<T> choices) {
		return choices.get(random.nextInt(choices.size()));
	}

}
