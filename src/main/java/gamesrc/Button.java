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
		System.out.println("signal raised");
		state.setSignalState(signal, true);
	}
	
	public void onContactEnd(ObservableGameState state, int pid) {
		System.out.println("signal lowered");
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
