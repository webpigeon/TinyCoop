package gamesrc.level;

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
	public ObjectType getType() {
		return ObjectType.BUTTON;
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
