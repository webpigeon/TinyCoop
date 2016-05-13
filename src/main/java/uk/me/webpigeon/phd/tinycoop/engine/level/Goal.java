package uk.me.webpigeon.phd.tinycoop.engine.level;

import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;

class Goal extends AbstractGameObject {
	private int goalId;

	public Goal(int goalId) {
		this.goalId = goalId;
	}

	@Override
	public int getSignal() {
		return goalId;
	}

	@Override
	public ObjectType getType() {
		return ObjectType.GOAL;
	}

	@Override
	public void onContact(SimpleGame state, int playerId) {
		state.setVisited(playerId, goalId);
	}
}
