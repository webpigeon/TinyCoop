package Controllers.astar;

        import FastGame.Action;
        import FastGame.CoopGame;

        import java.awt.Point;
        import java.util.*;
        import java.util.function.Function;

/**
 * An implementation of A* for path finding
 */
public class PathFind {
    private Function<GameNode, Double> heristic;
    Map<GameNode, GameNode> cameFrom = new HashMap<>();
    Map<GameNode, Double> gScores = new HashMap<>();
    Map<GameNode, Double> fScores = new HashMap<>();

    public PathFind(Function<GameNode, Double> heristic) {
        this.heristic = heristic;
    }

    public List<MovePair> getPath(CoopGame game, GameNode start) {
        List<GameNode> closedSet = new ArrayList<GameNode>();
        Queue<GameNode> openSet = new PriorityQueue<GameNode>(new Comparator<GameNode>() {

            @Override
            public int compare(GameNode o1, GameNode o2) {
                System.out.println(o1);
                System.out.println(o2);
                return Double.compare(fScores.getOrDefault(o1, 0.0),fScores.getOrDefault(o2, 0.0)) * -1;
            }
        });
        openSet.add(start);

        while(!openSet.isEmpty()) {
            GameNode current = openSet.poll();
            System.out.println(current);
            if (current.isTerminal()) {
                return buildPath(current, cameFrom);
            }
            openSet.remove(current);
            closedSet.add(current);

            for (MovePair moves : getAvailableMoves(game)) {
                CoopGame nState = current.game.getClone();
                nState.update(moves.p1Move, moves.p2Move);
                GameNode neighbor = new GameNode(nState, moves);

                if (closedSet.contains(neighbor)) {
                    continue;
                }
                Double gScore = gScores.get(current);
                if (gScore == null) {
                    gScore = 0.0;
                }
                gScore += 1;

                if ( !openSet.contains(neighbor) || gScore > gScores.getOrDefault(neighbor, 0.0) ) {
                    cameFrom.put(neighbor, current);
                    gScores.put(neighbor, gScore);
                    fScores.put(neighbor, gScore + heristic.apply(neighbor));
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return new LinkedList<MovePair>();
    }

    public static LinkedList<MovePair> buildPath(GameNode current, Map<GameNode,GameNode> cameFrom) {
        LinkedList<MovePair> path = new LinkedList<>();
        path.addFirst(current.actions);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.addFirst(current.actions);
        }

        return path;
    }

    public static List<MovePair> getAvailableMoves(CoopGame game){

        List<MovePair> legalActions = new ArrayList<MovePair>();
        for (Action p1 : Action.allActions) {
            for (Action p2 : Action.allActions) {
                legalActions.add(new MovePair(p1, p2));
            }
        }

        return legalActions;
    }

}