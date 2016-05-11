package runner.clear;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import api.GameState;
import api.controller.Controller;
import gamesrc.Filters;
import gamesrc.level.GameLevel;
import gamesrc.level.LevelParser;
import runner.cli.ControllerUtils;
import runner.experiment.Utils;
import runner.tinycoop.GameManager;
import runner.tinycoop.GameSetup;
import uk.me.webpigeon.controllers.prediction.ControllerPolicy;
import uk.me.webpigeon.controllers.prediction.Policy;
import uk.me.webpigeon.controllers.prediction.RandomPolicy;
import utils.AgentFactory;
import utils.GenerateCSV;

public class CigParams {
	private static final Integer MAX_THREADS = 4;
	private static final Integer MAX_TICKS = 2000;
	private static final Integer NUM_RUNS = 1;

	public static GameSetup buildSetup(Controller p1, Controller p2, GameLevel level) {
		GameSetup setup = new GameSetup(p1.getFriendlyName(), p2.getFriendlyName(), level.getLevelName(), level.getActionSetName());
		return setup;
	}

	public static void main(String[] args) throws IOException {

		String[] player2List = new String[] { "pathfinder", "random", "baisRandom", "mcts(500;10;45)" };

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
						Controller p1 = AgentFactory.buildPredictorMCTS(new RandomPolicy());
						Controller p2 = controllers.parseDescription(GameState.PLAYER_1, player2);
						manager.addGame(levelRel, p1, p2);
					}

					// setup (mirror predictor)
					{
						Controller p2 = controllers.parseDescription(GameState.PLAYER_1, player2);
						
						//build the predictor setup for p1
						Controller p2Predictor = controllers.parseDescription(GameState.PLAYER_1, player2);
						Policy policy = new ControllerPolicy(p2Predictor);
						Controller p1 = AgentFactory.buildPredictorMCTS(policy);

						manager.addGame(levelRel, p1, p2);
					}

					// setup (mcts predictor)
					{
						Controller p2 = controllers.parseDescription(GameState.PLAYER_1, player2);
						Policy policy = new ControllerPolicy(AgentFactory.buildStandardMCTS());
						Controller p1 = AgentFactory.buildPredictorMCTS(policy);
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
