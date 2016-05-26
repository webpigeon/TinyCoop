package uk.me.webpigeon.phd.tinycoop.controllers;

import runner.clear.Result;
import uk.me.webpigeon.phd.gvgai.AbstractMultiPlayer;
import uk.me.webpigeon.phd.gvgai.ElapsedCpuTimer;
import uk.me.webpigeon.phd.gvgai.StateObservationMulti;
import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.controller.GameObservation;
import uk.me.webpigeon.phd.tinycoop.engine.controllers.AbstractController;

/**
 * Expose GVGAI agents as if they were tinycoop agents.
 */
public class GVGAIFacade extends AbstractController {
	private AbstractMultiPlayer gvgAgent;
	
	public GVGAIFacade(AbstractMultiPlayer gvgAgent) {
		super("GVG-"+gvgAgent);
		this.gvgAgent = gvgAgent;
	}

	@Override
	public void startGame(int myID, int theirID) {
		super.startGame(myID, theirID);
		gvgAgent.setup(null, 0, false);
	}

	@Override
	public Action getAction(GameObservation state) {
		
		StateObservationMulti multiObs = new StateObservationMulti(state, myID);
		ElapsedCpuTimer timer = new ElapsedCpuTimer();
		timer.setMaxTimeMillis(40);
		
		Action gvgAction = gvgAgent.act(multiObs, timer);
		
		if (timer.exceededMaxTime()) {
			System.err.println("Controller exceeded max time: doing noop");
			return Action.NOOP;
		}
		
		return gvgAction;
	}

	@Override
	public void endGame(Result result, GameObservation state) {
		StateObservationMulti multiObs = new StateObservationMulti(state, myID);
		ElapsedCpuTimer timer = new ElapsedCpuTimer();
		timer.setMaxTimeMillis(System.currentTimeMillis()+40);
		
		gvgAgent.result(multiObs, timer);
		
	}
	
	

}