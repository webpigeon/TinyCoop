package runner.clear;

import java.io.IOException;

import Controllers.PiersController;
import runner.cli.ControllerUtils;
import runner.tinycoop.GameManager;
import runner.tinycoop.GameSetup;
import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;
import uk.me.webpigeon.phd.tinycoop.engine.Filters;
import uk.me.webpigeon.phd.tinycoop.engine.level.GameLevel;
import uk.me.webpigeon.phd.tinycoop.engine.level.LevelParser;
import utils.LegacyAgentFactory;

public class CigParams {
	private static final Integer NUM_RUNS = 10;

	public static GameSetup buildSetup(Controller p1, Controller p2, GameLevel level) {
		GameSetup setup = new GameSetup(p1.getFriendlyName(), p2.getFriendlyName(), level.getLevelName(), level.getActionSetName());
		return setup;
	}

	public static void main(String[] args) throws IOException {

		String[] player2List = new String[] { "pathfinder", "random", "baisRandom", "mcts" };
		
		/*
		 * String[] levels = new String[] { "data/norm_maps/airlock.txt",
		 * "data/norm_maps/butterfly.txt", "data/norm_maps/maze.txt",
		 * "data/norm_maps/mirror_lock.txt", "data/norm_maps/single_door.txt" };
		 */

		String[] levels = new String[] { "data/maps/airlock.txt", "data/maps/butterflies.txt",
				"data/maps/single_door.txt", "data/norm_maps/cloverleaf.txt" };

		ControllerUtils controllers = new ControllerUtils();
		
		//start the game processing workers
		GameManager manager = new GameManager();

		for (String level : levels) {
			GameLevel levelRel = LevelParser.buildParser(level);
			levelRel.setLegalMoves("relative", Filters.getBasicActions());

			for (int i = 0; i < NUM_RUNS; i++) {

					// setup (random predictor)
						PiersController p1 = LegacyAgentFactory.buildStandardPiersMCTS(true);
						PiersController p2 = LegacyAgentFactory.buildStandardPiersMCTS(false);
						manager.addGame(levelRel, p1, p2);
					}


		}

		//start the data collection thread
		Thread managerThread = new Thread(manager);
		managerThread.start();
		
		//this should wait until all games have finished
		manager.shutdown();
	}

}
