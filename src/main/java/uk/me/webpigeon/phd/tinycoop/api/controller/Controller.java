package uk.me.webpigeon.phd.tinycoop.api.controller;

import uk.me.webpigeon.phd.tinycoop.api.Action;

/**
 * A simplistic wrapper for agents.
 *
 * This is designed to make writing controllers less error prone.
 */
public interface Controller {

	public Action getAction(GameObservation state);

	public String getFriendlyName();

	public void startGame(int myID, int theirID);

}
