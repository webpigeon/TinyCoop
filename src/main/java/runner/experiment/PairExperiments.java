package runner.experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import Controllers.PiersController;
import Controllers.PassiveRefindController;
import Controllers.RandomController;
import Controllers.SortOfRandomController;
import uk.me.webpigeon.phd.tinycoop.engine.Filters;
import uk.me.webpigeon.phd.tinycoop.engine.level.GameLevel;
import uk.me.webpigeon.phd.tinycoop.engine.level.LevelParser;
import utils.GenerateCSV;
import utils.StatSummary;

/**
 * Experiments with pairings of MCTS and another agent.
 */
public class PairExperiments {

	private static final Integer TICK_LIMIT = 2000;
	private static final Integer NUMBER_REPEATS = 700; // number of times to
														// repeat each run
	private static final Integer NUMBER_RUNS = 1; // runs before outputting to
													// file
	private static AtomicInteger referenceCount = new AtomicInteger();

	// combined summary stats
	private static final String CSV_FILE = "results-pairs/results-tinycoop-%d.csv";

	public static void main(String[] args) throws IOException {

		File directory = new File("results/moves");
		directory.mkdirs();

		ExecutorService service = Executors.newFixedThreadPool(4);

		String[] levelStrings = new String[] { "data/maps/level1.txt", "data/maps/level1E.txt", "data/maps/level7.txt",
				"data/maps/level6.txt", "data/maps/level5.txt", "data/maps/level4.txt", };

		List<GameLevel> levels = new ArrayList<GameLevel>();
		List<GameLevel> simpleLevels = new ArrayList<GameLevel>();

		for (String levelString : levelStrings) {
			// GameLevel level = LevelParser.buildParser(levelString);
			// level.setLegalMoves("full",
			// Filters.getAllActions(level.getWidth(), level.getHeight()));

			GameLevel levelRel = LevelParser.buildParser(levelString);
			levelRel.setLegalMoves("relative", Filters.getAllRelativeActions());

			GameLevel levelSimp = LevelParser.buildParser(levelString);
			levelSimp.setLegalMoves("simple", Filters.getBasicActions());

			// levels.add(level);
			levels.add(levelRel);
			levels.add(levelSimp);
			simpleLevels.add(levelSimp);
		}

		GenerateCSV csv = new GenerateCSV(String.format(CSV_FILE, System.currentTimeMillis()));
		csv.writeLine("id", "player1", "player2", "levelID", "actionSet", "score", "ticks", "dequal.");

		for (int i = 0; i < NUMBER_REPEATS; i++) {
			long runStarted = System.currentTimeMillis();

			// setup the games
			List<GameRunner> tasks = new ArrayList<GameRunner>(7 * NUMBER_RUNS);

			for (int j = 0; j < NUMBER_RUNS; j++) {
				for (GameLevel level : levels) {

					// PMCTS(MCTS) & MCTS
					{
						PiersController p1 = Utils.buildPredictor(Utils.buildMCTS(false), "pmcts");
						PiersController p2 = Utils.buildMCTS(false);
						tasks.add(new GameRunner(level, p1, p2, TICK_LIMIT));
					}

					// MCTS & MCTS
					{
						PiersController p1 = Utils.buildMCTS(true);
						PiersController p2 = Utils.buildMCTS(false);
						tasks.add(new GameRunner(level, p1, p2, TICK_LIMIT));
					}

					// PMCTS(SortOfRandom) & SortOfRandom
					{
						PiersController p1 = Utils.buildPredictor(new SortOfRandomController(), "pmcts");
						PiersController p2 = new SortOfRandomController();
						tasks.add(new GameRunner(level, p1, p2, TICK_LIMIT));
					}

					// MCTS & SortOfRandom
					{
						PiersController p1 = Utils.buildMCTS(true);
						PiersController p2 = new SortOfRandomController();
						tasks.add(new GameRunner(level, p1, p2, TICK_LIMIT));
					}

					// PMCTS(PassiveRefind) & PassiveRefind
					{
						PiersController p1 = Utils.buildPredictor(new PassiveRefindController(), "pmcts");
						PiersController p2 = new PassiveRefindController();
						tasks.add(new GameRunner(level, p1, p2, TICK_LIMIT));
					}

					// MCTS & PassiveRefind
					{
						PiersController p1 = Utils.buildMCTS(true);
						PiersController p2 = new PassiveRefindController();
						tasks.add(new GameRunner(level, p1, p2, TICK_LIMIT));
					}

					// Random for scale (control)
					{
						PiersController p1 = new RandomController();
						PiersController p2 = new RandomController();
						tasks.add(new GameRunner(level, p1, p2, TICK_LIMIT));
					}

					// Sort of Random for scale (control)
					{
						PiersController p1 = new SortOfRandomController();
						PiersController p2 = new SortOfRandomController();
						tasks.add(new GameRunner(level, p1, p2, TICK_LIMIT));
					}

				}
			}
			referenceCount.addAndGet(tasks.size());

			// execute the games
			try {
				List<Future<GameResult>> results = service.invokeAll(tasks);
				printStats(results, csv);
				System.out.println("run complete " + (System.currentTimeMillis() - runStarted));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		service.shutdown();

	}

	private static void printStats(Collection<Future<GameResult>> resultFutures, GenerateCSV csv)
			throws ExecutionException, InterruptedException, FileNotFoundException {

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

			csv.writeLine(result.id, result.setup.p1, result.setup.p2, result.setup.levelID, result.setup.actionSet,
					result.score, result.ticks, result.disquals, result.userTime, result.wallTime);

			scores.add(result.score);
			ticks.add(result.ticks);
		}

	}

}
