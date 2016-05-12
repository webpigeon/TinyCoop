package runner.clear;

import java.io.IOException;
import Controllers.PiersController;
import api.GameState;
import api.controller.Controller;
import gamesrc.Filters;
import gamesrc.level.GameLevel;
import gamesrc.level.LevelParser;
import runner.cli.ControllerUtils;
import runner.tinycoop.GameManager;
import runner.tinycoop.GameSetup;
import utils.LegacyAgentFactory;

public class CigParams {
	private static final Integer NUM_RUNS = 1;

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
				"data/maps/single_door.txt" };

		ControllerUtils controllers = new ControllerUtils();
		
		//start the game processing workers
		GameManager manager = new GameManager();

		for (String level : levels) {
			GameLevel levelRel = LevelParser.buildParser(level);
			levelRel.setLegalMoves("relative", Filters.getAllRelativeActions());

			for (int i = 0; i < NUM_RUNS; i++) {

				for (String player2 : player2List) {
					// predictor agents as agent 1

					// setup (random predictor)
					{
						PiersController p1 = LegacyAgentFactory.buildStandardMCTS();
						PiersController p2 = controllers.parseLegacyDescription(GameState.PLAYER_1, player2);
						manager.addGame(levelRel, p1, p2);
					}

					// setup (mirror predictor)
					{
						PiersController p2 = controllers.parseLegacyDescription(GameState.PLAYER_1, player2);
						
						//build the predictor setup for p1
						PiersController p2Predictor = controllers.parseLegacyDescription(GameState.PLAYER_1, player2);
						PiersController p1 = LegacyAgentFactory.buildHighPredictor(p2Predictor);

						manager.addGame(levelRel, p1, p2);
					}

					// setup (mcts predictor)
					{
						PiersController p2 = controllers.parseLegacyDescription(GameState.PLAYER_1, player2);
						Controller p1 = LegacyAgentFactory.buildHighMCTS2();
						manager.addGame(levelRel, p1, p2);
					}
					/*
						 * 
						 * // setup (baisRandom) { Controller p2 =
						 * controllers.parseDescription(GameState.PLAYER_1,
						 * player2); GameSetup setup = buildSetup(new
						 * SortOfRandomController(), p2, levelRel);
						 * tasks.add(new GameEngine(setup, MAX_TICKS, new
						 * TraceGameRecord(setup))); }
						 * 
						 * // predictor agents as agent 2
						 * 
						 * // setup (random predictor) { Controller p2 =
						 * controllers.parseDescription(GameState.PLAYER_1,
						 * player2); GameSetup setup = buildSetup(p2,
						 * Utils.buildRandomPredictor(), levelRel);
						 * tasks.add(new GameEngine(setup, MAX_TICKS, new
						 * TraceGameRecord(setup))); }
						 * 
						 * // setup (mirror predictor) { Controller p2 =
						 * controllers.parseDescription(GameState.PLAYER_1,
						 * player2); Controller p2Predictor =
						 * controllers.parseDescription(GameState.PLAYER_1,
						 * player2); GameSetup setup = buildSetup(p2,
						 * Utils.buildPredictor(p2Predictor, "MIRROR"),
						 * levelRel); tasks.add(new GameEngine(setup, MAX_TICKS,
						 * new TraceGameRecord(setup))); }
						 * 
						 * // setup (mcts predictor) { Controller p2 =
						 * controllers.parseDescription(GameState.PLAYER_1,
						 * player2); GameSetup setup = buildSetup(p2,
						 * Utils.buildMCTSPredictor(), levelRel); tasks.add(new
						 * GameEngine(setup, MAX_TICKS, new
						 * TraceGameRecord(setup))); }
						 * 
						 * // setup (baisRandom) { Controller p2 =
						 * controllers.parseDescription(GameState.PLAYER_1,
						 * player2); GameSetup setup = buildSetup(p2, new
						 * SortOfRandomController(), levelRel); tasks.add(new
						 * GameEngine(setup, MAX_TICKS, new
						 * TraceGameRecord(setup))); }
						 */
				}
			}

		}

		//start the data collection thread
		Thread managerThread = new Thread(manager);
		managerThread.start();
		
		//this should wait until all games have finished
		manager.shutdown();
	}

}
