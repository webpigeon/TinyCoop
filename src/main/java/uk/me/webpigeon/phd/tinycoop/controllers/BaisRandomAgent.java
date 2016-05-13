package uk.me.webpigeon.phd.tinycoop.controllers;

import java.util.List;
import java.util.Random;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.controller.GameObservation;
import uk.me.webpigeon.phd.tinycoop.engine.Filters;
import uk.me.webpigeon.phd.tinycoop.engine.controllers.AbstractController;

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
