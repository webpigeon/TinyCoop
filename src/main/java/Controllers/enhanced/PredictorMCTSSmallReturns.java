package Controllers.enhanced;

import java.util.List;
import java.util.Random;

import Controllers.PiersController;
import FastGame.FastAction;
import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.GameState;

/**
 * Created by pwillic on 23/06/2015.
 */
public class PredictorMCTSSmallReturns extends PiersController {

	private class MCTSNode {

		private static final double EPSILON = 1e-6;

		private double explorationConstant;
		private Action moveToThisState;

		private double totalValue;
		private int numberOfVisits;
		private int currentDepth;
		private int childrenExpandedSoFar = 0;

		private MCTSNode parent;
		private MCTSNode[] children;
		private int childLength;

		private PredictorMCTSSmallReturns mcts;

		// Root
		public MCTSNode(double explorationConstant, PredictorMCTSSmallReturns mcts, int actionLength) {
			this.explorationConstant = explorationConstant;
			this.currentDepth = 0;
			this.mcts = mcts;
			this.childLength = actionLength;
		}

		// Child
		public MCTSNode(MCTSNode parent, Action moveToThisState, int actionLength) {
			this.parent = parent;
			this.explorationConstant = parent.explorationConstant;
			this.moveToThisState = moveToThisState;
			this.currentDepth = parent.currentDepth + 1;
			this.mcts = parent.mcts;
			this.childLength = actionLength;
		}

		public double calculateChild() {
			return totalValue / (numberOfVisits + EPSILON)
					+ Math.sqrt(2 * Math.log(parent.numberOfVisits + 1) / (numberOfVisits + EPSILON))
					+ mcts.random.nextDouble() * EPSILON;
		}

		private MCTSNode expand(GameState state) {
			int bestAction = 0;
			double bestValue = -Double.MAX_VALUE;
			if (children == null)
				children = new MCTSNode[childLength];
			Random random = mcts.random;
			for (int i = 0; i < children.length; i++) {
				double x = random.nextDouble();
				if (x > bestValue && children[i] == null) {
					bestAction = i;
					bestValue = x;
				} else if (x == bestValue && children[i] == null && random.nextBoolean()) {
					bestAction = i;
				}
			}

			List<Action> allActions = state.getLegalActions(0);
			children[bestAction] = new MCTSNode(this, allActions.get(bestAction), state.getActionLength());
			childrenExpandedSoFar++;
			return children[bestAction];
		}

		protected Action getBestAction() {
			if (children == null)
				return FastAction.NOOP;
			int selected = -1;
			double bestValue = -Double.MAX_VALUE;
			for (int child = 0; child < children.length; child++) {
				if (children[child] == null)
					continue;
				double childValue = children[child].getTotalValue();
				if (childValue > bestValue) {
					bestValue = childValue;
					selected = child;
				} else if (childValue == bestValue && random.nextBoolean()) {
					selected = child;
				}
			}
			if (selected == -1)
				return FastAction.NOOP;
			return children[selected].getMoveToThisState();
		}

		public int getChildLength() {
			return childLength;
		}

		public MCTSNode[] getChildren() {
			return children;
		}

		public int getChildrenExpandedSoFar() {
			return childrenExpandedSoFar;
		}

		public int getCurrentDepth() {
			return currentDepth;
		}

		public double getExplorationConstant() {
			return explorationConstant;
		}

		public Action getMoveToThisState() {
			return moveToThisState;
		}

		public int getNumberOfVisits() {
			return numberOfVisits;
		}

		protected Action getOppAction(int playerID, GameState state) {
			return predictor.predict(playerID, state);
		}

		public MCTSNode getParent() {
			return parent;
		}

		protected Action getRandomAction(int playerID, GameState state) {
			List<Action> legalActions = state.getLegalActions(playerID);
			int id = random.nextInt(legalActions.size());
			return legalActions.get(id);
		}

		public double getTotalValue() {
			return totalValue;
		}

		private boolean isFullyExpanded() {
			return childrenExpandedSoFar == childLength;
		}

