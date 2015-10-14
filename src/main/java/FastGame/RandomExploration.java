package FastGame;

import Controllers.Controller;
import Controllers.RandomController;

import java.util.ArrayList;

/**
 * Created by pwillic on 06/08/2015.
 */
public class RandomExploration {

    public static final int runs = 100_000;

    public static void main(String[] args) {


        Controller c1 = new RandomController();
        Controller c2 = new RandomController();

        ArrayList<CoopGame> games = new ArrayList<>();
        games.add(new CoopGame("data/maps/level1.txt"));
        games.add(new CoopGame("data/maps/level1E.txt"));
        games.add(new CoopGame("data/maps/level2.txt"));
        games.add(new CoopGame("data/maps/level3.txt"));
        games.add(new CoopGame("data/maps/level4.txt"));
        games.add(new CoopGame("data/maps/level5.txt"));
        games.add(new CoopGame("data/maps/level6.txt"));

        for(CoopGame theGame : games) {
            int total = 0;
            for (int i = 0; i < runs; i++) {
                CoopGame game = theGame.getClone();
                int ticks = 0;
                while (!game.hasWon()) {
                    game.update(c1.get(game), c2.get(game));
                    ticks++;
                }
//                System.out.println("Ticks" + ticks);
                total += ticks;
            }

            System.out.println("Average ticks to complete: " + (total / runs));
        }
    }
}
