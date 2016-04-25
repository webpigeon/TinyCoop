package backtrack.domain;

import java.io.IOException;
import java.util.List;

import FastGame.FastAction;
import actions.Action;
import backtrack.State;
import gamesrc.Filters;
import gamesrc.GameLevel;
import gamesrc.GameState;
import gamesrc.LevelParser;
import gamesrc.SimpleGame;
import backtrack.BredthFirstSearch;
import backtrack.Dijkstra;
import backtrack.IDSearch;
import backtrack.Search;

public class SearchTest {

	public static void main(String[] args) throws IOException {
		Search search = new BredthFirstSearch();
		testGrid(search);
	}
	
	private static void testGrid(Search search) throws IOException {
		GameLevel level = LevelParser.buildParser("data/maps/level1E.txt");
		level.setLegalMoves("relative", Filters.getAllRelativeActions());
		SimpleGame game = new SimpleGame(level);
		
		GameState start = game.getClone();
		SimpleGame end = (SimpleGame)game.getClone();
		end.setVisited(0, 1);
		
		List<Action> goalResult = search.search(start, end);
		
		System.out.println(end);
		System.out.println(goalResult);
	}

}
