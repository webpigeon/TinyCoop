package gamesrc;

import FastGame.Action;

public interface GameState {
	
	public GameState getClone();
	public double getScore();
	public boolean hasWon();
	public void update(Action p1, Action p2);
	
	public int getWidth();
	public int getHeight();

}
