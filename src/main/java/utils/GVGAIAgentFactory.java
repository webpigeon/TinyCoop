package utils;

import uk.me.webpigeon.phd.gvgai.ElapsedCpuTimer;
import uk.me.webpigeon.phd.gvgai.StateObservationMulti;
import uk.me.webpigeon.phd.gvgai.controllers.olmcts.Agent;
import uk.me.webpigeon.phd.gvgai.controllers.paired.olmcts.MirrorAgent;
import uk.me.webpigeon.phd.gvgai.controllers.polmcts.PredictorAgent;
import uk.me.webpigeon.phd.gvgai.controllers.polmcts.policy.HillClimber;
import uk.me.webpigeon.phd.gvgai.controllers.polmcts.policy.NestedControllerPolicy;
import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;
import uk.me.webpigeon.phd.tinycoop.api.controller.GameObservation;
import uk.me.webpigeon.phd.tinycoop.controllers.GVGAIFacade;
import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;

public class GVGAIAgentFactory {
	private static final long stdMaxTime = 40;
	private static final long nestedMaxTime = 2;
	
	public static Controller buildMCTS(SimpleGame game, int playerID) {
		GameObservation obs = game.getObservationFor(playerID);
		StateObservationMulti multiObs = new StateObservationMulti(obs, playerID);
		ElapsedCpuTimer timer = new ElapsedCpuTimer();
		timer.setMaxTimeMillis(stdMaxTime);
		
		Agent multiPlayer = new Agent(multiObs, timer, playerID);
		return new GVGAIFacade(multiPlayer, stdMaxTime);
	}
	
	public static Controller buildMCTSPredictor(SimpleGame game, int playerID) {
		GameObservation obs = game.getObservationFor(playerID);
		StateObservationMulti multiObs = new StateObservationMulti(obs, playerID);
		ElapsedCpuTimer timer = new ElapsedCpuTimer();
		timer.setMaxTimeMillis(stdMaxTime);
		
		PredictorAgent multiPlayer = new PredictorAgent(multiObs, timer, playerID, new HillClimber());
		return new GVGAIFacade(multiPlayer, stdMaxTime);
	}
	
	public static Controller buildMCTSPredictor(SimpleGame game, int playerID, Controller other) {
		GameObservation obs = game.getObservationFor(playerID);
		StateObservationMulti multiObs = new StateObservationMulti(obs, playerID);
		ElapsedCpuTimer timer = new ElapsedCpuTimer();
		timer.setMaxTimeMillis(stdMaxTime);
		
		PredictorAgent multiPlayer = new PredictorAgent(multiObs, timer, playerID, new NestedControllerPolicy(other));
		return new GVGAIFacade(multiPlayer, stdMaxTime);
	}

	public static Controller buildMirrorMCTS(SimpleGame game, int playerID) {
		GameObservation obs = game.getObservationFor(playerID);
		StateObservationMulti multiObs = new StateObservationMulti(obs, playerID);
		ElapsedCpuTimer timer = new ElapsedCpuTimer();
		timer.setMaxTimeMillis(nestedMaxTime);
		
		MirrorAgent multiPlayer = new MirrorAgent(multiObs, timer, playerID);
		return new GVGAIFacade(multiPlayer, nestedMaxTime);
	}
	
}
