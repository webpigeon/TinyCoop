package Controllers.astar;

/**
 * Created by jwalto on 02/07/2015.
 */
public class GameScore implements Function<GameNode, Double> {

	@Override
	public Double apply(GameNode gameNode) {
		return gameNode.game.getScore();
	}

}
