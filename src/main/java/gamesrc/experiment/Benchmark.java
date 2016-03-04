package gamesrc.experiment;

import java.io.IOException;

import FastGame.CoopGame;
import gamesrc.Filters;
import gamesrc.GameLevel;
import gamesrc.LevelParser;
import gamesrc.SimpleGame;
import utils.StatSummary;

public class Benchmark {

	public static void main(String[] args) throws IOException {
	
		
		long startTime = System.currentTimeMillis();
		GameLevel level = LevelParser.buildParser("data/maps/level2.txt");
		level.setLegalMoves("relative", Filters.getAllRelativeActions());
		SimpleGame game = new SimpleGame(level);
		for (int i=0; i<10_000_000; i++) {
			game = new SimpleGame(game);
		}
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
		
		long startTime2 = System.currentTimeMillis();
		CoopGame fastGame = new CoopGame("data/maps/level2.txt");
		for (int i=0; i<10_000_000; i++) {
			fastGame = fastGame.getClone();
		}
		long endTime2 = System.currentTimeMillis();
		System.out.println(endTime2 - startTime2);
		
	}

}
