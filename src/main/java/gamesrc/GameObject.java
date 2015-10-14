package gamesrc;

import static FastGame.ObjectTypes.OBJECT_COLOURS;
import static FastGame.ObjectTypes.TEXT_COLOURS;

import java.awt.Graphics;

public abstract class GameObject {

	public boolean isWalkable(ObservableGameState state, int playerId) {
		return true;
	}
	
	public abstract void onContact(ObservableGameState state, int playerId);

	public void onContactEnd(ObservableGameState state, int pid) {}

	public abstract int getSignal();
	public abstract int getType();
	
	public void paint(int x, int y, int gridSize, ObservableGameState game, Graphics g) {
		g.setColor(OBJECT_COLOURS[getType()]);
        g.fillRect(x * gridSize, y * gridSize, gridSize, gridSize);
    	
    	g.setColor(TEXT_COLOURS[getType()]);
        g.drawString("" + getSignal(), x * gridSize + gridSize / 2, (y * gridSize) + gridSize / 2);
	}
}
