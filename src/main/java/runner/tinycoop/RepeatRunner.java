package runner.tinycoop;

import api.GameState;
import api.controller.Controller;
import gamesrc.Filters;
import gamesrc.SimpleGame;
import gamesrc.level.GameLevel;
import gamesrc.level.LevelParser;
import runner.cli.ControllerUtils;

/**
 * Play a single game on a single map
 */
public class RepeatRunner {
	private static final Integer NUM_REPEATS = 1000;

	public static void main(String[] args) throws Exception {

		if (args.length != 3) {
			System.out.println("usage: level agent1 agent2");
			System.exit(-1);
		}

		ControllerUtils utils = new ControllerUtils();

		String levelFile = args[0];
		String agent1 = args[1];
		String agent2 = args[2];

		GameLevel levelRel = LevelParser.buildParser(levelFile);
		levelRel.setLegalMoves("relative", Filters.getAllRelativeActions());

		long startTime = System.nanoTime();

		for (int i = 0; i < NUM_REPEATS; i++) {
			SimpleGame game = new SimpleGame(levelRel);
			Controller p1 = utils.parseDescription(GameState.PLAYER_0, agent1);
			Controller p2 = utils.parseDescription(GameState.PLAYER_1, agent2);

			GameSetup setup = new GameSetup(p1.getFriendlyName(), p2.getFriendlyName(), levelRel.getLevelName(), levelRel.getActionSetName());
			GameExecutor executor = new GameExecutor(setup, game, p1, p2);
			GameResult r = executor.call();
			System.out.println(r);
		}

		long endTime = System.nanoTime();
		long total = endTime - startTime;
		System.out.println(String.format("%d NS, (%f per game)", total, (double) total / NUM_REPEATS));
	}

}
