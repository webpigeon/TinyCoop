package backtrack;

import java.util.Comparator;

public class MinimalCostNode implements Comparator<Node> {

	public int compare(Node o1, Node o2) {
		return Integer.compare(o1.cost, o2.cost);
	}

}
