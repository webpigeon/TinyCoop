package gamesrc;

import java.awt.Point;
import java.util.List;

import FastGame.Action;

public interface GameState {
	
	public GameState getClone();
	public double getScore();
	public boolean hasWon();
	public void update(Action p1, Action p2);
	public Point getPos(int i);
	
	public List<Action> getLegalActions();

}
