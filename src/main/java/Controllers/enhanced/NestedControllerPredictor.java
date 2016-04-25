package Controllers.enhanced;

import Controllers.Controller;
import FastGame.FastAction;
import actions.Action;
import gamesrc.GameState;
import gamesrc.ObservableGameState;

public class NestedControllerPredictor implements Predictor {
	private Controller controller;
	private int agentID;
	
	public NestedControllerPredictor(Controller controller){
		this.controller = controller;
	}

	@Override
	public void init(int agentID) {
		int oppID = agentID == 0?1:0;
		this.agentID = agentID;
		controller.startGame(oppID);
	}
	
	@Override
	public void observe(int pid, Action... history) {
		
	}

	@Override
	public Action predict(int pid, GameState state) {
		return controller.get(state.getClone());
	}
	
	public String toString() {
		return controller.toString();
	}

}
