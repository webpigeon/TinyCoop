package Controllers.astar;

import FastGame.Action;
import FastGame.CoopGame;

/**
 * Created by jwalto on 02/07/2015.
 */
public class GameTest {

    public static void main(String[] args) {
        CoopGame game = new CoopGame("data/maps/level1E.txt");

        CoopGame c1 = game.getClone();
        CoopGame c2 = game.getClone();

        Action noop = new Action(0,0);
        Action up = new Action(0, 1);
        Action down = new Action(0, -1);
        
        MovePair pair1 = new MovePair(noop, noop);
        MovePair pair2 = new MovePair(down, up);
        System.out.println(pair1.equals(pair2));

        GameNode node1 = new GameNode(c1, pair1);
        GameNode node2 = new GameNode(c2, pair2);
        System.out.println(node1.equals(node2));
    }
}
