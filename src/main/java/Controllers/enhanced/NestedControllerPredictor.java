package Controllers.enhanced;

import Controllers.Controller;
import FastGame.Action;
import gamesrc.GameState;

public class NestedControllerPredictor implements Predictor {
	private Controller controller;
	
	public NestedControllerPredictor(Controller controller){
		this.controller = controller;
	}

	@Override
	public void init(int agentID) {
		int oppID = agentID == 0?1:0;
		controller.startGame(oppID);
	}
	
	@Override
	public void observe(int pid, Action... history) {
		
	}

	@Override
	public Action predict(int pid, GameState state) {
		return controller.get(state.getClone());
	}

}
