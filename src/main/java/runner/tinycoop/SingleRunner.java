package runner.tinycoop;

import java.io.IOException;

import api.controller.Controller;
import gamesrc.Filters;
import gamesrc.SimpleGame;
import gamesrc.level.GameLevel;
import gamesrc.level.LevelParser;

/**
 * Play a single game on a single map
 */
public class SingleRunner {
	
	public static void main(String[] args) throws Exception{
		
		GameLevel levelRel = LevelParser.buildParser("data/maps/airlock.txt");
		levelRel.setLegalMoves("relative", Filters.getAllRelativeActions());
		
		SimpleGame game = new SimpleGame(levelRel);
		Controller p1 = AgentFactory.buildBiasRandomAgent();
		Controller p2 = AgentFactory.buildBiasRandomAgent();
		
		GameExecutor executor = new GameExecutor(game, p1, p2);
		GameResult r = executor.call();
		System.out.println(r);
	}

}
