package gamesrc.experiment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Controllers.MCTS;
import Controllers.enhanced.PredictorMCTS;
import Controllers.enhanced.RandomPredictor;
import gamesrc.Filters;
import gamesrc.level.GameLevel;
import gamesrc.level.LevelParser;
import utils.StatSummary;

public class DebugTheThing {
	private static final Integer NUMBER_RUNS = 500;
	private static final Integer TICK_LIMIT = 2000;

	public static void main(String[] args) throws IOException {
		String debugLevel = "data/maps/level5.txt";
		GameLevel simple = LevelParser.buildParser(debugLevel);
		simple.setLegalMoves("simple", Filters.getBasicActions());

		GameLevel relative = LevelParser.buildParser(debugLevel);
		relative.setLegalMoves("relative", Filters.getAllRelativeActions());
		
		// setup the games
		List<GameRunner> tasks = new ArrayList<GameRunner>(7 * NUMBER_RUNS);

		for (int j = 0; j < NUMBER_RUNS; j++) {

			tasks.add(new GameRunner(simple, new MCTS(true, 500, 10, 45), new MCTS(false, 500, 10, 45), TICK_LIMIT));
			tasks.add(new GameRunner(relative, new MCTS(true, 500, 10, 45), new MCTS(false, 500, 10, 45), TICK_LIMIT));

			tasks.add(new GameRunner(simple, new PredictorMCTS(500, 10, 45, new RandomPredictor()), new PredictorMCTS(500, 10, 45, new RandomPredictor()),
					TICK_LIMIT));

			tasks.add(new GameRunner(relative, new PredictorMCTS(500, 10, 45, new RandomPredictor()), new PredictorMCTS(500, 10, 45, new RandomPredictor()),
					TICK_LIMIT));

		}

		ExecutorService service = Executors.newCachedThreadPool();

		// execute the games
		try {
			List<Future<GameResult>> results = service.invokeAll(tasks);
			printStats(results, System.out);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		service.shutdown();
	}

	private static void printStats(Collection<Future<GameResult>> resultFutures, PrintStream out)
			throws ExecutionException, InterruptedException, FileNotFoundException {

		out.printf("MACHINE,%s,%s,%s,%s,%s,%s,%d,%d\n", System.getProperty("os.name").toLowerCase(),
				System.getProperty("os.version").toLowerCase(), System.getProperty("os.arch").toLowerCase(),
				System.getProperty("java.vendor").toLowerCase(), System.getProperty("java.version").toLowerCase(),
				"reserverd", Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().totalMemory());

		Map<GameSetup, StatSummary> scoreMap = new HashMap<GameSetup, StatSummary>();
		Map<GameSetup, StatSummary> tickMap = new HashMap<GameSetup, StatSummary>();

		for (Future<GameResult> resultFuture : resultFutures) {
			GameResult result = resultFuture.get();

			// collect stats
			StatSummary scores = scoreMap.get(result.setup);
			if (scores == null) {
				scores = new StatSummary();
				scoreMap.put(result.setup, scores);
			}

			StatSummary ticks = tickMap.get(result.setup);
			if (ticks == null) {
				ticks = new StatSummary();
				tickMap.put(result.setup, ticks);
			}

			scores.add(result.score);
			ticks.add(result.ticks);
		}

		for (GameSetup setup : scoreMap.keySet()) {
			out.println(setup);
			out.println(" S> " + scoreMap.get(setup));
			out.println(" T> " + tickMap.get(setup));
			out.println();
		}

	}

}
