package runner.tinycoop;

import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;
import uk.me.webpigeon.phd.tinycoop.engine.Filters;
import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;
import uk.me.webpigeon.phd.tinycoop.engine.level.GameLevel;
import uk.me.webpigeon.phd.tinycoop.engine.level.LevelParser;
import utils.AgentFactory;

/**
 * Play a single game on a single map
 */
public class SingleRunner {

	public static void main(String[] args) throws Exception {

		GameLevel levelRel = LevelParser.buildParser("data/maps/airlock.txt");
		levelRel.setLegalMoves("relative", Filters.getAllRelativeActions());

		SimpleGame game = new SimpleGame(levelRel);
		Controller p1 = AgentFactory.buildBiasRandomAgent();
		Controller p2 = AgentFactory.buildBiasRandomAgent();

		GameSetup setup = new GameSetup(p1.getFriendlyName(), p2.getFriendlyName(), levelRel.getLevelName(), levelRel.getActionSetName());
		GameExecutor executor = new GameExecutor(setup, game, p1, p2);
		GameResult r = executor.call();
		System.out.println(r);
	}

}
