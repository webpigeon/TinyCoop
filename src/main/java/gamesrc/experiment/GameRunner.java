package gamesrc.experiment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import Controllers.Controller;
import Controllers.MCTS;
import Controllers.PassiveRefindController;
import Controllers.RandomController;
import Controllers.SortOfRandomController;
import Controllers.enhanced.NestedControllerPredictor;
import Controllers.enhanced.PredictorMCTS;
import FastGame.Action;
import gamesrc.Filters;
import gamesrc.GameLevel;
import gamesrc.LevelParser;
import gamesrc.SimpleGame;
import utils.GenerateCSV;
import utils.StatSummary;

public class GameRunner implements Callable<GameResult> {
	private static final Integer TICK_LIMIT = 2000;
	private static final Integer NUMBER_REPEATS = 100; //number of files
	private static final Integer NUMBER_RUNS = 5; //runs per file
	private static AtomicInteger referenceCount = new AtomicInteger();

	//combined summary stats
	private static final String CSV_FILE="/home/webpigeon/tinycoop/results-tinycoop-%d.csv";
	
	//game records
	private static final String CSV_TRACE="/home/webpigeon/tinycoop/moves/trace-%s.csv";
	
	//batch summary stats
	private static final String RUN_STATS_FILE="/home/webpigeon/tinycoop/results/results-tinycoop-%d.txt";
	
