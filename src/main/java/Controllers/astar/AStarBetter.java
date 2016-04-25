package Controllers.astar;

import Controllers.Controller;
import FastGame.FastAction;
import api.Action;
import api.GameState;

import java.util.List;

/**
 * Created by jwalto on 02/07/2015.
 */
public class AStarBetter extends Controller {
    private PathFind find;
    private boolean isFirst;
    private MovePair lastMove;

    public AStarBetter(boolean isFirst) {
        this.find = new PathFind(new GameScore());
        this.isFirst = isFirst;
        this.lastMove = new MovePair(FastAction.NOOP, FastAction.NOOP);
    }

    @Override
    public Controller getClone() {
        AStarBetter btr = new AStarBetter(isFirst);
        btr.lastMove = lastMove;
        return btr;
    }

    @Override
    public Action get(GameState game) {
        System.out.println("requested move");
        GameNode start = new GameNode(game, lastMove);
        List<MovePair> pairs = find.getPath(game, start);

        if (pairs.isEmpty()) {
            System.out.println("Unable to find path, perform random");
            return FastAction.NOOP;
            //return Action.getRandom();
        }

        if (pairs.size() == 1) {
            System.out.println();
            return FastAction.NOOP;
        }

        System.out.println(pairs);
        // this assumes the move the opponent played last was the "perfect" move, but it probably wasn't.
        lastMove = pairs.get(1);
        return isFirst?lastMove.p1Move:lastMove.p2Move;
    }
}
