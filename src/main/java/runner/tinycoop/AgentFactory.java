package runner.tinycoop;

import Controllers.MCTS;
import api.controller.Controller;
import uk.me.webpigeon.controllers.BaisRandomAgent;
import uk.me.webpigeon.controllers.FlareFollowingAgent;
import uk.me.webpigeon.controllers.RandomAgent;
import uk.me.webpigeon.controllers.mcts.MCTSAgent;

public class AgentFactory {
	
	/**
	 * Build an agent with a uniform chance to select any action.
	 * 
	 * @return
	 */
	public static Controller buildRandomAgent() {
		return new RandomAgent();
	}
	
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

	public static Controller buildStandardMCTS() {
		return new MCTSAgent(500, 10, 25);
	}

}
