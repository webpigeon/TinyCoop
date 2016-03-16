package gamesrc.experiment;

import java.util.List;
import java.util.UUID;

public class GameTick {
	private String type;
	private UUID uuid;
	private String player1;
	private String player2;
	private String level;
	private String actionSet;
	private long tick;
	private List<Move> moves;
	
	public static class Move {
		int playerID;
		String move;
		String pos;
		boolean beep;
		String flare;
	}
	
}
