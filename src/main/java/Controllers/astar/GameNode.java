package Controllers.astar;

import uk.me.webpigeon.phd.tinycoop.api.GameState;

/**
 * Created by jwalto on 02/07/2015.
 */
public class GameNode implements Comparable<GameNode> {
	protected GameState game;
	protected MovePair actions;

	public GameNode(GameState game, MovePair actions) {
		this.game = game;
		this.actions = actions;
	}

	@Override
	public int compareTo(GameNode o) {
		return Double.compare(o.game.getScore(), game.getScore());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		GameNode gameNode = (GameNode) o;

		if (!actions.equals(gameNode.actions))
			return false;
		if (!gameEquals(game, gameNode.game))
			return false;

		return true;
	}

	public boolean gameEquals(GameState game2, GameState game3) {
		return game2.equals(game3);
	}

	@Override
	public int hashCode() {
		int result = game.hashCode();
		result = 31 * result + actions.hashCode();
		return result;
	}

	public boolean isTerminal() {
		return game.hasWon() || game.getScore() == 1;
	}

	@Override
	public String toString() {
		return actions.toString() + " " + game.getScore();
	}
}
