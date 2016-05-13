package uk.me.webpigeon.phd.search.domain;

import java.io.IOException;
import java.util.List;

import uk.me.webpigeon.phd.search.BredthFirstSearch;
import uk.me.webpigeon.phd.search.Search;
import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.GameState;
import uk.me.webpigeon.phd.tinycoop.engine.Filters;
import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;
import uk.me.webpigeon.phd.tinycoop.engine.level.GameLevel;
import uk.me.webpigeon.phd.tinycoop.engine.level.LevelParser;

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
		SimpleGame end = game.getClone();
		end.setVisited(0, 1);

		List<Action> goalResult = search.search(start, end);

		System.out.println(end);
		System.out.println(goalResult);
	}

}
