package backtrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FastGame.Action;
import gamesrc.GameState;

public abstract class AbstractSearch implements Search {
	protected Map<GameState, ActionPair> cameFrom;
	
	public AbstractSearch(){
		this.cameFrom = new HashMap<>();
	}
	
	@Override
	public List<Action> search(GameState start, GameState end) {
		addNode(start, null);
		
		while (!isFinished()) {
			Node node = getNext();
			if (discover(node)) {
				if (node.state.getScore() == 0.5) {
					return generatePath(node.state);
				}
				expand(node);
			}
			
		}

		return null;
	}

	
	public Action getOtherAgentsMove() {
		return Action.NOOP;
	}
	
	public void expand(Node node) {
		GameState parent = node.state;
		
		for (Action p1Action : parent.getLegalActions(0)) {
			/*for (Action p2Action : parent.getLegalActions(1) ) {*/
				GameState child = parent.getClone();
				child.update(p1Action, getOtherAgentsMove());
				addNode(child, p1Action, getOtherAgentsMove(), parent);
			/*}*/
		}
	}
	
	public boolean discover(Node node) {
		return true;
	}
	
	public void addNode(GameState state, Action p1action, Action p2action, GameState parent){
		if (p1action != null && p2action != null && parent != null) {
			ActionPair cameFrom = new ActionPair();
			cameFrom.state = parent;
			cameFrom.p1Action = p1action;
			cameFrom.p2Action = p2action;
			addNode(state, cameFrom);
		} else {
			addNode(state, null);
		}
	}
	
	public abstract Node addNode(GameState state, ActionPair cameFrom);
	
	public abstract Node getNext();
	
	public abstract boolean isFinished();
	
	public List<Action> generatePath(GameState end) {
		List<Action> actionList = new ArrayList<Action>();
		ActionPair previous = cameFrom.get(end);
		while (previous != null) {
			actionList.add(previous.p1Action);
			previous = cameFrom.get(previous.state);
		}
		return actionList;
	}
	
	class ActionPair {
		GameState state;
		Action p1Action;
		Action p2Action;
		
		public String toString() {
			return String.format("%s [%s,%s]", state, p1Action, p2Action);
		}
	}
}