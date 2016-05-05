package gamesrc.level;

import FastGame.ObjectTypes;
import gamesrc.SimpleGame;

class Button extends AbstractGameObject {
	private final Integer signal;

	public Button(int signal) {
		this.signal = signal;
	}

	@Override
	public int getSignal() {
		return signal;
	}

	@Override
	public int getType() {
		return ObjectTypes.BUTTON;
	}

	@Override
	public void onContact(SimpleGame state, int playerId) {
		if (state.getSignalState(signal) >= 0) {
			state.setSignalState(signal, true);
		}
	}

	@Override
	public void onContactEnd(SimpleGame state, int pid) {
		state.setSignalState(signal, false);
	}

}
