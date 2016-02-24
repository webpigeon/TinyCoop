package gamesrc;

import java.util.ArrayList;
import java.util.List;

import FastGame.Action;

public class GameResult {

	public final GameSetup setup;
	
	public double score;
	public int ticks;
	public int disquals = 0;
	public List<MovePair> moves;
	
	public GameResult(GameSetup setup) {
		this.setup = setup;
		this.moves = new ArrayList<MovePair>();
	}
	
	public void recordMoves(Action p1, Action p2) {
		moves.add(new MovePair(p1, p2));
	}
	
	static class MovePair {
		Action p1;
		Action p2;
		
		MovePair(Action p1, Action p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
	}
}
