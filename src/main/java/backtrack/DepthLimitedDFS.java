package backtrack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class DepthLimitedDFS implements Search {
	private Stack<Node> stack;
	private Set<State> discovered;
	private int depth;
	
	public DepthLimitedDFS(int maxDepth) {
		this.stack = new Stack<Node>();
		this.discovered = new HashSet<State>();
		this.depth = maxDepth;
	}
	
	
	public List<State> search(State start, State goal) {
		Node startNode = new Node();
		startNode.cost = 0;
		startNode.depth = 0;
		startNode.parent = null;
		startNode.state = start;
		stack.add(build(start, null));
		
		while(!stack.isEmpty()) {
			System.out.println(stack);
			Node node = nextNode();
			if (!discovered.contains(node.state)) {
				
				discovered.add(node.state);
				if (goal.equals(node.state)) {
					return buildPath(node);
				}
				
				if (node.depth < depth) {
					expand(node);
				}
			}
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
	
	public void expand(Node state) {
		for (State child : state.state.expand()) {
			stack.add(build(child, state));
		}
	}
	
	public Node nextNode() {
		return stack.pop();
	}
	
	
}
