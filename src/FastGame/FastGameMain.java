package FastGame;

import Controllers.*;
import Controllers.VariGA.VariGA;
import Controllers.astar.AStarBetter;
import Controllers.ga.GAController;

import javax.swing.*;

/**
 * Created by pwillic on 25/06/2015.
 */
public class FastGameMain {

    public static void main(String[] args) throws InterruptedException {

        JFrame frame = new JFrame("Game");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        WASDController wasd = new WASDController();
        ArrowController arrows = new ArrowController();

//        Controller c1 = new GAController(true);
//        Controller c1 = new VariGA(true, 500);
        //Controller c1 = new RandomController();
          Controller c1 = new NoOp();
//        Controller c1 = wasd;
        Controller c2 = new AStarBetter(false);
//        Controller c2 = new GAController(false);
//        Controller c2 = new VariGA(false, 500);

//        Controller c2 = new RandomController();
//        Controller c2 = arrows;

        CoopGame game = new CoopGame("data/maps/level1E.txt");

        Viewer viewer = new Viewer(game);
        viewer.addKeyListener(wasd);
        viewer.addKeyListener(arrows);
        viewer.setFocusable(true);
        viewer.requestFocus();

        frame.add(viewer);
        frame.pack();
        frame.setVisible(true);

        while (!game.hasWon()) {
            game.update(c1.get(game.getClone()), c2.get(game.getClone()));
//            Thread.sleep(40);
            viewer.repaint();
        }
    }
}