	public static void main(String[] args) throws IOException {
		
		ExecutorService service = Executors.newFixedThreadPool(4);
		
		String[] levelStrings = new String[] {
			"data/maps/level1.txt",
			"data/maps/level1E.txt",
			"data/maps/level7.txt",
			"data/maps/level6.txt"
		};
		
		List<GameLevel> levels = new ArrayList<GameLevel>();
		List<GameLevel> simpleLevels = new ArrayList<GameLevel>();
		
		for (String levelString : levelStrings) {
			//GameLevel level = LevelParser.buildParser(levelString);
			//level.setLegalMoves("full", Filters.getAllActions(level.getWidth(), level.getHeight()));

			GameLevel levelRel = LevelParser.buildParser(levelString);
			levelRel.setLegalMoves("relative", Filters.getAllRelativeActions());
			
			GameLevel levelSimp = LevelParser.buildParser(levelString);
			levelSimp.setLegalMoves("simple", Filters.getBasicActions());
			
			//levels.add(level);
			levels.add(levelRel);
			levels.add(levelSimp);
			simpleLevels.add(levelSimp);
		}
		
		GenerateCSV csv = new GenerateCSV(String.format(CSV_FILE, System.currentTimeMillis()));
		csv.writeLine(
				"id",
				"player1", 
				"player2",
				"levelID",
				"actionSet",
				"score",
				"ticks",
				"dequal."
				);
		
		for (int i = 0; i < NUMBER_REPEATS; i++) {	
			long runStarted = System.currentTimeMillis();
			PrintStream out = new PrintStream(new FileOutputStream(String.format(RUN_STATS_FILE, runStarted)));
			
			// setup the games
			List<GameRunner> tasks = new ArrayList<GameRunner>(7 * NUMBER_RUNS);		
			
			for (int j = 0; j < NUMBER_RUNS; j++) {
				for (GameLevel level : levels) {
					
					//round robin pairings
					tasks.add(new GameRunner(level, new MCTS(true, 500, 10, 45), new MCTS(false, 500, 10, 45), TICK_LIMIT));
					tasks.add(new GameRunner(level, new RandomController(), new RandomController(), TICK_LIMIT));
					tasks.add(new GameRunner(level, new SortOfRandomController(), new SortOfRandomController(), TICK_LIMIT));
					tasks.add(new GameRunner(level, new MCTS(true, 500, 10, 45), new MCTS(false, 500, 10, 45), TICK_LIMIT));
					
					//predictor MCTS duel pairing
					NestedControllerPredictor predictorP1 = new NestedControllerPredictor(new MCTS(false, 500, 10, 45));
					NestedControllerPredictor predictorP2 = new NestedControllerPredictor(new MCTS(true, 500, 10, 45));
					tasks.add(new GameRunner(level, new PredictorMCTS(500, 10, 45, predictorP1), new PredictorMCTS(500, 10, 45, predictorP2), TICK_LIMIT));
					
					// MCTS co-op tests
					if (!simpleLevels.equals(level)) {
						NestedControllerPredictor predictor = new NestedControllerPredictor(new PassiveRefindController());
						tasks.add(new GameRunner(level, new MCTS(true, 500, 10, 45), new PassiveRefindController(), TICK_LIMIT));
						tasks.add(new GameRunner(level, new PredictorMCTS(500, 10, 45, predictor), new PassiveRefindController(), TICK_LIMIT));
					}
				}
			}
			referenceCount.addAndGet(tasks.size());
			
			// execute the games
			try {
				List<Future<GameResult>> results = service.invokeAll(tasks);
				printStats(results, out, csv);
				out.close();
				System.out.println("run complete "+ (System.currentTimeMillis() - runStarted));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		service.shutdown();
		
	}

	private static void printStats(Collection<Future<GameResult>> resultFutures, PrintStream out, GenerateCSV csv)
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

			csv.writeLine(
					result.id,
					result.setup.p1, 
					result.setup.p2,
					result.setup.levelID,
					result.setup.actionSet,
					result.score,
					result.ticks,
					result.disquals,
					result.userTime,
					result.wallTime
					);
			
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
		long startTimeUser = GameTimer.getUserTime();
		long startTimeWall = System.nanoTime();
		
		UUID id = UUID.randomUUID();
		GenerateCSV moves = new GenerateCSV(String.format(CSV_TRACE, id.toString()));
		moves.writeLine(
				"uuid",
				"player1",
				"player2",
				"level",
				"actionSet",
				"tick",
				"p1Move",
				"p1Pos",
				"p2Move",
				"p2Pos",
				"score",
				"p1Beep",
				"p2Beep",
				"p1Flare",
				"p2Flare",
				"singal0",
				"singal1",
				"singal2",
				"singal3",
				"singal4",
				"singal5",
				"p1VisitedGoal",
				"p2VisitedGoal"
				);
		
		// setup result entry already
		GameSetup setup = new GameSetup();
		setup.levelID = level.getLevelName();
		setup.actionSet = level.getActionSetName();
		setup.p1 = p1.getSimpleName();
		setup.p2 = p2.getSimpleName();

		GameResult result = new GameResult(setup);
		result.id = id;

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

			result.recordMoves(tickCount, p1Move, p2Move);
			moves.writeLine(id,
					setup.p1,
					setup.p2,
					setup.levelID,
					setup.actionSet,
					tickCount,
					p1Move,
					game.getPos(0),
					p2Move,
					game.getPos(1),
					game.getScore(),
					game.getBeep(0),
					game.getBeep(1),
					game.getFlare(0),
					game.getFlare(1),
					game.getSignalState(0),
					game.getSignalState(1),
					game.getSignalState(2),
					game.getSignalState(3),
					game.getSignalState(4),
					game.getSignalState(5),
					game.hasVisited(0, 0),
					game.hasVisited(1, 0)
					);
			
			if (!legalMoves1.contains(p1Move) || !legalMoves2.contains(p2Move)) {
				System.err.println("illegal move detected " + p1Move + " " + p2Move);
				result.disquals++;
				break;
			}

			game.update(p1Move, p2Move);
			
			tickCount++;
		}

		long timeTakenUser = GameTimer.getUserTime() - startTimeUser;
		long timeTakenWall = System.nanoTime() - startTimeWall;
		
		// record results
		result.score = game.getScore();
		result.ticks = tickCount;
		result.userTime = timeTakenUser/1_000_000f;
		result.wallTime = timeTakenWall/1_000_000f;
		moves.close();

		//System.out.println("game over, "+result.setup+" left: "+referenceCount.getAndDecrement()+" ticks: "+result.ticks);

		return result;
	}

}