package Controllers.astar;

import java.util.function.Function;

/**
 * Created by jwalto on 02/07/2015.
 */
public class GameScore implements Function<GameNode,Double> {

    @Override
    public Double apply(GameNode gameNode) {
        return gameNode.game.getScore();
    }

    @Override
    public <V> Function<V, Double> compose(Function<? super V, ? extends GameNode> before) {
        return null;
    }

    @Override
    public <V> Function<GameNode, V> andThen(Function<? super Double, ? extends V> after) {
        return null;
    }
}
