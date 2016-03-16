package gamesrc.experiment;

import Controllers.Controller;
import Controllers.MCTS;
import Controllers.enhanced.NestedControllerPredictor;
import Controllers.enhanced.PredictorMCTS;

public class Utils {
	
	public static Controller buildMCTS(boolean first) {
		return new MCTS(first, 500, 10, 45);
	}
	
	public static Controller buildPredictor(Controller nested) {
		NestedControllerPredictor p = new NestedControllerPredictor(nested);
		return new PredictorMCTS(500, 10, 45, p);
	}
	
	public static String getHostname() {
		String hostname = System.getenv("HOSTNAME");
		if (hostname != null) {
			return hostname;
		}
		
		hostname = System.getenv("COMPUTERNAME");
		if (hostname != null) {
			return hostname;
		}
		
		return "UNKNOWN_HOST";
	}

}
