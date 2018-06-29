package FastGame;

import Controllers.*;

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

//        Controller c1 = new PathMCTS(true, 10000, 10, 45);
//        Controller c1 = new VariGA(true, 2000);
//        Controller c1 = new RandomController();

//        Controller c1 = new VariGA(true, 500);
//        Controller c1 = new RandomController();
//          Controller c1 = new NoOp();

        Controller c1 = wasd;
//        Controller c2 = new GAController(false);
//        Controller c2 = new DrawingMCTS(false, 1000, 10, 45);
//            Controller c2 = new VariGA(false, 2000);
//        Controller c2 = new RandomController(1);
        Controller c2 = arrows;
//        Controller c2 = new NoOp();

        CoopGame game = new CoopGame("data/maps/level1.txt", false);

        Viewer viewer = new ControllerViewer(game, c1, null);
        viewer.addKeyListener(wasd);
        viewer.addKeyListener(arrows);
        viewer.setFocusable(true);
        viewer.requestFocus();

        frame.add(viewer);
        frame.pack();
        frame.setVisible(true);

        int ticks = 0;
        while (!game.hasWon()) {
            game.update(c1.get(game.getClone()), c2.get(game.getClone()));
            ticks++;
//            viewer.paintImmediately(viewer.getBounds());
//            ViewSaver.saveToFile(viewer, "../../../views/level1/" + ticks + ".png");
            viewer.repaint();
            Thread.sleep(1000);
        }
        frame.setVisible(false);
        frame.dispose();
        System.out.println(ticks);
    }
}
