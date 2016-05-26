package Controllers;

import runner.clear.Result;
import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.GameState;
import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;
import uk.me.webpigeon.phd.tinycoop.api.controller.GameObservation;

/**
 * Created by pwillic on 23/06/2015.
 * 
 * Modified to implement new controller interface by JWR.
 * All new methods are final to prevent accidental overriding, extend abstract
 * controller instead of this one.
 */
@Deprecated
public abstract class PiersController implements Controller {

	@Override
	public final String toString() {
		return getFriendlyName();
	}

	/**
	 * Proxy the controller's getAction to pier's get
	 */
	@Override
	public final Action getAction(GameObservation state) {
		return get(state);
	}

	/**
	 * Proxy the controller's getFriendlyName to pier's getSimpleName
	 */
	@Override
	public final String getFriendlyName() {
		return getSimpleName();
	}

	/**
	 * Proxy startGame(us,them) to startGame(id)
	 */
	@Override
	public final void startGame(int myID, int theirID) {
		startGame(myID);
	}
	
	
	/*
	 * Original API BELOW THIS POINT
	 */
	
	public Action get(GameState game) {
		return Action.NOOP;
	}

	public PiersController getClone() {
		return this;
	}

	public String getSimpleName() {
		return this.getClass().getSimpleName();
	}

	public void startGame(int agentID) {

	}

	@Override
	public void endGame(Result result, GameObservation state) {
		// TODO Auto-generated method stub
		
	}
	
}
