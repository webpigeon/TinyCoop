package runner.clear;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Controllers.Controller;
import api.GameState;
import gamesrc.Filters;
import gamesrc.level.GameLevel;
import gamesrc.level.LevelParser;
import runner.cli.ControllerUtils;
import runner.experiment.Utils;
import utils.GenerateCSV;

public class CigParams {
	private static final Integer MAX_THREADS = 4;
	private static final Integer MAX_TICKS = 2000;
	private static final Integer NUM_RUNS = 1;

	public static GameSetup buildSetup(Controller p1, Controller p2, GameLevel level) {
		GameSetup setup = new GameSetup();
		setup.p1 = p1;
		setup.p2 = p2;
		setup.level = level;
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

		List<GameEngine> tasks = new ArrayList<GameEngine>();
		for (String level : levels) {
			GameLevel levelRel = LevelParser.buildParser(level);
			levelRel.setLegalMoves("relative", Filters.getAllRelativeActions());

			for (int i = 0; i < NUM_RUNS; i++) {

				for (String player2 : player2List) {
					// predictor agents as agent 1

					// setup (random predictor)
					{
						Controller p2 = controllers.parseDescription(GameState.PLAYER_1, player2);
						GameSetup setup = buildSetup(Utils.buildRandomPredictor(), p2, levelRel);
						tasks.add(new GameEngine(setup, MAX_TICKS, new TraceGameRecord(setup)));
					}

					// setup (mirror predictor)
					{
						Controller p2 = controllers.parseDescription(GameState.PLAYER_1, player2);
						Controller p2Predictor = controllers.parseDescription(GameState.PLAYER_1, player2);
						GameSetup setup = buildSetup(Utils.buildPredictor(p2Predictor, "MIRROR"), p2, levelRel);
						tasks.add(new GameEngine(setup, MAX_TICKS, new TraceGameRecord(setup)));
					}

					// setup (mcts predictor)
					{
						Controller p2 = controllers.parseDescription(GameState.PLAYER_1, player2);
						GameSetup setup = buildSetup(Utils.buildMCTSPredictor(), p2, levelRel);
						tasks.add(new GameEngine(setup, MAX_TICKS, new TraceGameRecord(setup)));
					} /*
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

		System.out.println(tasks.size());

		GenerateCSV csv = new GenerateCSV(String.format("results-%d.csv", System.nanoTime()));

		try {
			int threads = MAX_THREADS;
			if (args.length == 1) {
				threads = Integer.parseInt(args[0]);
			}

			ExecutorService service = Executors.newFixedThreadPool(threads);
			List<Future<GameRecord>> recordFutures = service.invokeAll(tasks);

			for (Future<GameRecord> recordFuture : recordFutures) {
				try {
					GameRecord record = recordFuture.get();
					csv.writeLine(record.getID(), record.getPlayer1(), record.getPlayer2(), record.getLevel(),
							record.getActionSet(), record.getResult(), record.getScore(), record.getTicks());
					System.out.println(record + " " + record.getResultString());
				} catch (ExecutionException ex) {
					System.err.println("game crashed: writing to stats failed");
					ex.printStackTrace();
				}
			}

			service.shutdown();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

}
