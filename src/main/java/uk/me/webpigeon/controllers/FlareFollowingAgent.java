package uk.me.webpigeon.controllers;

import java.awt.Point;
import java.util.List;

import api.Action;
import api.Flare;
import api.controller.GameObservation;
import gamesrc.Filters;
import gamesrc.controllers.AbstractController;

public class FlareFollowingAgent extends AbstractController {

	public FlareFollowingAgent() {
		super("FlareFollower");
	}

	@Override
	public Action getAction(GameObservation state) {
		List<Action> myActions = state.getLegalActions(myID);
		List<Action> movementActions = Filters.filterMovement(myActions);

		Flare flare = state.getFlare(theirID);
		if (flare == null) {
			return Action.NOOP;
		}
		assert flare.pid == myID : "the other agent appears to have flared someone else";

		// find the target pos and location
		Point myLocation = state.getPos(myID);
		Point targetPos = flare.toAbs(state.getPos(flare.pid));

		// find a matching movement action and return it
		for (Action legalAction : movementActions) {
			Point movementTarget = new Point(legalAction.getX(), legalAction.getY());

			// if it's relative, we need to add our location to it
			if (legalAction.isRelative()) {
				movementTarget.x += myLocation.x;
				movementTarget.y += myLocation.y;
			}

			if (targetPos.equals(movementTarget)) {
				return legalAction;
			}
		}

		// no action matched, not sure what happened, noop
		return Action.NOOP;
	}

}
