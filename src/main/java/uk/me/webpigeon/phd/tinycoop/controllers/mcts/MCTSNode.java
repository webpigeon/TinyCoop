package uk.me.webpigeon.phd.tinycoop.controllers.mcts;

import java.util.ArrayList;
import java.util.List;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.controller.GameObservation;

public class MCTSNode {
	private static int deepest;

	private static final Double EPSILON = 1e-6;
	private static final Double EXP_CONST = 2d;

	private final MCTSAgent agent;
	private final MCTSNode parent;

	private final MCTSNode[] children;
	private int expandedChildren;

	private int depth;
	private int numberOfVisits;
	private double totalScore;

	private Action moveToState;

	public MCTSNode(MCTSAgent agent, int childSize) {
		this(agent, null, null, childSize);
	}

	public MCTSNode(MCTSAgent agent, MCTSNode parent, Action action, int childSize) {
		this.agent = agent;
		this.parent = parent;
		this.numberOfVisits = 0;
		this.totalScore = 0;
		this.children = new MCTSNode[childSize];
		this.moveToState = action;

		if (parent == null) {
			depth = 0;
		} else {
			depth = parent.depth + 1;

			if (depth > deepest) {
				deepest = depth;
			}
		}
	}

	private MCTSNode expand(GameObservation obs) {

		List<Action> possibleActions = obs.getLegalActions(agent.getMyID());
		List<Integer> unexplored = new ArrayList<Integer>();

		for (int i = 0; i < possibleActions.size(); i++) {
			if (children[i] == null) {
				unexplored.add(i);
			}
		}

		if (unexplored.isEmpty()) {
			System.err.println(isFullyExpanded());
		}

		// select one of the actions
		Integer chosenIndex = agent.selectRandomly(unexplored);
		Action chosenAction = possibleActions.get(chosenIndex);
		children[chosenIndex] = new MCTSNode(agent, this, chosenAction, children.length);
		expandedChildren++;
		return children[chosenIndex];
	}

	public Action getBestAction() {
		// no children, return default move
		if (children == null) {
			return agent.getDefaultMove();
		}

		double bestScore = -Double.MAX_VALUE;
		List<Action> bestActions = new ArrayList<Action>();

		for (MCTSNode child : children) {
			if (child == null) {
				continue;
			}

			double childScore = child.getTotalScore();
			if (childScore > bestScore) {
				bestScore = childScore;
				bestActions.clear();
			} else if (childScore == bestScore) {
				bestActions.add(child.getMove());
			}
		}

		// if no actions are good, ask the agent for it's default position
		if (bestActions.isEmpty()) {
			return agent.getDefaultMove();
		}

		// if there are more than 1 "best" moves, select one randomly
		return agent.selectRandomly(bestActions);
	}

	public int getDeepestNode() {
		return deepest;
	}

	private Action getMove() {
		return moveToState;
	}

	private double getTotalScore() {
		return totalScore;
	}

	/**
	 * Get the best child (according to UCT).
	 *
	 * Ties are broken by asking the MCTS agent to decide for us.
	 *
	 * @return the best UCT child.
	 */
	public MCTSNode getUCTChild() {

		double bestScore = -Double.MAX_VALUE;
		List<MCTSNode> bestActions = new ArrayList<MCTSNode>();

		for (MCTSNode child : children) {
			if (child == null) {
				continue;
			}

			double childScore = child.getUCTScore();
			if (childScore > bestScore) {
				bestScore = childScore;
				bestActions.clear();
				bestActions.add(child);
			} else if (childScore == bestScore) {
				bestActions.add(child);
			}
		}

		assert !bestActions.isEmpty() : "No child was better than the worst possible penality";
		return agent.selectRandomly(bestActions);
	}

	// TODO wtf is all this epsilon stuff about?
	private double getUCTScore() {
		return totalScore / (numberOfVisits + EPSILON)
				+ Math.sqrt(EXP_CONST * Math.log(parent.numberOfVisits + 1) / (numberOfVisits + EPSILON));
	}

	public int getVisits() {
		return numberOfVisits;
	}

	private boolean isFullyExpanded() {
		return children.length == expandedChildren;
	}

	public MCTSNode select(int maxTreeDepth, GameObservation obs) {

		MCTSNode current = this;
		GameObservation currentObs = obs;

		while (current.depth < maxTreeDepth && !obs.hasWon()) {

			if (current.isFullyExpanded()) {
				// System.out.println("I have fully expanded my children!
				// "+depth);

				current = current.getUCTChild();
				currentObs.apply(current.moveToState, agent.getOppAction(currentObs));
			} else {
				MCTSNode expanded = current.expand(currentObs);
				if (expanded.moveToState == null) {
					throw new RuntimeException("BROKEN MCTS "+expanded.depth);
				}
				
				currentObs.apply(expanded.moveToState, agent.getOppAction(currentObs));
				return expanded;
			}

		}

		return current;
	}

	@Override
	public String toString() {
		return String.format("[%d] taking action %s (%f/%d)", depth, moveToState, totalScore, numberOfVisits);
	}

	public void update(double score) {
		MCTSNode current = this;
		while (current.parent != null) {
			current.totalScore += totalScore;
			current.numberOfVisits++;
			current = current.parent;
		}
		current.totalScore += totalScore;
		current.numberOfVisits++;
	}

}
