package Controllers.dummy;

import java.util.List;

import uk.me.webpigeon.phd.tinycoop.api.Action;

public class UCBGreedyRollout extends GreedyRollout {

	@Override
	public int selectAction(List<Action> legalActions) {

		int[] bestActions = new int[legalActions.size()];
		int used = 0;

		double bestScore = -Double.MAX_VALUE;

		for (int action = 0; action < legalActions.size(); action++) {
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
			return random.nextInt(legalActions.size());
		}

		System.out.println(legalActions);
		return bestActions[random.nextInt(used)];
	}

}
