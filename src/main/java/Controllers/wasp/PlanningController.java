package Controllers.wasp;

import java.util.Map;
import java.util.TreeMap;

import Controllers.Controller;
import FastGame.Action;
import FastGame.CoopGame;
import uk.me.webpigeon.planner.wasp.Wasp;
import uk.me.webpigeon.planner.wasp.WaspState;

public class PlanningController extends Controller {
	private Wasp wasp;
	private Map<String, Action> actionList;
	
	public PlanningController() {
		this.wasp = new Wasp();
		this.actionList = new TreeMap<String, Action>();
		actionList.put("LEFT", Action.LEFT);
		actionList.put("RIGHT", Action.RIGHT);
		actionList.put("UP", Action.UP);
		actionList.put("DOWN", Action.DOWN);
		actionList.put("WAIT", Action.NOOP);
	}

	@Override
	public Action get(CoopGame game) {
		WaspState state = doSense(game);
		String nextAction = wasp.step(state);
		
		if (nextAction == null) {
			System.err.println("planner gave up, sit still");
			return Action.NOOP;
		}
		
		return actionList.get(nextAction);
	}
	
	protected WaspState doSense(CoopGame game) {
		
		WaspState state = new WaspState();
		
		//TODO hard code some observations about the world
		
		//what objects are in the world and what sort of objects are they?
		state.recordVariable("agent", "player1");
		state.recordVariable("agent", "player2");
		state.recordVariable("door", "door1");
		state.recordVariable("door", "door2");
		state.recordVariable("button", "button1");
		state.recordVariable("button", "button2");
		
		// what we know about the current state of the world
		state.recordObservation("open(door1)");
		state.recordObservation("open(door2)");
		state.recordObservation("at(agent1,button)");
		state.recordObservation("at(agent2, room1)");
		
		//non-contextual actions (we know what we are allowed to do but not what effect they will have)
		for (String action : actionList.keySet()) {
			state.recordAction(action);
		}
		
		return state;
	}
	
}
