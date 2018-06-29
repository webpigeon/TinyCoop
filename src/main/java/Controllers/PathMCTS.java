package Controllers;

import FastGame.Action;
import FastGame.CoopGame;
import gamesrc.GameState;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by pwillic on 23/06/2015.
 */
public class PathMCTS extends Controller {

    protected Random random = new Random();
    private int maxUCTDepth = 5;
    private int maxRolloutDepth = 30;
    private int iterationLimit = 0;
    private PathMCTSNode root;

    private boolean first;

    public PathMCTS(boolean first, int iterationLimit, int maxUCTDepth, int maxRolloutDepth) {
        this.first = first;
        this.iterationLimit = iterationLimit;
        this.maxUCTDepth = maxUCTDepth;
        this.maxRolloutDepth = maxRolloutDepth;
    }

    public PathMCTS(boolean first, int iterationLimit) {
        this.first = first;
        this.iterationLimit = iterationLimit;
    }

    @Override
    public Action get(GameState game) {
        CoopGame actualState = (CoopGame) game;
        root = new PathMCTSNode(2.0, this, game.getActionLength());
        PathMCTSNode travel;
        CoopGame workingGame;
        int iterations = 0;
        while (iterations < iterationLimit) {
            workingGame = actualState.getClone();
            travel = root.select(workingGame);
            double result = travel.rollout(workingGame);
            travel.updateValues(result);
            iterations++;
        }
//        System.out.println("Iterations : " + iterations);
//        System.out.println(root.getBestAction());
        return root.getBestAction();
    }

    public int getMaxUCTDepth() {
        return maxUCTDepth;
    }

    public int getMaxRolloutDepth() {
        return maxRolloutDepth;
    }

    public boolean isFirst() {
        return first;
    }

    @Override
    public String getSimpleName() {
        return "PathMCTS: (" + iterationLimit + ";" + maxUCTDepth + ";" + maxRolloutDepth + ")";
    }

    @Override
    public void paint(Graphics g, Point pos, int gridSize) {
        List<List<Point>> paths = root.getPaths();
        List<Double> pathScores = root.getPathScores();
        for (int i = 0; i < paths.size(); i++) {
            List<Point> path = paths.get(i);
            float score = pathScores.get(i).floatValue();

            paintPath((Graphics2D) g, gridSize, path, score);
        }
    }

    private void paintPath(Graphics2D g, int gridSize, List<Point> path, float hue){
        Iterator<Point> pathIterator = path.iterator();
        Point first = pathIterator.next();
        g.setStroke(new BasicStroke(3));
        Color rgba = new Color(1 - hue , hue,  0, 0.02f);
//        Color color = new Color(rgba.getRed(), rgba.getBlue(), rgba.getGreen(), 10);
        while(pathIterator.hasNext()){
            Point second = pathIterator.next();
            g.setColor(rgba);
            g.drawLine(
                    (first.x * gridSize) + (gridSize / 2),
                    (first.y * gridSize) + (gridSize / 2),
                    (second.x * gridSize) + (gridSize / 2),
                    (second.y * gridSize) + (gridSize / 2)
            );
            first = second;
        }
    }

}

class PathMCTSNode {

    private static final double EPSILON = 1e-6;

    private double explorationConstant;
    private Action moveToThisState;

    private double totalValue;
    private int numberOfVisits;
    private int currentDepth;
    private int childrenExpandedSoFar = 0;

    private PathMCTSNode parent;
    private PathMCTSNode[] children;
    private int childLength;

    private PathMCTS mcts;

    private List<List<Point>> paths;
    private List<Double> pathScores;

    // Root
    public PathMCTSNode(double explorationConstant, PathMCTS mcts, int actionLength) {
        this.explorationConstant = explorationConstant;
        this.currentDepth = 0;
        this.mcts = mcts;
        this.childLength = actionLength;
        this.paths = new ArrayList<>();
        this.pathScores = new ArrayList<>();
    }

    // Child
    public PathMCTSNode(PathMCTSNode parent, Action moveToThisState, int actionLength) {
        this.parent = parent;
        this.explorationConstant = parent.explorationConstant;
        this.moveToThisState = moveToThisState;
        this.currentDepth = parent.currentDepth + 1;
        this.mcts = parent.mcts;
        this.childLength = actionLength;
    }

