package gamesrc;

import FastGame.ObjectTypes;

public class Goal extends GameObject {
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
