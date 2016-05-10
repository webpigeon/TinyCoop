package api;

import java.util.List;

public interface GameState {
	public static final Integer PLAYER_0 = 0;
	public static final Integer PLAYER_1 = 1;

	public int getActionLength();

	public GameState getClone();

	public int getHeight();

	public List<Action> getLegalActions(int playerID);

	public double getScore();

	public int getWidth();

	public boolean hasWon();

	public void update(Action p1, Action p2);

}
