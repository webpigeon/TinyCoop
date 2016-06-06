package uk.me.webpigeon.phd.gvgai.controllers.polmcts.policy;

import java.util.Random;

import uk.me.webpigeon.phd.gvgai.StateObservationMulti;
import uk.me.webpigeon.phd.gvgai.controllers.polmcts.PredictorAgent;
import uk.me.webpigeon.phd.tinycoop.api.Action;

public class RandomPolicy implements GVGPolicy {

	@Override
	public Action getActionAt(Action myAction, StateObservationMulti multi) {
        Action[] oppActions = PredictorAgent.actions[PredictorAgent.oppID];
        return oppActions[new Random().nextInt(oppActions.length)];
	}

	@Override
	public void init(int myID, int oppID) {
		// TODO Auto-generated method stub
		
	}

}
