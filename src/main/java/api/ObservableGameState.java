package api;

import java.awt.Point;

public interface ObservableGameState extends GameState {

	public Flare getFlare(int agent);

	public int getFloor(int x, int y);

	public int getGoalsCount();

	public GameObject getObject(int x, int y);

	public Point getPos(int i);

	public int getSignalState(int signal);

	public boolean hasVisited(int agent, int goalID);

	public boolean isSignalHigh(int signal);

	public boolean isWalkable(int pid, int x, int y);
}
