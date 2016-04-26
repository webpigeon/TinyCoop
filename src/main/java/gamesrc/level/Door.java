package gamesrc.level;

import static FastGame.ObjectTypes.OBJECT_COLOURS;
import static FastGame.ObjectTypes.TEXT_COLOURS;

import java.awt.Graphics;

import FastGame.ObjectTypes;
import api.ObservableGameState;
import gamesrc.SimpleGame;

class Door extends AbstractGameObject {
	private final Integer signal;
	
	public Door(int signal) {
		this.signal = signal;
	}

	@Override
	public boolean isWalkable(ObservableGameState state, int playerId) {
		return state.isSignalHigh(signal);
	}

	@Override
	public int getSignal() {
		return signal;
	}

	@Override
	public int getType() {
		return ObjectTypes.DOOR;
	}
	
}
