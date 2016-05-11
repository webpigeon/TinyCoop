package uk.me.webpigeon.controllers.prediction;

import api.Action;
import api.controller.GameObservation;

public interface Policy {

	void init(int agentID, int myID);
	Action getActionAt(GameObservation obs);
	
}