		public double rollout(GameState state) {
			int rolloutDepth = this.currentDepth;

			double score = state.getScore();
			double weightedScore = score;

			while (!state.hasWon() && rolloutDepth < mcts.getMaxRolloutDepth()) {
				state.update(getRandomAction(0, state), getRandomAction(0, state));

				if (score != state.getScore()) {
					double deltaScore = state.getScore() - score;
					weightedScore += (deltaScore * Math.pow(gamma, rolloutDepth));
					score = state.getScore();
				}

				rolloutDepth++;
			}
			return weightedScore;
		}

		protected MCTSNode select(GameState state) {
			MCTSNode current = this;
			while (current.currentDepth < mcts.getMaxUCTDepth() && !state.hasWon()) {
				if (current.isFullyExpanded()) {
					current = current.selectBestChild();
					if (mcts.isFirst()) {
						state.update(current.getMoveToThisState(), getOppAction(1, state));
					} else {
						state.update(getOppAction(0, state), current.getMoveToThisState());
					}
				} else {
					/// Expand
					MCTSNode expandedChild = current.expand(state);
					if (mcts.isFirst()) {
						state.update(expandedChild.getMoveToThisState(), getOppAction(1, state));
					} else {
						state.update(getOppAction(0, state), expandedChild.getMoveToThisState());
					}
					return expandedChild;
				}
			}
			return current;
		}

		protected MCTSNode selectBestChild() {
			int selected = 0;
			double bestValue = children[0].calculateChild();
			for (int child = 1; child < children.length; child++) {
				double childValue = children[child].calculateChild();
				if (childValue > bestValue) {
					selected = child;
					bestValue = childValue;
				}
			}
			return children[selected];
		}

		public void updateValues(double value) {
			// All nodes are ours so lets go for it
			MCTSNode current = this;
			while (current.parent != null) {
				current.totalValue += value;
				current.numberOfVisits++;
				current = current.parent;
			}
			current.totalValue += value;
			current.numberOfVisits++;
		}
	}

	private Random random = new Random();
	private int maxUCTDepth = 5;
	private int maxRolloutDepth = 30;
	private int iterationLimit = 0;
	private double gamma;

	private Predictor predictor;

	private boolean first;

	public PredictorMCTSSmallReturns(boolean first, int iterationLimit) {
		this.first = first;
		this.iterationLimit = iterationLimit;
	}

	public PredictorMCTSSmallReturns(int iterationLimit, int maxUCTDepth, int maxRolloutDepth, float gamma,
			Predictor predictor) {
		this.iterationLimit = iterationLimit;
		this.maxUCTDepth = maxUCTDepth;
		this.maxRolloutDepth = maxRolloutDepth;
		this.predictor = predictor;
		this.gamma = gamma;
	}

	public PredictorMCTSSmallReturns(int iterationLimit, int maxUCTDepth, int maxRolloutDepth, Predictor predictor) {
		this.iterationLimit = iterationLimit;
		this.maxUCTDepth = maxUCTDepth;
		this.maxRolloutDepth = maxRolloutDepth;
		this.predictor = predictor;
		this.gamma = 0.99;
	}

	@Override
	public Action get(GameState game) {
		MCTSNode root = new MCTSNode(2.0, this, game.getActionLength());
		MCTSNode travel;
		GameState workingGame;
		int iterations = 0;
		while (iterations < iterationLimit) {
			workingGame = game.getClone();
			travel = root.select(workingGame);
			double result = travel.rollout(workingGame);
			travel.updateValues(result);
			iterations++;
		}
		// System.out.println("Iterations : " + iterations);
		// System.out.println(root.getBestAction());
		return root.getBestAction();
	}

	public int getMaxRolloutDepth() {
		return maxRolloutDepth;
	}

	public int getMaxUCTDepth() {
		return maxUCTDepth;
	}

	@Override
	public String getSimpleName() {
		return "PredictorMCTS: (" + iterationLimit + ";" + maxUCTDepth + ";" + maxRolloutDepth + ")";
	}

	public boolean isFirst() {
		return first;
	}

	@Override
	public void startGame(int agentID) {
		super.startGame(agentID);
		predictor.init(agentID);
		this.first = agentID == 0;
	}
}
