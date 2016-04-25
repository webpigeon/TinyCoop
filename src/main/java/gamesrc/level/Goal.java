package gamesrc.level;

import FastGame.ObjectTypes;
import gamesrc.SimpleGame;

class Goal extends AbstractGameObject {
	private int goalId;
	
	public Goal(int goalId) {
		this.goalId = goalId;
	}

	@Override
	public void onContact(SimpleGame state, int playerId) {
		state.setVisited(playerId, goalId);
	}
	
	@Override
	public int getSignal() {
		return goalId;
	}

	@Override
	public int getType() {
		return ObjectTypes.GOAL;
	}
}
