package uk.me.webpigeon.controllers;

import java.util.List;
import java.util.Random;

import api.Action;
import api.controller.GameObservation;
import gamesrc.controllers.AbstractController;

public class RandomAgent extends AbstractController {
	private final Random random;

	public RandomAgent() {
		super("UniformRandom");
		this.random = new Random();
	}

	public RandomAgent(long seed) {
		super("UniformAgent");
		this.random = new Random(seed);
	}

	@Override
	public Action getAction(GameObservation state) {
		List<Action> myActions = state.getLegalActions(myID);
		return myActions.get(random.nextInt(myActions.size()));
	}

}
