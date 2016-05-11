package uk.me.webpigeon.controllers.prediction;

import java.util.List;
import java.util.Random;

import api.Action;
import api.controller.GameObservation;
import runner.experiment.Utils;

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

}
