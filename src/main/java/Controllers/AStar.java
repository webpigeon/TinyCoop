package Controllers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import FastGame.CoopGame;
import FastGame.FastAction;
import FastGame.ObjectTypes;
import api.Action;

/**
 * An implementation of A* for path finding
 */
public class AStar extends Controller {

	static class ActionNode {
		Action p1action;
		Action p2action;
		CoopGame game;
		int depth = 0;

		ActionNode(Action action, Action other, CoopGame game, int depth) {
			this.p1action = action;
			this.p2action = other;
			this.game = game;
			this.depth = 0;
		}

		@Override
		public boolean equals(Object o) {
			ActionNode other = (ActionNode) o;
			return p1action.equals(other.p1action) && p2action.equals(other.p2action) && gameEquals(game, other.game);
		}

		@Override
		public int hashCode() {
			return Objects.hash(p1action, p2action, game);
		}

		// TODO Auto-generated method stub
		@Override
		public String toString() {
			return p1action + "+" + p2action + "@" + game.getScore();
		}
	}

	static class fScoreMetric implements Comparator<ActionNode> {
		private Map<ActionNode, Double> scores;

		fScoreMetric(Map<ActionNode, Double> scores) {
			this.scores = scores;
		}

		@Override
		public int compare(ActionNode o1, ActionNode o2) {
			return Double.compare(scores.get(o1), scores.get(o2));
		}
	}

	public static LinkedList<api.Action> buildPath(ActionNode current, Map<ActionNode, ActionNode> cameFrom,
			boolean first) {
		LinkedList<api.Action> path = new LinkedList<>();
		if (first) {
			path.addFirst(current.p1action);
		} else {
			path.addFirst(current.p2action);
		}

		while (cameFrom.containsKey(current)) {
			current = cameFrom.get(current);
			if (first) {
				path.addFirst(current.p1action);
			} else {
				path.addFirst(current.p2action);
			}
		}

		return path;
	}

	// figure out if two world states are the same (needed to avoid loops)
	// XXX needs to take into account player positions and goal hit times
	static boolean gameEquals(CoopGame g1, CoopGame g2) {
		if (g1.getScore() != g2.getScore()) {
			return false;
		}

		if (g1.hasWon() != g2.hasWon()) {
			return false;
		}

		if (g1.doorOpen(0) != g2.doorOpen(0)) {
			return false;
		}

		for (int i = 0; i < 2; i++) {
			if (g1.getPos(i) != null && g2.getPos(i) != null) {
				if (!g1.getPos(i).equals(g2.getPos(i))) {
					return false;
				}
			}
		}

		return true;
	}

	public static List<ActionNode> getAvailableMoves(ActionNode current, boolean isFirst) {
		List<ActionNode> nextActions = new ArrayList<>();
		for (Action action : current.game.getLegalActions(isFirst ? 0 : 1)) {
			// Action other = Action.getRandom();
			Action other = Action.NOOP;
			CoopGame clone = current.game.getClone();

			// so that's what the isFirst is for ;P
			// TODO evaluate game state when the node is selected to save time
			if (isFirst) {
				clone.update(action, other); // TODO max/max tree to avoid
												// random moves
			} else {
				clone.update(other, action); // TODO max/max tree to avoid
												// random moves
			}

			ActionNode next = new ActionNode(action, other, clone, current.depth + 1);
			nextActions.add(next);
		}
		return nextActions;
	}

	// Hersitic function
	// TODO heristic function
	public static double getDistance(ActionNode start, List<Point> goals, boolean first) {
		Point myPos = start.game.getPos(first ? 0 : 1);

		int total = 0;
		for (Point goal : goals) {
			total += goal.distanceSq(myPos); // TODO where am i? o.O
		}

		return total;
	}

	public static List<Point> getGoals(CoopGame cg) {
		List<Point> goals = new ArrayList<Point>();

		for (int x = 0; x < cg.getWidth(); x++) {
			for (int y = 0; y < cg.getHeight(); y++) {
				for (int l = 0; l < cg.getLayers(); l++) {
					if (cg.get(x, y, l) == ObjectTypes.GOAL) {
						goals.add(new Point(x, y));
					}
				}
			}
		}

		return goals;
	}

	public static Queue<api.Action> getPath(CoopGame game, ActionNode start, boolean isFirst) {
		int maxExpands = 25;

		List<Point> goals = getGoals(game);

		List<ActionNode> closedSet = new ArrayList<ActionNode>();
		Map<ActionNode, ActionNode> cameFrom = new HashMap<>();
		Map<ActionNode, Double> gScores = new HashMap<>();
		Map<ActionNode, Double> fScores = new HashMap<>();
		List<ActionNode> openSet = new ArrayList<ActionNode>();
		openSet.add(start);

		while (!openSet.isEmpty()) {
			ActionNode current = openSet.get(0);
			// System.out.println("open: "+openSet);
			// System.out.println("closed: "+closedSet);
			if (current.game.hasWon() || current.game.getScore() == 0.5) {
				return buildPath(current, cameFrom, isFirst);
			}
			openSet.remove(current);
			closedSet.add(current);

			for (ActionNode neighbor : getAvailableMoves(current, isFirst)) {
				if (closedSet.contains(neighbor)) {
					continue;
				}
				Double gScore = gScores.get(current);
				gScore = gScore != null ? gScore : 0;
				gScore += 1; // 'cost' to get to this state

				Double otherScore = gScores.get(neighbor);
				otherScore = otherScore != null ? otherScore : 0;

				if (!openSet.contains(neighbor) || gScore > otherScore) {
					cameFrom.put(neighbor, current);
					gScores.put(neighbor, gScore);
					fScores.put(neighbor, gScore + getDistance(neighbor, goals, isFirst));
					if (!openSet.contains(neighbor)) {
						openSet.add(neighbor);
					}
				}
			}
		}

		// we didn't find a path
		return new LinkedList<api.Action>();
	}

	private boolean isFirst;

	public AStar(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public Action get(CoopGame game) {
		// System.out.println("started");

		ActionNode start = new ActionNode(FastAction.NOOP, FastAction.NOOP, game, 0);
		Queue<Action> actionPath = AStar.getPath(game, start, isFirst);

		if (actionPath.isEmpty()) {
			System.out.println("bail out");
			return Action.NOOP;
		}

		// A star (thinks) it is finding solutions but they all start with no-op
		// and so don't go very well.
		System.out.println(actionPath);
		actionPath.poll();
		return actionPath.poll();
	}

}
