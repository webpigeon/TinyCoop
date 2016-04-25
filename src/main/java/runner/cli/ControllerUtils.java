package runner.cli;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Controllers.Controller;
import Controllers.MCTS;
import Controllers.RandomController;
import Controllers.SortOfRandomController;

public class ControllerUtils {
	private Pattern p;
	
	public ControllerUtils() {
		p = Pattern.compile("([a-zA-Z0-9]+)(?:\\(([a-z0-9;.-]+)\\))?");
	}
	
	
	public Controller buildController(String name, String[] args) {
		
		//mcts agent
		if ("mcts".equals(name)) {
			if (args.length != 3) {
				throw new IllegalArgumentException("mtcs takes 3 params");
			}
			
			int p1 = Integer.parseInt(args[0]);
			int p2 = Integer.parseInt(args[1]);
			int p3 = Integer.parseInt(args[2]);
			return new MCTS(false, p1, p2, p3);
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
		
		throw new IllegalArgumentException("no such agent! "+name);
	}

	
	public Controller parseDescription(String description) {
		Matcher m = p.matcher(description);
		if (m.matches()) {
			String name = m.group(1);
			
			String argStr = m.group(2);
			String[] args = new String[0];
			if (argStr != null) {
				args = argStr.split(";");
			}
			return buildController(name, args);
		}
		
		throw new IllegalArgumentException("invalid controller spec");
	}
}
