package runner.clear;

import Controllers.Controller;
import gamesrc.SimpleGame;
import gamesrc.level.GameLevel;

public class GameSetup {
	public Controller p1;
	public Controller p2;
	public GameLevel level;

	public SimpleGame buildGame() {
		return new SimpleGame(level);
	}

	public String getActionSetName() {
		return level.getActionSetName();
	}

	public String getLevelName() {
		return level.getLevelName();
	}

	public Controller getPlayer0() {
		return p1.getClone();
	}

	public String getPlayer0Name() {
		return p1.getSimpleName();
	}

	public Controller getPlayer1() {
		return p2.getClone();
	}

	public String getPlayer1Name() {
		return p2.getSimpleName();
	}

	@Override
	public String toString() {
		return String.format("%s and %s on %s (%s)", p1, p2, level, level.getActionSetName());
	}
}
