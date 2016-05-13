package uk.me.webpigeon.phd.tinycoop.controllers.prediction;

import java.util.List;
import java.util.Random;

import runner.experiment.Utils;
import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.controller.GameObservation;

/**
 * Assume the other agent will move randomly.
 */
public class RandomPolicy implements Policy {
	private int agentID;
	private Random random;
	
	@Override
	public void init(int agentID, int predictingID) {
		this.agentID = agentID;
		this.random = new Random();
	}

	@Override
	public Action getActionAt(GameObservation obs) {
		List<Action> legalActions = obs.getLegalActions(agentID);
		return Utils.getRandomAction(legalActions, random);
	}

	public String toString() {
		return "RandomPolicy";
	}
	
}
