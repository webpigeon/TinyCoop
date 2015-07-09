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
		
		//TODO hard code some observations about the world - current pseudo code
		state.recordRule("LEFT(us)", "AT(us,x+1,y)"); 
		state.recordRule("RIGHT(us)","AT(us,x-1,y)"); 
		state.recordRule("UP(us)", "AT(x, y-1)");
		state.recordRule("DOWN(us)", "AT(x, y-1)");
		state.recordRule("ON(AGENT x, BUTTON y)", "+OPEN(door,y)"); 
		state.recordRule("!ON(AGENT x, BUTTON y)","-OPEN(door,y)"); 
		state.recordRule("AT(a, x, y) && AT(b, x, y)","ON(a, b)"); 
		
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
		state.recordObservation("AT(agent1,0,0)");
		state.recordObservation("AT(agent2,0,0)");
		state.recordObservation("AT(door1,0,0)");
		state.recordObservation("AT(door2,0,0)");
		state.recordObservation("AT(button1,0,0)");
		state.recordObservation("AT(button2,0,0)");
		
		//non-contextual actions (we know what we are allowed to do but not what effect they will have)
		for (String action : actionList.keySet()) {
			state.recordAction(action);
		}
		
		return state;
	}
	
}
