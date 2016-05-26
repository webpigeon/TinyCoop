package uk.me.webpigeon.phd.gvgai.controllers.polmcts.policy;

import uk.me.webpigeon.phd.gvgai.StateObservationMulti;
import uk.me.webpigeon.phd.gvgai.controllers.polmcts.PredictorAgent;
import uk.me.webpigeon.phd.tinycoop.api.Action;

public class OneStepLookAhead implements GVGPolicy {
	
	@Override
	public Action getActionAt(Action myAction, StateObservationMulti multi) {
		
		Action[] legalActions = PredictorAgent.actions[PredictorAgent.oppID];
		
		double bestScore = -99999;
		Action bestAction = Action.NOOP;
		
		Action[] actionSet = new Action[PredictorAgent.no_players];
		actionSet[PredictorAgent.id] = myAction;

		for (Action legalAction : legalActions) {
			actionSet[PredictorAgent.oppID] = legalAction;
			
			StateObservationMulti clone = multi.copy();
			clone.advance(actionSet);
			
			if (bestScore < clone.getGameScore(PredictorAgent.oppID)) {
				bestScore = clone.getGameScore(PredictorAgent.oppID);
				bestAction = legalAction;
			}
		}
		
		return bestAction;
	}
	
	

}
