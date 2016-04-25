package runner.clear;

import Controllers.Controller;
import gamesrc.SimpleGame;
import gamesrc.level.GameLevel;

public class GameSetup {
	public Controller p1;
	public Controller p2;
	public GameLevel level;

	public Controller getPlayer0() {
		return p1.getClone();
	}
	
	public Controller getPlayer1() {
		return p2.getClone();
	}

	public SimpleGame buildGame() {
		return new SimpleGame(level);
	}
	
	@Override
	public String toString() {
		return String.format("%s and %s on %s (%s)", p1, p2, level, level.getActionSetName());
	}

	public String getLevelName() {
		return level.getLevelName();
	}
	
	public String getActionSetName() {
		return level.getActionSetName();
	}

	public String getPlayer0Name() {
		return p1.getSimpleName();
	}
	
	public String getPlayer1Name() {
		return p2.getSimpleName();
	}
}
