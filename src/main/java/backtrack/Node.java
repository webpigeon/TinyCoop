package backtrack;

import FastGame.Action;
import gamesrc.GameState;

public class Node implements Comparable<Node> {
	public Node parent;
	public int cost;
	public int depth;
	public GameState state;
	public Action action;
	
	public boolean equals(Object object) {
		Node other = (Node)object;
		if (other != null) {
			return state.equals(other.state);
		}
		return false;
	}
	
	public String toString() {
		return String.format("%s{c:%d d:%d}", state, cost, depth);
	}

	public int compareTo(Node o) {
		return Integer.compare(cost, o.cost);
	}
}
