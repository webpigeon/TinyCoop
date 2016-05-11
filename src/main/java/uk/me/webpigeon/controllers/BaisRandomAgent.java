package uk.me.webpigeon.controllers;

import java.util.List;
import java.util.Random;

import api.Action;
import api.controller.GameObservation;
import gamesrc.Filters;
import gamesrc.controllers.AbstractController;

public class BaisRandomAgent extends AbstractController {
	private final Random random;
	private final Double commChance;

	public BaisRandomAgent() {
		super("BiasRandom");
		this.random = new Random();
		this.commChance = 0.25;
	}

	@Override
	public Action getAction(GameObservation state) {
		List<Action> myActions = state.getLegalActions(myID);

		List<Action> choices;
		if (random.nextDouble() < commChance) {
			choices = Filters.filterTalk(myActions);
		} else {
			choices = Filters.filterMovement(myActions);
		}

		return choices.get(random.nextInt(choices.size()));
	}

}
