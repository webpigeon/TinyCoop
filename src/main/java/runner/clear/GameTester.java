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
import runner.tinycoop.GameSetup;
import utils.GenerateCSV;

public class GameTester {
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

		String[] player1List = new String[] { "hillClimber" };

		String[] player2List = new String[] { "random" };

		String[] levels = new String[] { "data/norm_maps/airlock.txt", "data/norm_maps/butterfly.txt",
				"data/norm_maps/maze.txt", "data/norm_maps/mirror_lock.txt", "data/norm_maps/single_door.txt" };

		ControllerUtils controllers = new ControllerUtils();

		List<GameEngine> tasks = new ArrayList<GameEngine>();
		for (String level : levels) {
			GameLevel levelRel = LevelParser.buildParser(level);
			levelRel.setLegalMoves("relative", Filters.getAllRelativeActions());

			for (int i = 0; i < NUM_RUNS; i++) {
				for (String player1 : player1List) {
					for (String player2 : player2List) {

						// setup
						GameSetup setup = buildSetup(controllers.parseDescription(GameState.PLAYER_0, player1),
								controllers.parseDescription(GameState.PLAYER_1, player2), levelRel);

						tasks.add(new GameEngine(setup, MAX_TICKS, new TraceGameRecord(setup)));
					}
				}
			}

		}

		GenerateCSV csv = new GenerateCSV(String.format("results-%d.csv", System.nanoTime()));

		try {
			ExecutorService service = Executors.newFixedThreadPool(MAX_THREADS);
			List<Future<GameRecord>> recordFutures = service.invokeAll(tasks);

			for (Future<GameRecord> recordFuture : recordFutures) {
				try {
					GameRecord record = recordFuture.get();
					csv.writeLine(record.getID(), record.getPlayer1(), record.getPlayer2(), record.getLevel(),
							record.getActionSet(), record.getResult(), record.getScore(), record.getTicks());
					System.out.println(record + " " + record.getResultString());
				} catch (ExecutionException ex) {
					ex.printStackTrace();
				}
			}

			service.shutdown();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

}
