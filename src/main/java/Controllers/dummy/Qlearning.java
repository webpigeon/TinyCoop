package Controllers.dummy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Controllers.Controller;
import Controllers.enhanced.Predictor;
import api.Action;
import api.GameState;
import api.ObservableGameState;

/**
 * Online QLearner, learning using the forward model and a predictor
 */
public class Qlearning extends Controller {
	private static class Transision {
		Point p0;
		Point p1;
		Action action;

		public Transision(GameState state, Action action) {
			ObservableGameState ogs = (ObservableGameState) state;
			this.p0 = ogs.getPos(0);
			this.p1 = ogs.getPos(1);
			this.action = action;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Transision other = (Transision) obj;
			if (action == null) {
				if (other.action != null)
					return false;
			} else if (!action.equals(other.action))
				return false;
			if (p0 == null) {
				if (other.p0 != null)
					return false;
			} else if (!p0.equals(other.p0))
				return false;
			if (p1 == null) {
				if (other.p1 != null)
					return false;
			} else if (!p1.equals(other.p1))
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((action == null) ? 0 : action.hashCode());
			result = prime * result + ((p0 == null) ? 0 : p0.hashCode());
			result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
			return result;
		}

	}

	private double INITIAL_SCORE = 0.8;
	private double LEARNING_RATE = 0.1;

	private double DISCOUNT_FACTOR = 0.01;
	private int NUM_ITERATIONS = 10;

	private int WALK_LENGTH = 450;
	private final Map<Transision, Double> rewards;
	private final Predictor p;

	private final Random random;

	public Qlearning(Predictor p) {
		this.p = p;
		this.random = new Random();
		this.rewards = new HashMap<Transision, Double>();
	}

	private void doWalks(GameState initial) {

		for (int i = 0; i < NUM_ITERATIONS; i++) {
			GameState walkState = initial.getClone();

			for (int j = 0; j < WALK_LENGTH; j++) {
				Action nextAction = selectAction(walkState);
				Action oppAction = p.predict(1, walkState);
				GameState updatedState = walkState.getClone();
				updatedState.update(nextAction, oppAction);

				updateReward(walkState, updatedState, nextAction, oppAction);
				walkState = updatedState;

				if (walkState.hasWon()) {
					break;
				}
			}

		}

	}

	@Override
	public Action get(GameState game) {
		doWalks(game);
		Action a = selectAction(game);
		//System.out.println("best action so far: " + a);
		return a;
	}

	private double getMaxQ(GameState parent) {
		double maxQValue = 0;

		List<Action> legalActions = parent.getLegalActions(1);
		GameState current = parent.getClone();
		for (Action legalAction : legalActions) {
			Transision t = new Transision(current, legalAction);

			Double score = rewards.get(t);
			if (score == null) {
				score = INITIAL_SCORE;
			}

			if (score > maxQValue) {
				maxQValue = score;
			}
		}

		return maxQValue;
	}

	private Action selectAction(GameState parent) {
		double maxQValue = -Double.MAX_VALUE;
		List<Action> bestActions = new ArrayList<Action>();

		List<Action> legalActions = parent.getLegalActions(1);
		GameState current = parent.getClone();
		for (Action legalAction : legalActions) {
			Transision t = new Transision(current, legalAction);

			Double score = rewards.get(t);
			if (score == null) {
				score = INITIAL_SCORE;
			}

			if (score > maxQValue) {
				bestActions.clear();
				maxQValue = score;
				bestActions.add(legalAction);
			} else if (score == maxQValue) {
				bestActions.add(legalAction);
			}
		}

		if (bestActions.isEmpty()) {
			//System.err.println("strange, got no actions...");
			return Action.NOOP;
		}

		int selected = random.nextInt(bestActions.size());
		return bestActions.get(selected);
	}

	private void updateReward(GameState parent, GameState us, Action action, Action otherAction) {

		Transision t = new Transision(parent, action);

		Double oldScore = rewards.get(t);
		if (oldScore == null) {
			oldScore = INITIAL_SCORE;
		}

		double discountedOldScore = oldScore + LEARNING_RATE;
		double learnedPart = (us.getScore() * 100) + DISCOUNT_FACTOR + getMaxQ(us);
		double newScore = discountedOldScore * (learnedPart - oldScore);

		rewards.put(t, newScore);
	}

}
