package runner.experiment;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import Controllers.Controller;
import Controllers.MCTS;
import Controllers.RandomController;
import Controllers.enhanced.NestedControllerPredictor;
import Controllers.enhanced.Predictor;
import Controllers.enhanced.PredictorMCTS;
import Controllers.enhanced.RandomPredictor;
import api.Action;

public class Utils {
	private static final Integer NUM_ROLLOUTS = 20000;
	private static final Integer TREE_SIZE = 5;
	private static final Integer ROLLOUT_LENGTH = 250;

	public static Controller buildMCTS(boolean first) {
		return new MCTS(first, NUM_ROLLOUTS, TREE_SIZE, ROLLOUT_LENGTH);
	}

	public static Controller buildMCTSPredictor() {
		// predicting a MCTSPredictor(Random)
		Controller predicted = buildPredictor(new RandomController(), "inner");
		return buildPredictor(predicted, "2MCTS");
	}

	public static PredictorMCTS buildPredictor(Controller nested, String tag) {
		NestedControllerPredictor p = new NestedControllerPredictor(nested);
		PredictorMCTS host = new PredictorMCTS(NUM_ROLLOUTS, TREE_SIZE, ROLLOUT_LENGTH, p);
		host.tag = "#[" + tag + "]#";
		return host;
	}

	public static Predictor buildRandomPolicy() {
		return new RandomPredictor();
	}

	public static Controller buildRandomPredictor() {
		// predicting a random controller
		Controller predicted = new RandomController();
		return buildPredictor(predicted, "RND");
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
	
	public static Action getRandomAction(List<Action> actions, Random rnd) {
		int choice = rnd.nextInt(actions.size());
		return actions.get(choice);
	}

}
