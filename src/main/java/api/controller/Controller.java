package api.controller;

import api.Action;

/**
 * A simplistic wrapper for agents.
 * 
 * This is designed to make writing controllers less error prone.
 */
public interface Controller {

	public void startGame(int myID, int theirID);
	public Action getAction(GameObservation state);
	public String getFriendlyName();
	
}
