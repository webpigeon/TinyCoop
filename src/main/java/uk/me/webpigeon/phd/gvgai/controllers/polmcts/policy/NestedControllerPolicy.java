package uk.me.webpigeon.phd.gvgai.controllers.polmcts.policy;

import uk.me.webpigeon.phd.gvgai.StateObservationMulti;
import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;

public class NestedControllerPolicy implements GVGPolicy {
	private final Controller controller;
	
	public NestedControllerPolicy(Controller controller) {
		this.controller = controller;
	}
	
	public void init(int myID, int theirID) {
		controller.startGame(myID, theirID);
	}

	@Override
	public Action getActionAt(Action myAction, StateObservationMulti multi) {
		return controller.getAction(multi.getGameState());
	}

	public String toString() {
		return controller.getFriendlyName();
	}
	
}
