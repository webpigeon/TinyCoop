package gamesrc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Controllers.Controller;
import Controllers.MCTS;
import Controllers.RandomController;
import Controllers.SortOfRandomController;
import FastGame.Action;
import utils.StatSummary;

public class GameRunner implements Callable<GameResult> {
	private static final Integer TICK_LIMIT = 10_000;
	private static final Integer NUMBER_RUNS = 1000;

	public static void main(String[] args) throws IOException {
		GameLevel level = LevelParser.buildParser("data/maps/level1.txt");
		level.setLegalMoves("basic", Filters.getBasicActions());

		GameLevel level2 = LevelParser.buildParser("data/maps/level1.txt");
		level2.setLegalMoves("relative", Filters.getAllRelativeActions());

		// setup the games
		List<GameRunner> tasks = new ArrayList<GameRunner>(10);
		for (int i = 0; i < NUMBER_RUNS; i++) {
			tasks.add(new GameRunner(level, new MCTS(true, 500, 10, 45), new MCTS(false, 500, 10, 45), TICK_LIMIT));
			tasks.add(new GameRunner(level, new RandomController(), new RandomController(), TICK_LIMIT));
			tasks.add(new GameRunner(level, new SortOfRandomController(), new SortOfRandomController(), TICK_LIMIT));
			// tasks.add(new GameRunner(level2, new MCTS(true, 500, 10, 45), new
			// MCTS(false, 500, 10, 45), TICK_LIMIT));
			tasks.add(new GameRunner(level2, new RandomController(), new RandomController(), TICK_LIMIT));
			tasks.add(new GameRunner(level2, new SortOfRandomController(), new SortOfRandomController(), TICK_LIMIT));
		}

		// execute the games
		ExecutorService service = Executors.newFixedThreadPool(5);
		try {
			List<Future<GameResult>> results = service.invokeAll(tasks);
			printStats(results);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			service.shutdown();
		}

	}

	private static void printStats(Collection<Future<GameResult>> resultFutures)
			throws ExecutionException, InterruptedException {
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
			System.out.println(setup);
			System.out.println(" S> " + scoreMap.get(setup));
			System.out.println(" T> " + tickMap.get(setup));
			System.out.println();
		}
	}

	private GameLevel level;
	private Controller p1;
	private Controller p2;
	private int tickLimit;

	public GameRunner(GameLevel level, Controller p1, Controller p2, int tickLimit) {
		this.level = level;
		this.p1 = p1;
		this.p2 = p2;
		this.tickLimit = tickLimit;
	}

	@Override
	public GameResult call() throws Exception {

		// setup result entry already
		GameSetup setup = new GameSetup();
		setup.levelID = level.getLevelName();
		setup.actionSet = level.getActionSetName();
		setup.p1 = p1.getSimpleName();
		setup.p2 = p2.getSimpleName();

		GameResult result = new GameResult(setup);

		SimpleGame game = new SimpleGame(level);
		p1.startGame(0);
		p2.startGame(1);

		// make a list of legal moves for each player
		List<Action> legalMoves1 = Arrays.asList(game.getLegalActions(0));
		List<Action> legalMoves2 = Arrays.asList(game.getLegalActions(1));

		int tickCount = 0;
		while (!game.hasWon() && tickCount < tickLimit) {
			Action p1Move = p1.get(game.getClone());
			Action p2Move = p2.get(game.getClone());

			if (!legalMoves1.contains(p1Move) || !legalMoves2.contains(p2Move)) {
				System.err.println("illegal move detected " + p1Move + " " + p2Move);
				result.disquals++;
				break;
			}

			game.update(p1Move, p2Move);
			result.recordMoves(p1Move, p2Move);
			tickCount++;
		}

		// record results{
		if (result.disquals == 0) {
			result.score = game.getScore();
			result.ticks = tickCount;
		}

		return result;
	}

}
