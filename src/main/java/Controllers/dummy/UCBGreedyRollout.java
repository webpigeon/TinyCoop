package Controllers.dummy;

import java.util.Arrays;

import api.Action;

public class UCBGreedyRollout extends GreedyRollout {

	@Override
	public int selectAction(Action[] legalActions) {

		int[] bestActions = new int[legalActions.length];
		int used = 0;

		double bestScore = -Double.MAX_VALUE;

		for (int action = 0; action < legalActions.length; action++) {
			double avgReward = 0;
			if (visits[action] != 0) {
				avgReward = scores[action] / visits[action];
			}
			double exploration = Math.sqrt((2 * Math.log(numRollouts)) / visits[action] + 1);
			double score = avgReward + exploration;

			if (bestScore < score) {
				bestScore = score;
				used = 1;
				bestActions[0] = action;
			} else if (bestScore == score) {
				bestActions[used] = action;
				used++;
			}
		}

		// if we have no data, select randomly
		if (used == 0) {
			return random.nextInt(legalActions.length);
		}

		System.out.println(Arrays.toString(legalActions));
		return bestActions[random.nextInt(used)];
	}

}
