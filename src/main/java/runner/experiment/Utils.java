package runner.experiment;

import Controllers.Controller;
import Controllers.MCTS;
import Controllers.RandomController;
import Controllers.enhanced.NestedControllerPredictor;
import Controllers.enhanced.PredictorMCTS;

public class Utils {
	private static final Integer ROLLOUT_LENGTH = 450;
	
	public static Controller buildMCTS(boolean first) {
		return new MCTS(first, 500, 10, ROLLOUT_LENGTH);
	}
	
	public static PredictorMCTS buildPredictor(Controller nested, String tag) {
		NestedControllerPredictor p = new NestedControllerPredictor(nested);
		PredictorMCTS host = new PredictorMCTS(500, 10, ROLLOUT_LENGTH, p);
		host.tag = "#"+tag+"#";
		return host;
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

	public static Controller buildMCTSPredictor() {
		//predicting a MCTSPredictor(Random)
		Controller predicted = buildPredictor(new RandomController(),"inner");
		return buildPredictor(predicted, "2MCTS");
	}
	
	public static Controller buildRandomPredictor() {
		//predicting a random controller
		Controller predicted = new RandomController();
		return buildPredictor(predicted, "RND");
	}

}
