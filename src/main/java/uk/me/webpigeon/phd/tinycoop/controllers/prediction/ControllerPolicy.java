package uk.me.webpigeon.phd.tinycoop.controllers.prediction;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;
import uk.me.webpigeon.phd.tinycoop.api.controller.GameObservation;

/**
 * Asks a controller what it would do in a given situation.
 */
public class ControllerPolicy implements Policy {
	private Controller controller;
	
	public ControllerPolicy(Controller controller){
		this.controller = controller;
	}

	@Override
	public void init(int agentID, int predictorAgent) {
		controller.startGame(agentID, predictorAgent);
	}

	@Override
	public Action getActionAt(GameObservation obs) {
		return controller.getAction(obs);
	}
	
	public String toString() {
		return controller.getFriendlyName();
	}

}