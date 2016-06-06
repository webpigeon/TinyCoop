package uk.me.webpigeon.phd.gvgai.controllers.polmcts.policy;

import uk.me.webpigeon.phd.gvgai.StateObservationMulti;
import uk.me.webpigeon.phd.gvgai.controllers.polmcts.PredictorAgent;
import uk.me.webpigeon.phd.tinycoop.api.Action;

/**
 * Return the first action strictly better than the current action.
 */
public class HillClimber implements GVGPolicy {
	
	@Override
	public Action getActionAt(Action myAction, StateObservationMulti multi) {
		
		double currScore = multi.getGameScore(PredictorAgent.oppID);
		
		Action[] actionSet = new Action[PredictorAgent.no_players];
		actionSet[PredictorAgent.id] = myAction;

		for (Action legalAction : PredictorAgent.actions[PredictorAgent.oppID]) {
			actionSet[PredictorAgent.oppID] = legalAction;
			
			StateObservationMulti clone = multi.copy();
			clone.advance(actionSet);
			
			if (currScore < clone.getGameScore(PredictorAgent.oppID)) {
				return legalAction;
			}
		}
		
		return Action.NOOP;
	}
	
	public String toString() {
		return "HillClimb";
	}

	@Override
	public void init(int myID, int oppID) {
		// TODO Auto-generated method stub
		
	}
	

}
