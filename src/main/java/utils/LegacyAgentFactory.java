package utils;

import Controllers.FollowTheFlare;
import Controllers.MCTS;
import Controllers.PiersController;
import Controllers.RandomController;
import Controllers.SortOfRandomController;
import Controllers.enhanced.NestedControllerPredictor;
import Controllers.enhanced.Predictor;
import Controllers.enhanced.PredictorMCTS;
import Controllers.enhanced.RandomPredictor;

/**
 * Build original Agents with the proxy methods in place.
 */
public class LegacyAgentFactory {
	private static final Integer NUM_ITERS = 500;
	private static final Integer UCT_BORDER = 10;
	private static final Integer NUM_ROLLOUTS = 25;
	
	public static MCTS buildStandardPiersMCTS(boolean isFirst) {
		return new MCTS(isFirst, NUM_ITERS, UCT_BORDER, NUM_ROLLOUTS);
	}
	
	public static PredictorMCTS buildStandardMCTS() {
		Predictor random = new RandomPredictor();
		PredictorMCTS mcts = new PredictorMCTS(500, UCT_BORDER, NUM_ROLLOUTS, random);
		mcts.tag = "standard";
		return mcts;
	}
	
	public static PredictorMCTS buildHighPredictor(PiersController predicted) {
		Predictor predictor = new NestedControllerPredictor(predicted);
		PredictorMCTS mcts = new PredictorMCTS(NUM_ITERS, UCT_BORDER, NUM_ROLLOUTS, predictor);
		mcts.tag = "mirror";
		return mcts;
	}
	
	public static PredictorMCTS buildHighMCTS2() {
		PredictorMCTS predicted = buildStandardMCTS();
		PredictorMCTS mcts = buildHighPredictor(predicted);
		mcts.tag = "2mcts";
		return mcts;
	}
	
	public static PiersController buildBiasRandomAgent() {
		return new SortOfRandomController();
	}
	
	public static PiersController buildRandomAgent() {
		return new RandomController();
	}
	
	public static PiersController buildFlareFollower() {
		return new FollowTheFlare();
	}
	
}
