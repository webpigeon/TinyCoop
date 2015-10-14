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
	
	public boolean getBeep(int agent);
	public Point getFlare(int agent);

	public void setVisited(int agent, int goalID);
	public void setSignalState(int signal, boolean state);
	public boolean isWalkable(int pid, int x, int y);

}
