package runner.cli;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import api.controller.Controller;
import runner.tinycoop.AgentFactory;

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
