package runner.tinycoop;

import java.util.UUID;

import api.controller.Controller;
import gamesrc.SimpleGame;
import gamesrc.level.GameLevel;

public class GameSetup {
	public final UUID id;
	public final String p1;
	public final String p2;
	public final String level;
	public final String actionSet;
	
	public GameSetup(String p1, String p2, String level, String actionSet) {
		this.id = UUID.randomUUID();
		this.p1 = p1;
		this.p2 = p2;
		this.level = level;
		this.actionSet = actionSet;
	}
	
	public String getPlayer1() {
		return p1;
	}
	
	public String getPlayer2() {
		return p2;
	}
	
	public String getActionSet() {
		return actionSet;
	}

	@Override
	public String toString() {
		return String.format("[%s] %s and %s on %s (%s)", id, p1, p2, level, actionSet);
	}
}
