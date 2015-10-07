package gamesrc;

import java.awt.Point;

public interface ObservableGameState extends GameState {
	
	public boolean isSignalHigh(int signal);
	public boolean hasVisited(int agent, int goalID);
	public GameObject getObject(int x, int y);
	public Point getPos(int i);
	public int getFloor(int x, int y);
	public int getSignalState(int signal);
	public int getGoalsCount();

	public void setVisited(int agent, int goalID);
	public void setSignalState(int signal, boolean state);

}
