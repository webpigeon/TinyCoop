package utils;

import api.controller.Controller;
import uk.me.webpigeon.controllers.BaisRandomAgent;
import uk.me.webpigeon.controllers.FlareFollowingAgent;
import uk.me.webpigeon.controllers.RandomAgent;
import uk.me.webpigeon.controllers.mcts.MCTSAgent;
import uk.me.webpigeon.controllers.mcts.MCTSPredictorAgent;
import uk.me.webpigeon.controllers.prediction.Policy;

public class AgentFactory {

	/**
	 * Build an agent which has a bias towards movement.
	 *
	 * @return
	 */
	public static Controller buildBiasRandomAgent() {
		return new BaisRandomAgent();
	}

	/**
	 * Build an agent which will follow flares.
	 *
	 * @return
	 */
	public static Controller buildFlareFollower() {
		return new FlareFollowingAgent();
	}

	/**
	 * Build an agent with a uniform chance to select any action.
	 *
	 * @return
	 */
	public static Controller buildRandomAgent() {
		return new RandomAgent();
	}

	/**
	 * The standard open loop MCTS created by Piers Williams.
	 * 
	 * @return Pier's MCTS agent
	 */
	public static Controller buildStandardMCTS() {
		return new MCTSAgent(500, 10, 25);
	}
	
	/**
	 * Build an MCTS agent with a customised policy for the other agent.
	 * 
	 * @return The MCTS agent which has a policy for the other agent
	 */
	public static Controller buildPredictorMCTS(Policy policy){
		return new MCTSPredictorAgent(500, 10, 25, policy);
	}

}
