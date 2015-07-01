package FastGame;

import Controllers.AStar;
import Controllers.Controller;
import Controllers.MCTS;
import Controllers.RandomController;
import Controllers.ga.GAController;
import utils.GenerateCSV;

import java.io.FileNotFoundException;
import java.lang.reflect.Executable;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by jwalto on 01/07/2015.
 */
public class RoundRobinMT {
    private final static Integer REPEATS = 10;
    private final static Integer MAX_TICKS = 2000;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException, ExecutionException {

        ExecutorService service = Executors.newFixedThreadPool(4);

        String[] maps = {
                "data/maps/level1.txt",
                "data/maps/level2.txt",
                "data/maps/level3.txt"
        };

        Controller[] player1List = new Controller[]{
                new MCTS(true, 500),
                new MCTS(true, 200),
                new GAController(true),
                new AStar(true),
                new RandomController()
        };

        Controller[] player2List = new Controller[] {
                new MCTS(false, 500),
                new MCTS(false, 200),
                new GAController(false),
                new AStar(false),
                new RandomController()
        };

        System.out.println("generating matchups");
        List<Matchup> tasks = new ArrayList<>();
        for (Controller p1 : player1List) {
            for (Controller p2 : player2List) {
                for (String map : maps) {
                    for (int trial = 0; trial<REPEATS; trial++) {
                        tasks.add(new Matchup(p1, p2, map, trial));
                    }
                }
            }
        }

        System.out.println("calculating results ("+tasks.size()+" tasks)");
        List<Future<Result>> results = service.invokeAll(tasks);

        System.out.println("Processing results");
        GenerateCSV csv = new GenerateCSV("results-mt.csv");
        for (Future<Result> resultf : results) {
            Result result = resultf.get();
            csv.writeLine(result.getP1(), result.getP2(), result.map, result.trialID, result.score, result.timeTaken);
        }
    }

    static class Matchup implements Callable<RoundRobinMT.Result> {
        Controller p1;
        Controller p2;
        String map;
        int trialID;

        Matchup(Controller p1, Controller p2, String map, int trialID) {
            this.p1 = p1;
            this.p2 = p2;
            this.map = map;
            this.trialID = trialID;
        }

        @Override
        public Result call() throws Exception {
            System.out.println("Running game");
            CoopGame game = new CoopGame(map);

            int ticksTaken = 0;
            while (ticksTaken < MAX_TICKS && !game.hasWon()) {
                game.update(p1.get(game.getClone()), p2.get(game.getClone()));
                ticksTaken++;
            }

            Result r = new Result();
            r.p1 = p1;
            r.p2 = p2;
            r.map = map;
            r.trialID = trialID;
            r.score = game.getScore();
            r.timeTaken = ticksTaken;

            System.out.println("game complete");
            return r;
        }
    }

    static class Result {
        Controller p1;
        Controller p2;
        String map;
        int trialID;
        double score;
        int timeTaken;

        public String getP1() {
            return p1.getClass().getSimpleName();
        }

        public String getP2() {
            return p2.getClass().getSimpleName();
        }
    }
}
