package uk.me.webpigeon.phd.tinycoop.engine.controllers;

import runner.clear.Result;
import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;
import uk.me.webpigeon.phd.tinycoop.api.controller.GameObservation;

public abstract class AbstractController implements Controller {
	private final String name;
	protected Integer myID;
	protected Integer theirID;

	public AbstractController(String name) {
		this.name = name;
	}

	@Override
	public String getFriendlyName() {
		return name;
	}

	@Override
	public void startGame(int myID, int theirID) {
		this.myID = myID;
		this.theirID = theirID;
	}
	
	@Override
	public void endGame(Result result, GameObservation state) {
		
	}

	@Override
	public String toString() {
		return getFriendlyName();
	}

}