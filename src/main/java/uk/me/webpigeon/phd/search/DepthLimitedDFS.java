package uk.me.webpigeon.phd.search;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import uk.me.webpigeon.phd.tinycoop.api.GameState;

public class DepthLimitedDFS extends AbstractSearch {
	private Stack<Node> stack;
	private Set<GameState> discovered;
	private int limit;

	public DepthLimitedDFS(int limit) {
		this.stack = new Stack<>();
		this.discovered = new HashSet<>();
	}

	@Override
	public Node addNode(GameState state, ActionPair previous, Node parent) {
		if (discovered.contains(state)) {
			return null;
		}

		if (parent != null && limit > parent.depth) {
			return null;
		}

		Node node = new Node();
		node.cost = parent != null ? parent.cost + 1 : 0;
		node.depth = parent != null ? parent.depth + 1 : 0;
		node.state = state;
		stack.add(node);

		cameFrom.put(state, previous);
		return node;
	}

	@Override
	public boolean discover(Node node) {
		if (discovered.contains(node.state)) {
			return false;
		}

		discovered.add(node.state);
		return true;
	}

	@Override
	public Node getNext() {
		return stack.pop();
	}

	@Override
	public boolean isFinished() {
		return stack.isEmpty();
	}

}
