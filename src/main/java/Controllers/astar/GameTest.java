package Controllers.astar;

import FastGame.FastAction;
import FastGame.CoopGame;

/**
 * Created by jwalto on 02/07/2015.
 */
public class GameTest {

    public static void main(String[] args) {
        CoopGame game = new CoopGame("data/maps/level1E.txt");

        CoopGame c1 = game.getClone();
        CoopGame c2 = game.getClone();

        MovePair pair1 = new MovePair(FastAction.UP, FastAction.NOOP);
        MovePair pair2 = new MovePair(FastAction.DOWN, FastAction.NOOP);
        System.out.println(pair1.equals(pair2));

        GameNode node1 = new GameNode(c1, pair1);
        GameNode node2 = new GameNode(c2, pair2);
        System.out.println(node1.equals(node2));
    }
}
