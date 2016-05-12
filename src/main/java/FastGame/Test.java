package FastGame;

import java.util.Arrays;

import Controllers.PiersController;
import Controllers.MCTS;
import Controllers.RandomController;

/**
 * Created by pwillic on 30/06/2015.
 */
public class Test {

	public static void main(String[] args) {
		// Controller m1 = new PlanningController();
		PiersController m2 = new MCTS(false, 500);
		PiersController r1 = new RandomController();

		// Controller c1 = m1;
		// Controller c1 = new RandomController();
		// Controller c2 = new MCTS(false);
		PiersController c2 = m2;

		int gamesPerMatchup = 10;
		// double[] results1 = runGames(m1, m2, gamesPerMatchup);
		// System.out.println(Arrays.toString(results1));
		// double[] results2 = runGames(m1, r1, gamesPerMatchup);
		// System.out.println(Arrays.toString(results2));
		double[] results3 = runGames(r1, r1, gamesPerMatchup);
		System.out.println(Arrays.toString(results3));
		// System.out.println(results2);
		// System.out.println(results3);
	}

	public static double[] runGames(PiersController c1, PiersController c2, int gamesPerMatchup) {
		double[] results = new double[2];
		for (int i = 0; i < gamesPerMatchup; i++) {
			int ticksTaken = 0;
			CoopGame game = new CoopGame("data/maps/level2.txt");
			while (ticksTaken < 2000 && !game.hasWon()) {
				game.update(c1.get(game.getClone()), c2.get(game.getClone()));
				ticksTaken++;
			}
			results[0] += game.getScore();
			results[1] += ticksTaken;
			System.out.println("Game: " + i);
		}
		return results;
	}
}
