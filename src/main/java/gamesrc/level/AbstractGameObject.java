package gamesrc.level;

import java.awt.Graphics;

import api.GameObject;
import api.ObservableGameState;
import gamesrc.SimpleGame;

abstract class AbstractGameObject implements GameObject {

	/*
	 * (non-Javadoc)
	 *
	 * @see gamesrc.GameObjectI#getSignal()
	 */
	@Override
	public abstract int getSignal();

	/*
	 * (non-Javadoc)
	 *
	 * @see gamesrc.GameObjectI#getType()
	 */
	@Override
	public abstract ObjectType getType();

	/*
	 * (non-Javadoc)
	 *
	 * @see gamesrc.GameObjectI#isWalkable(gamesrc.SimpleGame, int)
	 */
	@Override
	public boolean isWalkable(ObservableGameState state, int playerId) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gamesrc.GameObjectI#onContact(gamesrc.SimpleGame, int)
	 */
	public void onContact(SimpleGame state, int playerId) {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gamesrc.GameObjectI#onContactEnd(gamesrc.SimpleGame, int)
	 */
	public void onContactEnd(SimpleGame state, int pid) {
	}

	public void paint(int x, int y, int gridSize, ObservableGameState game, Graphics g) {
		ObjectType type = getType();
		g.setColor(type.getObjectColor());
		g.fillRect(x * gridSize, y * gridSize, gridSize, gridSize);

		g.setColor(type.getTextColor());
		g.drawString("" + getSignal(), x * gridSize + gridSize / 2, (y * gridSize) + gridSize / 2);
	}
}
