package FastGame;

import Controllers.AStar;
import Controllers.Controller;
import Controllers.MCTS;
import Controllers.RandomController;
import Controllers.VariGA.VariGA;
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
    private final static Integer REPEATS = 1;
    private final static Integer MAX_TICKS = 2000;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException, ExecutionException {

        ExecutorService service = Executors.newFixedThreadPool(4);

        String[] maps = {
                "data/maps/level1.txt",
                "data/maps/level2.txt",
                "data/maps/level3.txt"
        };

        Controller[] player1List = new Controller[]{
                new MCTS(true, 500, 5, 30),
                new MCTS(true, 200),
                new GAController(true),
                //new AStar(true),
                new RandomController(),
                new VariGA(true, 500)
        };

        Controller[] player2List = new Controller[] {
                new MCTS(false, 500, 5, 30),
                new MCTS(false, 200),
                new GAController(false),
                //new AStar(false),
                new VariGA(false, 500),
              //  new VariGA(false, 200)
                new RandomController()
        };

        while(!Thread.interrupted()) {
            System.out.println("generating matchups");
            List<Matchup> tasks = new ArrayList<>();
            for (Controller p1 : player1List) {
                for (Controller p2 : player2List) {
                    for (String map : maps) {
                        for (int trial = 0; trial < REPEATS; trial++) {
                            tasks.add(new Matchup(p1, p2, map, trial));
                        }
                    }
                }
            }

            System.out.println("calculating results (" + tasks.size() + " tasks)");
            List<Future<Result>> results = service.invokeAll(tasks);

            System.out.println("Processing results");
            GenerateCSV csv = new GenerateCSV(System.getenv("COMPUTERNAME") + "-results-mtp.csv");
            for (Future<Result> resultf : results) {
                Result result = resultf.get();
                csv.writeLine(result.getP1(), result.getP2(), result.map, result.trialID, result.score, result.timeTaken);
            }
            csv.close();
        }
        service.shutdown();
    }

    static class Matchup implements Callable<RoundRobinMT.Result> {
        static int count =0;
        Controller p1;
        Controller p2;
        String map;
        int trialID;

        Matchup(Controller p1, Controller p2, String map, int trialID) {
            this.p1 = p1.getClone();
            this.p2 = p2.getClone();
            this.map = map;
            this.trialID = trialID;
            count++;
        }

        @Override
        public Result call() throws Exception {
            long realTime = System.currentTimeMillis();
            try {
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
                r.realTimeTaken = System.currentTimeMillis() - realTime;
                count--;
                System.out.println("game complete "+r+" (" + count + " left) "+r.realTimeTaken);
                return r;
            } catch (Exception ex) {
                System.err.println("Error: "+ex);
                count--;
                Result r = new Result();
                r.p1 = p1;
                r.p2 = p2;
                r.map = map;
                r.trialID = trialID;
                r.score = -1;
                r.timeTaken = -1;
                r.realTimeTaken = System.currentTimeMillis() - realTime;
                return r;
            }
        }
    }

    static class Result {
        Controller p1;
        Controller p2;
        String map;
        int trialID;
        double score;
        int timeTaken;
        long realTimeTaken;

        public String getP1() {
            return p1.getSimpleName();
        }

        public String getP2() {
            return p2.getSimpleName();
        }

        public String toString() {
            return String.format("%s and %s (%d ticks, %f score)", p1.getSimpleName(), p2.getSimpleName(), timeTaken, score);
        }
    }
}
