package Controllers.astar;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import api.Action;
import api.GameState;

/**
 * An implementation of A* for path finding
 */
public class PathFind {
	public static LinkedList<MovePair> buildPath(GameNode current, Map<GameNode, GameNode> cameFrom) {
		LinkedList<MovePair> path = new LinkedList<>();
		path.addFirst(current.actions);

		while (cameFrom.containsKey(current)) {
			current = cameFrom.get(current);
			path.addFirst(current.actions);
		}

		return path;
	}

	public static Queue<Action> findPath(GameState state, Point curr, Point goal) {
		return null;
	}

	public static List<MovePair> getAvailableMoves(GameState game) {

		List<MovePair> legalActions = new ArrayList<MovePair>();
		for (Action p1 : game.getLegalActions(0)) {
			for (Action p2 : game.getLegalActions(1)) {
				legalActions.add(new MovePair(p1, p2));
			}
		}

		return legalActions;
	}

	private Function<GameNode, Double> heristic;

	Map<GameNode, GameNode> cameFrom = new HashMap<>();

	Map<GameNode, Double> gScores = new HashMap<>();

	Map<GameNode, Double> fScores = new HashMap<>();

	public PathFind(Function<GameNode, Double> heristic) {
		this.heristic = heristic;
	}

	public List<MovePair> getPath(GameState game, GameNode start) {
		List<GameNode> closedSet = new ArrayList<GameNode>();
		Queue<GameNode> openSet = new PriorityQueue<GameNode>(10, new Comparator<GameNode>() {

			@Override
			public int compare(GameNode o1, GameNode o2) {
				System.out.println(o1);
				System.out.println(o2);
				Double score1 = fScores.get(o1);
				Double score2 = fScores.get(o2);
				return Double.compare(score1, score2) * -1;
			}
		});
		openSet.add(start);

		while (!openSet.isEmpty()) {
			GameNode current = openSet.poll();
			System.out.println(current);
			if (current.isTerminal()) {
				return buildPath(current, cameFrom);
			}
			openSet.remove(current);
			closedSet.add(current);

			for (MovePair moves : getAvailableMoves(game)) {
				GameState nState = current.game.getClone();
				nState.update(moves.p1Move, moves.p2Move);
				GameNode neighbor = new GameNode(nState, moves);

				if (closedSet.contains(neighbor)) {
					continue;
				}
				Double gScore = gScores.get(current);
				if (gScore == null) {
					gScore = 0.0;
				}
				gScore += 1;

				Double gScoreOther = gScores.get(neighbor);
				gScoreOther = gScoreOther == null ? 0 : gScoreOther;

				if (!openSet.contains(neighbor) || gScore > gScoreOther) {
					cameFrom.put(neighbor, current);
					gScores.put(neighbor, gScore);
					fScores.put(neighbor, gScore + heristic.apply(neighbor));
					if (!openSet.contains(neighbor)) {
						openSet.add(neighbor);
					}
				}
			}
		}

		return new LinkedList<MovePair>();
	}

}