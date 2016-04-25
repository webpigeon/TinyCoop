package gamesrc;

import static FastGame.ObjectTypes.OBJECT_COLOURS;
import static FastGame.ObjectTypes.TEXT_COLOURS;

import java.awt.Graphics;

import api.GameObject;
import api.ObservableGameState;

public abstract class AbstractGameObject implements GameObject {

	/* (non-Javadoc)
	 * @see gamesrc.GameObjectI#isWalkable(gamesrc.SimpleGame, int)
	 */
	@Override
	public boolean isWalkable(SimpleGame state, int playerId) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see gamesrc.GameObjectI#onContact(gamesrc.SimpleGame, int)
	 */
	@Override
	public abstract void onContact(SimpleGame state, int playerId);

	/* (non-Javadoc)
	 * @see gamesrc.GameObjectI#onContactEnd(gamesrc.SimpleGame, int)
	 */
	@Override
	public void onContactEnd(SimpleGame state, int pid) {}

	/* (non-Javadoc)
	 * @see gamesrc.GameObjectI#getSignal()
	 */
	@Override
	public abstract int getSignal();
	/* (non-Javadoc)
	 * @see gamesrc.GameObjectI#getType()
	 */
	@Override
	public abstract int getType();
	
	public void paint(int x, int y, int gridSize, ObservableGameState game, Graphics g) {
		g.setColor(OBJECT_COLOURS[getType()]);
        g.fillRect(x * gridSize, y * gridSize, gridSize, gridSize);
    	
    	g.setColor(TEXT_COLOURS[getType()]);
        g.drawString("" + getSignal(), x * gridSize + gridSize / 2, (y * gridSize) + gridSize / 2);
	}
}
