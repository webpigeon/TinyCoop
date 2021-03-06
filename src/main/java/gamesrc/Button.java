package gamesrc;

import java.awt.Graphics;

import FastGame.ObjectTypes;

public class Button extends GameObject {
	private final Integer signal;
	
	public Button(int signal) {
		this.signal = signal;
	}

	@Override
	public void onContact(ObservableGameState state, int playerId) {
		if (state.getSignalState(signal) >= 0) {
			state.setSignalState(signal, true);
		}
	}

	@Override
	public void onContactEnd(ObservableGameState state, int pid) {
		state.setSignalState(signal, false);
	}

	@Override
	public int getSignal() {
		return signal;
	}

	@Override
	public int getType() {
		return ObjectTypes.BUTTON;
	}

}
