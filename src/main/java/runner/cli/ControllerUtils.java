package runner.cli;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Controllers.Controller;
import Controllers.FollowTheFlare;
import Controllers.MCTS;
import Controllers.PassiveRefindController;
import Controllers.RandomController;
import Controllers.SortOfRandomController;
import Controllers.enhanced.NestedControllerPredictor;
import Controllers.enhanced.Predictor;
import Controllers.enhanced.PredictorMCTS;
import api.GameState;

public class ControllerUtils {
	private Pattern p;
	
	public ControllerUtils() {
		p = Pattern.compile("([a-zA-Z0-9]+)(?:\\(([a-z0-9;.-]+)\\))?");
	}
	
	
	public Controller buildController(int pid, String name, String[] args) {
		
		//mcts agent
		if ("mcts".equals(name)) {
			if (args.length != 3) {
				throw new IllegalArgumentException("mtcs takes 3 params");
			}
			
			int p1 = Integer.parseInt(args[0]);
			int p2 = Integer.parseInt(args[1]);
			int p3 = Integer.parseInt(args[2]);
			return new MCTS(pid==GameState.PLAYER_0, p1, p2, p3);
		}
		
		if ("2mcts".equals(name)) {
			if (args.length != 3) {
				throw new IllegalArgumentException("mtcs takes 3 params");
			}
			
			int p1 = Integer.parseInt(args[0]);
			int p2 = Integer.parseInt(args[1]);
			int p3 = Integer.parseInt(args[2]);
			
			
			Controller nested = new MCTS(pid==GameState.PLAYER_0, p1, p2, p3);
			Predictor predictor = new NestedControllerPredictor(nested);
			return new PredictorMCTS(p1, p2, p3, predictor);
		}
		
		if ("randommcts".equals(name)) {
			if (args.length != 3) {
				throw new IllegalArgumentException("mtcs takes 3 params");
			}
			
			int p1 = Integer.parseInt(args[0]);
			int p2 = Integer.parseInt(args[1]);
			int p3 = Integer.parseInt(args[2]);
			
			
			Controller nested = new RandomController();
			Predictor predictor = new NestedControllerPredictor(nested);
			return new PredictorMCTS(p1, p2, p3, predictor);
		}
		
		//random agent
		if ("random".equals(name)) {
			if (args.length > 1) {
				throw new IllegalArgumentException("random takes 0 or 1 args");
			}
			
			if (args.length == 0) {
				return new RandomController();
			} else {
				long seed = Long.parseLong(args[0]);
				return new RandomController(seed);
			}
		}
		
		if ("baisRandom".equals(name)) {
			if (args.length > 1) {
				throw new IllegalArgumentException("biasRandom takes 0 or 1 args");
			}
			
			if (args.length == 0) {
				return new RandomController();
			} else {
				double bais = Double.parseDouble(args[0]);
				return new SortOfRandomController(bais);
			}
		}
		
		if ("pathfinder".equals(name)) {
			return new FollowTheFlare();
		}
		
		throw new IllegalArgumentException("no such agent! "+name);
	}

	
	public Controller parseDescription(int pid, String description) {
		Matcher m = p.matcher(description);
		if (m.matches()) {
			String name = m.group(1);
			
			String argStr = m.group(2);
			String[] args = new String[0];
			if (argStr != null) {
				args = argStr.split(";");
			}
			return buildController(pid, name, args);
		}
		
		throw new IllegalArgumentException("invalid controller spec");
	}
}
