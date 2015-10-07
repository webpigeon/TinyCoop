package gamesrc;

import static FastGame.ObjectTypes.OBJECT_COLOURS;
import static FastGame.ObjectTypes.TEXT_COLOURS;

import java.awt.Graphics;

import FastGame.ObjectTypes;

public class TrapDoor extends GameObject {
	private final Integer signal;
	
	public TrapDoor(int signal) {
		this.signal = signal;
	}

	@Override
	public boolean isWalkable(ObservableGameState state, int playerId) {
		return state.isSignalHigh(signal);
	}
	
	@Override
	public void onContact(ObservableGameState state, int playerId) {
		state.setSignalState(signal, false);
	}

	@Override
	public int getSignal() {
		return signal;
	}

	@Override
	public int getType() {
		return ObjectTypes.DOOR;
	}
	
	public void paint(int x, int y, int gridSize, ObservableGameState game, Graphics g) {
		g.setColor(OBJECT_COLOURS[getType()]);
    	if (game.isSignalHigh(getSignal())) {
            g.drawRect(x * gridSize, y * gridSize, gridSize, gridSize);
    	} else{
            g.fillRect(x * gridSize, y * gridSize, gridSize, gridSize);
    	}
    	
    	g.setColor(TEXT_COLOURS[getType()]);
        g.drawString("" + getSignal(), x * gridSize + gridSize / 2, (y * gridSize) + gridSize / 2);
	}
}
