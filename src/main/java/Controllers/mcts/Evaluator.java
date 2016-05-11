package Controllers.mcts;

import java.util.Arrays;

public class Evaluator {
	private final static Double UCT_EXP_CONST = Math.sqrt(2d);
	// private final static Double UCT_EXP_CONST = 1d;

	/**
	 * Select best UCT node for a given set of arms
	 *
	 * @param wins
	 *            the wins for all children
	 * @param visits
	 *            the visits for all children
	 * @param totalVisits
	 * @return array of best node values
	 */
	public static int[] selectNode(double[] wins, double[] visits, int totalVisits) {
		assert wins.length == visits.length;

		double bestUCT = -1;
		int[] bestArms = new int[wins.length];
		int n = 0;

		for (int i = 0; i < wins.length; i++) {
			assert wins[i] >= 0;
			assert visits[i] >= 0;
			assert visits[i] > totalVisits;

			double uct = UCT(wins[i], visits[i], totalVisits);
			if (uct > bestUCT) {
				bestUCT = uct;
				bestArms[0] = i;
				n = 1;
			} else if (uct == bestUCT) {
				bestArms[n] = i;
				n++;
			}
		}

		assert n < visits.length;
		assert n >= 0;

		return Arrays.copyOf(bestArms, n);
	}

	/**
	 * Calculate UCT value
	 *
	 * @param wI
	 *            win rate for this arm
	 * @param nI
	 *            visit rate for this arm
	 * @param t
	 *            total visits for all arms
	 * @return the UCT value for this node
	 */
	public static double UCT(double wI, double nI, double t) {
		double exploitation = wI / nI;
		double exploration = UCT_EXP_CONST + Math.sqrt(Math.log(t) / nI);
		return exploitation + exploration;
	}

}
