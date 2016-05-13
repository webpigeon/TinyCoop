package uk.me.webpigeon.phd.search;

import java.util.Comparator;

public class MinimalCostNode implements Comparator<Node> {

	@Override
	public int compare(Node o1, Node o2) {
		return Integer.compare(o1.cost, o2.cost);
	}

}
