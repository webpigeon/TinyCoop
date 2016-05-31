package runner.cli;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Controllers.PiersController;
import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;
import uk.me.webpigeon.phd.tinycoop.controllers.prediction.ControllerPolicy;
import utils.AgentFactory;
import utils.LegacyAgentFactory;

public class ControllerUtils {
	private Pattern p;

	public ControllerUtils() {
		p = Pattern.compile("([a-zA-Z0-9]+)(?:\\(([a-z0-9;.-]+)\\))?");
	}

	public Controller buildController(int pid, String name, String[] args) {

		if ("mcts".equals(name)) {
			return AgentFactory.buildStandardMCTS();
		}

		if ("random".equals(name)) {
			return AgentFactory.buildRandomAgent();
		}

		if ("baisRandom".equals(name)) {
			return AgentFactory.buildBiasRandomAgent();
		}

		if ("pathfinder".equals(name)) {
			return AgentFactory.buildFlareFollower();
		}

		throw new IllegalArgumentException("no such agent! " + name);
	}
	
	public PiersController buildLegacyController(int pid, String name, String[] args) {

		if ("mcts".equals(name)) {
			return LegacyAgentFactory.buildStandardMCTS();
		}

		if ("random".equals(name)) {
			return LegacyAgentFactory.buildRandomAgent();
		}

		if ("baisRandom".equals(name)) {
			return LegacyAgentFactory.buildBiasRandomAgent();
		}

		if ("pathfinder".equals(name)) {
			return LegacyAgentFactory.buildFlareFollower();
		}

		throw new IllegalArgumentException("no such agent! " + name);
	}

	/**
	 * build agents which are predictor aware
	 * 
	 * @param pid
	 * @param name
	 * @param other
	 * @return
	 */
	public Controller parseDescription(int pid, String name, Controller other) {
		if ("mcts".equals(name)) {
			return AgentFactory.buildStandardMCTS();
		}
		
		if ("predictor".equals(name)) {
			return AgentFactory.buildPredictorMCTS(new ControllerPolicy(other));
		}

		if ("nested".equals(name)) {
			return AgentFactory.buildPredictorMCTS(new ControllerPolicy(AgentFactory.buildStandardMCTS()));
		}

		return parseDescription(pid, name);
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
	
	public PiersController parseLegacyDescription(int pid, String description) {
		Matcher m = p.matcher(description);
		if (m.matches()) {
			String name = m.group(1);

			String argStr = m.group(2);
			String[] args = new String[0];
			if (argStr != null) {
				args = argStr.split(";");
			}
			return buildLegacyController(pid, name, args);
		}

		throw new IllegalArgumentException("invalid controller spec");
	}
}
