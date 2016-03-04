package backtrack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import FastGame.Action;
import gamesrc.GameState;

public class BredthFirstSearch extends AbstractSearch {
	private Queue<Node> stack;
	private Set<GameState> discovered;
	
	public BredthFirstSearch() {
		this.stack = new LinkedList<>();
		this.discovered = new HashSet<>();
	}
	
	public boolean discover(Node node) {
		discovered.add(node.state);
		return true;
	}

	@Override
	public Node getNext() {
		return stack.poll();
	}

	@Override
	public boolean isFinished() {
		return stack.isEmpty();
	}
	

	@Override
	public Node addNode(GameState state, ActionPair previous) {
		if (discovered.contains(state)) {
			return null;
		}
		
		System.out.println("expanding: "+previous);
		Node node = new Node();
		//node.cost = parent!=null?parent.cost + 1:0;
		//node.depth = parent!=null?parent.depth + 1:0;
		node.state = state;
		stack.add(node);
		
		cameFrom.put(state, previous);
		return node;
	}
	
}
