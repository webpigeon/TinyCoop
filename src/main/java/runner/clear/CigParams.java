package runner.clear;

import java.io.IOException;

import runner.cli.ControllerUtils;
import uk.me.webpigeon.phd.tinycoop.api.GameState;
import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;
import uk.me.webpigeon.phd.tinycoop.engine.Filters;
import uk.me.webpigeon.phd.tinycoop.engine.level.GameLevel;
import uk.me.webpigeon.phd.tinycoop.engine.level.LevelParser;
import uk.me.webpigeon.phd.tinycoop.runner.GameManager;
import uk.me.webpigeon.phd.tinycoop.runner.GameSetup;

public class CigParams {
	private static final Integer NUM_RUNS = 10;

	public static GameSetup buildSetup(Controller p1, Controller p2, GameLevel level) {
		GameSetup setup = new GameSetup(p1.getFriendlyName(), p2.getFriendlyName(), level.getLevelName(),
				level.getActionSetName());
		return setup;
	}

	public static void main(String[] args) throws IOException {
		String[] player1List = new String[] { "mcts", "predictor", "nested", "baisRandom" };
		String[] player2List = new String[] { "pathfinder", "random", "baisRandom", "mcts" };

		/*
		 * String[] levels = new String[] { "data/norm_maps/airlock.txt",
		 * "data/norm_maps/butterfly.txt", "data/norm_maps/maze.txt",
		 * "data/norm_maps/mirror_lock.txt", "data/norm_maps/single_door.txt" };
		 */

		String[] levels = new String[] {
			"data/maps/airlock.txt",
			"data/maps/butterflies.txt",
			"data/maps/single_door.txt",
			"data/norm_maps/cloverleaf.txt"
		};

		ControllerUtils controllers = new ControllerUtils();

		// start the game processing workers
		GameManager manager = new GameManager();

		for (String level : levels) {
			GameLevel levelRel = LevelParser.buildParser(level);
			levelRel.setLegalMoves("relative", Filters.getBasicActionsWithComms());

			for (int i = 0; i < NUM_RUNS; i++) {
				for (String agent1 : player1List) {
					for (String agent2 : player2List) {
						// setup (other agent as 1)
						Controller predictorAgentP2 = controllers.parseDescription(GameState.PLAYER_1, agent2);
						Controller testAgentP1 = controllers.parseDescription(GameState.PLAYER_0, agent1,
								predictorAgentP2);
						Controller coopAgentP2 = controllers.parseDescription(GameState.PLAYER_1, agent2);
						manager.addGame(levelRel, testAgentP1, coopAgentP2);

						// setup (other agent as 2)
						Controller predictorAgentP1 = controllers.parseDescription(GameState.PLAYER_0, agent2);
						Controller coopAgentP1 = controllers.parseDescription(GameState.PLAYER_0, agent2);
						Controller testAgentP2 = controllers.parseDescription(GameState.PLAYER_1, agent1,
								predictorAgentP1);
						manager.addGame(levelRel, coopAgentP1, testAgentP2);
					}
				}
			}
		}

		// start the data collection thread
		Thread managerThread = new Thread(manager);
		managerThread.start();

		// this should wait until all games have finished
		manager.shutdown();
	}

}
