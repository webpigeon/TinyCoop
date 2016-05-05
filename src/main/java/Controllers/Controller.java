package Controllers;

import api.Action;
import api.GameState;

/**
 * Created by pwillic on 23/06/2015.
 */
public abstract class Controller {

	public Action get(GameState game) {
		return Action.NOOP;
	}

	public Controller getClone() {
		return this;
	}

	public String getSimpleName() {
		return this.getClass().getSimpleName();
	}

	public void startGame(int agentID) {

	}

	@Override
	public String toString() {
		return getSimpleName();
	}
}
