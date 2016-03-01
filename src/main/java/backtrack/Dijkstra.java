package backtrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Dijkstra implements Search {
	private Map<State, Integer> distance;
	private Map<State, State> cameFrom;
	private PriorityQueue<Node> queue;
	
	public Dijkstra(){
		this.queue = new PriorityQueue<Node>();
		this.distance = new HashMap<State, Integer>();
		this.cameFrom = new HashMap<State, State>();
	}

	public List<State> search(State start, State end) {
		queue.add(build(start, null));
		distance.put(start, 0);
		
		while (!queue.isEmpty()){
			Node u = queue.poll();
			if (end.equals(u.state)) {
				return buildPath(u);
			}
			
			expand(u);			
		}

		return null;
	}

	public List<State> buildPath(Node node) {
		List<State> path = new ArrayList<State>();
		while (node != null) {
			path.add(node.state);
			node = node.parent;
		}
		
		return path;
	}
	
	public Node build(State state, Node parent) {
		Node node = new Node();
		node.parent = parent;
		node.cost = parent!=null?parent.cost + 1:0;
		node.depth = parent!=null?parent.depth + 1:0;
		node.state = state;
		return node;
	}
	
	public void expand(Node node) {
		for (State child : node.state.expand()) {
			int cost = distance.get(node.state) + child.getCost();
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
	
}
