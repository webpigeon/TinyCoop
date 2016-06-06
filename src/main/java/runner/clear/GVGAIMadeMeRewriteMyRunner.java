package runner.clear;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import runner.cli.ControllerUtils;
import uk.me.webpigeon.phd.tinycoop.api.GameState;
import uk.me.webpigeon.phd.tinycoop.api.controller.Controller;
import uk.me.webpigeon.phd.tinycoop.engine.Filters;
import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;
import uk.me.webpigeon.phd.tinycoop.engine.level.GameLevel;
import uk.me.webpigeon.phd.tinycoop.engine.level.LevelParser;
import uk.me.webpigeon.phd.tinycoop.runner.GameExecutor;
import uk.me.webpigeon.phd.tinycoop.runner.GameResult;
import uk.me.webpigeon.phd.tinycoop.runner.GameSetup;
import utils.GenerateCSV;

public class GVGAIMadeMeRewriteMyRunner {
	private static final Integer NUM_RUNS = 10;

	public static GameSetup buildSetup(Controller p1, Controller p2, GameLevel level) {
		GameSetup setup = new GameSetup(p1.getFriendlyName(), p2.getFriendlyName(), level.getLevelName(),
				level.getActionSetName());
		return setup;
	}

	public static void main(String[] args) throws Exception {
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
		List<GameResult> results = new ArrayList<>();

		for (String level : levels) {
			GameLevel levelRel = LevelParser.buildParser(level);
			levelRel.setLegalMoves("relative", Filters.getBasicActionsWithComms());

			for (int i = 0; i < NUM_RUNS; i++) {
				for (String agent1 : player1List) {
					for (String agent2 : player2List) {
						// setup (other agent as 1)
						SimpleGame game = new SimpleGame(levelRel);
						Controller predictorAgentP2 = controllers.parseDescription(GameState.PLAYER_1, game, agent2);
						Controller testAgentP1 = controllers.parseDescription(GameState.PLAYER_0, game, agent1,
								predictorAgentP2);
						Controller coopAgentP2 = controllers.parseCoopDescription(GameState.PLAYER_1, game, agent2);
						
						GameExecutor executor = buildGame(levelRel, game, testAgentP1, coopAgentP2);
						GameResult result = executor.call();
						results.add(result);

						// setup (other agent as 2)
						SimpleGame game2 = new SimpleGame(levelRel);
						Controller predictorAgentP1 = controllers.parseDescription(GameState.PLAYER_0, game, agent2);
						Controller coopAgentP1 = controllers.parseCoopDescription(GameState.PLAYER_0, game, agent2);
						Controller testAgentP2 = controllers.parseDescription(GameState.PLAYER_1, game, agent1,
								predictorAgentP1);
						
						GameExecutor executor2 = buildGame(levelRel, game2, coopAgentP1, testAgentP2);
						GameResult result2 = executor2.call();
						results.add(result2);
					}
				}
			}
		}
		
		writeResults(results, UUID.randomUUID().toString());
	}
	
	public static GameExecutor buildGame(GameLevel level, SimpleGame game, Controller p1, Controller p2) {
			GameSetup setup = new GameSetup(p1.getFriendlyName(), p2.getFriendlyName(), level.getLevelName(), level.getActionSetName());
			GameExecutor ec = new GameExecutor(setup, game, p1, p2);
			return ec;
	}

	
	private static void writeResults(List<GameResult> results, String runID) {
		//this is used to keep track of restarts
		try (
				OutputStream out = new FileOutputStream(String.format("results-%s.csv", runID), true);
				){
			GenerateCSV csv = new GenerateCSV(out);
			csv.writeLine("gameID","levelID","actionSet","player1","player2","result","score","ticks","wallTime","userTime","runID");
			
			for (GameResult result : results) {
				
				//detect timeouts and respond correctly
				if (result != null) {
					csv.writeLine(result.setup.id,
							result.setup.level,
							result.setup.actionSet,
							result.setup.p1,
							result.setup.p2,
							result.result,
							result.score,
							result.ticks,
							result.wallTime,
							result.cpuTime,
							runID);
				}
				
			}
			
			csv.close();
			
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
	}
}
