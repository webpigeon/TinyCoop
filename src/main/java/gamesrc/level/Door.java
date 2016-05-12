package gamesrc.level;

import FastGame.ObjectTypes;
import api.ObservableGameState;

class Door extends AbstractGameObject {
	private final Integer signal;

	public Door(int signal) {
		this.signal = signal;
	}

	@Override
	public int getSignal() {
		return signal;
	}

	@Override
	public ObjectType getType() {
		return ObjectType.DOOR;
	}

	@Override
	public boolean isWalkable(ObservableGameState state, int playerId) {
		return state.isSignalHigh(signal);
	}

}
