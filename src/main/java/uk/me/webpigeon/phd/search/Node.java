package uk.me.webpigeon.phd.search;

import FastGame.FastAction;
import uk.me.webpigeon.phd.tinycoop.api.GameState;

public class Node implements Comparable<Node> {
	public Node parent;
	public int cost;
	public int depth;
	public GameState state;
	public FastAction action;

	@Override
	public int compareTo(Node o) {
		return Integer.compare(cost, o.cost);
	}

	@Override
	public boolean equals(Object object) {
		Node other = (Node) object;
		if (other != null) {
			return state.equals(other.state);
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%s{c:%d d:%d}", state, cost, depth);
	}
}
