package gamesrc;

import actions.Action;

public interface GameState {
	public static final Integer PLAYER_0 = 0;
	public static final Integer PLAYER_1 = 1;
	
	
	public GameState getClone();
	public double getScore();
	public boolean hasWon();
	public void update(Action p1, Action p2);
	
	
	public Action[] getLegalActions(int playerID);
	public int getActionLength();
	
	public int getWidth();
	public int getHeight();

}
