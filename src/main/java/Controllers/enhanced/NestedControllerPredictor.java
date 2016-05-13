package Controllers.enhanced;

import Controllers.PiersController;
import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.GameState;

public class NestedControllerPredictor implements Predictor {
	private PiersController controller;
	private int agentID;

	public NestedControllerPredictor(PiersController controller) {
		this.controller = controller;
	}

	@Override
	public void init(int agentID) {
		int oppID = agentID == 0 ? 1 : 0;
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

	@Override
	public String toString() {
		return controller.toString();
	}

}
