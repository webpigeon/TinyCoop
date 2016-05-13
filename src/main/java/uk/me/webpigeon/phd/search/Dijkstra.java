package uk.me.webpigeon.phd.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.GameState;

public class Dijkstra implements Search {
	private Map<GameState, Integer> distance;
	private Map<GameState, GameState> cameFrom;
	private PriorityQueue<Node> queue;

	public Dijkstra() {
		this.queue = new PriorityQueue<Node>();
		this.distance = new HashMap<>();
		this.cameFrom = new HashMap<>();
	}

	public Node build(GameState state, Node parent) {
		Node node = new Node();
		node.parent = parent;
		node.cost = parent != null ? parent.cost + 1 : 0;
		node.depth = parent != null ? parent.depth + 1 : 0;
		node.state = state;
		return node;
	}

	public List<Action> buildPath(Node node) {
		List<Action> path = new ArrayList<Action>();
		while (node != null) {
			path.add(node.action);
			node = node.parent;
		}

		return path;
	}

	public void expand(Node node, int pid) {
		GameState parent = node.state.getClone();

		for (Action action : parent.getLegalActions(pid)) {
			GameState child = parent.getClone();
			child.update(getOtherAgentMove(), action);

			int cost = distance.get(node.state) + getCost(child);
			Integer currCost = distance.get(child);
			if (currCost == null) {
				currCost = Integer.MAX_VALUE;
			}

			if (cost < currCost) {
				distance.put(child, cost);
				cameFrom.put(child, node.state);

				queue.remove(build(child, node));
				queue.add(build(child, node));
			}

		}
	}

	public int getCost(GameState state) {
		return 1;
	}

	public Action getOtherAgentMove() {
		return Action.NOOP;
	}

	@Override
	public List<Action> search(GameState start, GameState end) {
		queue.add(build(start, null));
		distance.put(start, 0);

		while (!queue.isEmpty()) {
			Node u = queue.poll();
			if (end.equals(u.state)) {
				return buildPath(u);
			}

			expand(u, 1);
		}

		return null;
	}

}