    protected PathMCTSNode select(CoopGame state) {
        PathMCTSNode current = this;
        List<Point> path = new ArrayList<>();
        paths.add(path);
        Point location = state.getPos(mcts.isFirst() ? 0 : 1);
        path.add(location);
        while (current.currentDepth < mcts.getMaxUCTDepth() && !state.hasWon()) {
            if (current.isFullyExpanded()) {
                current = current.selectBestChild();
                if (mcts.isFirst()) {
                    state.update(current.getMoveToThisState(), Action.getRandom(1, state));
                } else {
                    state.update(Action.getRandom(0, state), current.getMoveToThisState());
                }
                location = state.getPos(mcts.isFirst() ? 0 : 1);
                path.add(location);
            } else {
                /// Expand
                PathMCTSNode expandedChild = current.expand(state);
                if (mcts.isFirst()) {
                    state.update(expandedChild.getMoveToThisState(), Action.getRandom(1, state));
                } else {
                    state.update(Action.getRandom(0, state), expandedChild.getMoveToThisState());
                }
                location = state.getPos(mcts.isFirst() ? 0 : 1);
                path.add(location);
                return expandedChild;
            }
        }
        return current;
    }

    protected PathMCTSNode selectBestChild() {
        int selected = 0;
        double bestValue = children[0].calculateChild();
        for (int child = 1; child < children.length; child++) {
            double childValue = children[child].calculateChild();
            if (childValue > bestValue) {
                selected = child;
                bestValue = childValue;
            }
        }
        return children[selected];
    }

    protected Action getBestAction() {
        if (children == null) return Action.NOOP;
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;
        for (int child = 0; child < children.length; child++) {
            if (children[child] == null) continue;
            double childValue = children[child].getTotalValue();
            if (childValue > bestValue) {
                bestValue = childValue;
                selected = child;
            }
        }
        if (selected == -1) return Action.NOOP;
        return children[selected].getMoveToThisState();
    }

    public void updateValues(double value) {
        // All nodes are ours so lets go for it
        PathMCTSNode current = this;
        while (current.parent != null) {
            current.totalValue += value;
            current.numberOfVisits++;
            current = current.parent;
        }
        current.totalValue += value;
        current.numberOfVisits++;
        current.pathScores.add(value);
    }

    private PathMCTSNode expand(CoopGame state) {
        int bestAction = 0;
        double bestValue = -Double.MAX_VALUE;
        if (children == null) children = new PathMCTSNode[childLength];
        Random random = mcts.random;
        for (int i = 0; i < children.length; i++) {
            double x = random.nextDouble();
            if (x > bestValue && children[i] == null) {
                bestAction = i;
                bestValue = x;
            }
        }

        Action[] allActions = state.getLegalActions(0);
        children[bestAction] = new PathMCTSNode(this, allActions[bestAction], state.getActionLength());
        childrenExpandedSoFar++;
        return children[bestAction];
    }

    public double rollout(CoopGame state) {
        int rolloutDepth = this.currentDepth;
        while (!state.hasWon() && rolloutDepth < mcts.getMaxRolloutDepth()) {
            state.update(Action.getRandom(0, state), Action.getRandom(0, state));
            rolloutDepth++;
        }
        return state.getScore();
    }

    private boolean isFullyExpanded() {
        return childrenExpandedSoFar == childLength;
    }

    public double getExplorationConstant() {
        return explorationConstant;
    }

    public Action getMoveToThisState() {
        return moveToThisState;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public int getNumberOfVisits() {
        return numberOfVisits;
    }

    public int getCurrentDepth() {
        return currentDepth;
    }

    public int getChildrenExpandedSoFar() {
        return childrenExpandedSoFar;
    }

    public PathMCTSNode getParent() {
        return parent;
    }

    public List<List<Point>> getPaths() {
        return paths;
    }

    public List<Double> getPathScores() {
        return pathScores;
    }

    public PathMCTSNode[] getChildren() {
        return children;
    }

    public int getChildLength() {
        return childLength;
    }

    public double calculateChild() {
        return totalValue / (numberOfVisits + EPSILON) +
                Math.sqrt(2 * Math.log(parent.numberOfVisits + 1) / (numberOfVisits + EPSILON)) +
                mcts.random.nextDouble() * EPSILON;
    }
}
