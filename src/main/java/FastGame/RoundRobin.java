package FastGame;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import Controllers.AStar;
import Controllers.Controller;
import Controllers.MCTS;
import Controllers.RandomController;
import Controllers.ga.GAController;
import utils.GenerateCSV;

/**
 * Created by jwalto on 01/07/2015.
 */
public class RoundRobin {
	static class Result {
		Controller p1;
		Controller p2;
		String map;
		int trialID;
		double score;
		int timeTaken;

		public String getP1() {
			return p1.getClass().getSimpleName();
		}

		public String getP2() {
			return p2.getClass().getSimpleName();
		}
	}

	private final static Integer REPEATS = 5;

	private final static Integer MAX_TICKS = 2000;

	public static void main(String[] args) throws FileNotFoundException {

		String[] maps = { "data/maps/level1.txt", "data/maps/level2.txt", "data/maps/level3.txt" };

		Controller[] player1List = new Controller[] { new MCTS(true, 500), new MCTS(true, 200), new GAController(true),
				new AStar(true), new RandomController() };

		Controller[] player2List = new Controller[] { new MCTS(false, 500), new MCTS(false, 200),
				new GAController(false), new AStar(false), new RandomController() };

		List<Result> results = new ArrayList<Result>();
		for (Controller p1 : player1List) {
			for (Controller p2 : player2List) {
				for (String map : maps) {
					for (int trial = 0; trial < REPEATS; trial++) {
						results.add(runTrial(p1, p2, map, trial));
					}
				}
			}
		}

		GenerateCSV csv = new GenerateCSV("results.csv");
		for (Result result : results) {
			csv.writeLine(result.getP1(), result.getP2(), result.map, result.trialID, result.score, result.timeTaken);
		}
	}

	private static Result runTrial(Controller player1, Controller player2, String map, int tid) {
		CoopGame game = new CoopGame(map);

		Controller p1 = player1.getClone();
		Controller p2 = player2.getClone();

		int ticksTaken = 0;
		while (ticksTaken < MAX_TICKS && !game.hasWon()) {
			game.update(p1.get(game.getClone()), p2.get(game.getClone()));
			ticksTaken++;
		}

		Result r = new Result();
		r.p1 = p1;
		r.p2 = p2;
		r.map = map;
		r.trialID = tid;
		r.score = game.getScore();
		r.timeTaken = ticksTaken;

		return r;
	}
}
