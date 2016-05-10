package gamesrc.controllers;

import api.Action;
import api.controller.Controller;
import api.controller.GameObservation;

public abstract class AbstractController implements Controller {
	private final String name;
	protected Integer myID;
	protected Integer theirID;

	public AbstractController(String name) {
		this.name = name;
	}
	
	@Override
	public void startGame(int myID, int theirID) {
		this.myID = myID;
		this.theirID = theirID;
	}
	
	@Override
	public String getFriendlyName() {
		return name;
	}

	@Override
	public String toString() {
		return getFriendlyName();
	}
	
}
