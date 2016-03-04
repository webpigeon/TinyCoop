package backtrack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import FastGame.Action;
import backtrack.AbstractSearch.ActionPair;
import gamesrc.GameState;

public class DepthFirstSearch extends AbstractSearch {
	private Stack<Node> stack;
	private Set<GameState> discovered;
	
	public DepthFirstSearch() {
		this.stack = new Stack<>();
		this.discovered = new HashSet<>();
	}
	
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


	@Override
	public Node addNode(GameState state, ActionPair previous) {
		if (discovered.contains(state)) {
			return null;
		}
		
		Node node = new Node();
		//node.cost = parent!=null?parent.cost + 1:0;
		//node.depth = parent!=null?parent.depth + 1:0;
		node.state = state;
		stack.add(node);
		
		cameFrom.put(state, previous);
		return node;
	}
	
	
}
