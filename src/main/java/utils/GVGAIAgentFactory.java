package utils;

import uk.me.webpigeon.phd.gvgai.ElapsedCpuTimer;
import uk.me.webpigeon.phd.gvgai.StateObservationMulti;
import uk.me.webpigeon.phd.gvgai.controllers.olmcts.Agent;
import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;
import uk.me.webpigeon.phd.tinycoop.api.controller.GameObservation;
import uk.me.webpigeon.phd.tinycoop.controllers.GVGAIFacade;
import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;

public class GVGAIAgentFactory {

	public static Controller buildMCTS(SimpleGame game, int playerID) {
		GameObservation obs = game.getObservationFor(playerID);
		StateObservationMulti multiObs = new StateObservationMulti(obs, playerID);
		ElapsedCpuTimer timer = new ElapsedCpuTimer();
		timer.setMaxTimeMillis(System.currentTimeMillis()+40);
		
		Agent multiPlayer = new Agent(multiObs, timer, playerID);
		return new GVGAIFacade(multiPlayer);
	}
	
}
