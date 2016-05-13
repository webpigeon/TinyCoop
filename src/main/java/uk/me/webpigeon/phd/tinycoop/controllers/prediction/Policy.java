package uk.me.webpigeon.phd.tinycoop.controllers.prediction;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.controller.GameObservation;

public interface Policy {

	void init(int agentID, int myID);
	Action getActionAt(GameObservation obs);
	
}
