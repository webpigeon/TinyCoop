package uk.me.webpigeon.phd.tinycoop.engine.level;

import java.awt.Color;
import java.awt.Graphics;

import uk.me.webpigeon.phd.tinycoop.api.ObservableGameState;
import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;

/**
 * Trap door object.
 *
 * Once someone walks though the trap door object, the door will subtract 1 from
 * the signal state (disabling it).
 */
class TrapDoor extends AbstractGameObject {
	private final Integer signal;

	public TrapDoor(int signal) {
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

	@Override
	public void onContact(SimpleGame state, int playerId) {
		state.setSignalState(signal, false);
	}

	@Override
	public void paint(int x, int y, int gridSize, ObservableGameState game, Graphics g) {
		ObjectType type = getType();
		Color objColour = type.getObjectColor();
		
		g.setColor(type.getObjectColor());
		if (game.isSignalHigh(getSignal())) {
			g.drawRect(x * gridSize, y * gridSize, gridSize, gridSize);
		} else {
			g.fillRect(x * gridSize, y * gridSize, gridSize, gridSize);
		}

		g.setColor(type.getTextColor());
		g.drawString("" + getSignal(), x * gridSize + gridSize / 2, (y * gridSize) + gridSize / 2);
	}
}
