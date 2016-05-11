package uk.me.webpigeon.controllers.mcts;

import api.Action;
import api.controller.GameObservation;
import uk.me.webpigeon.controllers.prediction.Policy;

/**
 * Open Loop MCTS implementation.
 *
 * This is the same basic code as Pier's MCTS player but tweaked for
 * readability.
 */
public class MCTSPredictorAgent extends MCTSAgent {
	private Policy policy;

	public MCTSPredictorAgent(int iterationLimit, int treeDepth, int rolloutDepth, Policy policy) {
		super(iterationLimit, treeDepth, rolloutDepth);
		this.policy = policy;
	}
	
	@Override
	public void startGame(int myID, int theirID) {
		super.startGame(myID, theirID);
		policy.init(theirID, myID);
	}

	public Action getOppAction(GameObservation obs) {	
		GameObservation theirs = obs.fromPerspective(theirID);
		return policy.getActionAt(theirs);
	}

	@Override
	public String getFriendlyName() {
		return super.getFriendlyName()+"["+policy+"]";
	}

}
