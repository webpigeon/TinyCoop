package Controllers;

import FastGame.Action;
import FastGame.CoopGame;
import gamesrc.GameState;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by pwillic on 23/06/2015.
 */
public class AwareMCTS extends Controller {

    protected Random random = new Random();
    private int maxUCTDepth = 5;
    private int maxRolloutDepth = 30;
    private int iterationLimit = 0;
    private AwareMCTSNode root;

    private boolean first;

    public AwareMCTS(boolean first, int iterationLimit, int maxUCTDepth, int maxRolloutDepth) {
        this.first = first;
        this.iterationLimit = iterationLimit;
        this.maxUCTDepth = maxUCTDepth;
        this.maxRolloutDepth = maxRolloutDepth;
    }

    public AwareMCTS(boolean first, int iterationLimit){
        this.first = first;
        this.iterationLimit = iterationLimit;
    }

    @Override
    public Action get(GameState game) {
        CoopGame actualState = (CoopGame) game;
        root = new AwareMCTSNode(2.0, this, game.getActionLength());
        AwareMCTSNode travel;
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
        return "AwareMCTS: (" + iterationLimit + ";" + maxUCTDepth + ";" + maxRolloutDepth + ")";
    }

    @Override
    public void paint(Graphics g, Point pos, int gridSize) {
        Map<Point, Integer> visits = calculateVisits(root, new HashMap<>());
        Map<Point, Double> scores = calculateScores(root, new HashMap<>());

        Map<Point, Double> averages = new HashMap<>();
        for(Map.Entry<Point, Double> entry : scores.entrySet()){
            averages.put(entry.getKey(), entry.getValue() / visits.getOrDefault(entry.getKey(), 1));
        }

        int radius = gridSize / 3;
        FontMetrics metrics = g.getFontMetrics();
        // Draw these
        for(Map.Entry<Point, Double> entry : averages.entrySet()){
            Point location = entry.getKey();
            if(location == null) continue;
            int x = (int)((location.getX() * gridSize) + (gridSize / 2));
            int y = (int)((location.getY() * gridSize) + (gridSize / 2));
            g.setColor(Color.CYAN);
            String visitString = String.format("%.3f", entry.getValue());
//            int radius = (int)( (entry.getValue() / max) * maxRadius);
            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            g.setColor(Color.BLACK);
            int width = metrics.stringWidth(visitString);
            int height = metrics.getHeight();
            g.drawString(visitString, x - (width / 2), y + (height / 2));
        }
//        for(Map.Entry<Point, Integer> entry : visits.entrySet()){
//            Point location = entry.getKey();
//            int x = (int)((location.getX() * gridSize) + (gridSize / 2));
//            int y = (int)((location.getY() * gridSize) + (gridSize / 2));
//            g.setColor(Color.CYAN);
//            String visitString = String.format("%d", entry.getValue());
////            int radius = (int)( (entry.getValue() / max) * maxRadius);
//            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
//            g.setColor(Color.BLACK);
//            int width = metrics.stringWidth(visitString);
//            int height = metrics.getHeight();
//            g.drawString(visitString, x - (width / 2), y + (height / 2));
//        }
    }

    public Map<Point, Integer> calculateVisits(AwareMCTSNode node, Map<Point, Integer> visits){
        for(Map.Entry<Point, Integer> entry : node.getLocationVisits().entrySet()){
            visits.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        if(node.getChildren() == null) return visits;
        for(AwareMCTSNode child : node.getChildren()){
            if(child == null) continue;
            calculateVisits(child, visits);
        }
        return visits;
    }

    public Map<Point, Double> calculateScores(AwareMCTSNode node, Map<Point, Double> scores){
        for(Map.Entry<Point, Double> entry : node.getLocationScores().entrySet()){
            scores.merge(entry.getKey(), entry.getValue(), Double::sum);
        }
        if(node.getChildren() == null) return scores;
        for(AwareMCTSNode child : node.getChildren()){
            if(child == null) continue;
            calculateScores(child, scores);
        }
        return scores;
    }
}

class AwareMCTSNode {

    private static final double EPSILON = 1e-6;

    private double explorationConstant;
    private Action moveToThisState;

    private double totalValue;
    private int numberOfVisits;
    private int currentDepth;
    private int childrenExpandedSoFar = 0;

    private AwareMCTSNode parent;
    private AwareMCTSNode[] children;
    private int childLength;

    private Map<Point, Integer> locationVisits = new HashMap<>();
    private Map<Point, Double> locationScores = new HashMap<>();
    private Point lastLocation;
    private AwareMCTS mcts;

    // Root
    public AwareMCTSNode(double explorationConstant, AwareMCTS mcts, int actionLength) {
        this.explorationConstant = explorationConstant;
        this.currentDepth = 0;
        this.mcts = mcts;
        this.childLength = actionLength;
    }

    // Child
    public AwareMCTSNode(AwareMCTSNode parent, Action moveToThisState, int actionLength) {
        this.parent = parent;
        this.explorationConstant = parent.explorationConstant;
        this.moveToThisState = moveToThisState;
        this.currentDepth = parent.currentDepth + 1;
        this.mcts = parent.mcts;
        this.childLength = actionLength;
    }

    public void addLocationVisit(Point location){
        locationVisits.merge(location, 1,Integer::sum);
    }

    public Map<Point, Integer> getLocationVisits() {
        return locationVisits;
    }

    public Map<Point, Double> getLocationScores() {
        return locationScores;
    }

    protected AwareMCTSNode select(CoopGame state) {
        AwareMCTSNode current = this;
        while (current.currentDepth < mcts.getMaxUCTDepth() && !state.hasWon()) {
            if (current.isFullyExpanded()) {
                current = current.selectBestChild();
                if (mcts.isFirst()) {
                    state.update(current.getMoveToThisState(), Action.getRandom(1, state));
                } else {
                    state.update(Action.getRandom(0, state), current.getMoveToThisState());
                }
                Point location = state.getPos(mcts.isFirst() ? 0 : 1);
                current.addLocationVisit(location);
                current.lastLocation = location;
            } else {
                /// Expand
                AwareMCTSNode expandedChild = current.expand(state);
                if (mcts.isFirst()) {
                    state.update(expandedChild.getMoveToThisState(), Action.getRandom(1, state));
                } else {
                    state.update(Action.getRandom(0, state), expandedChild.getMoveToThisState());
                }
                Point location = state.getPos(mcts.isFirst() ? 0 : 1);
                current.addLocationVisit(location);
                current.lastLocation = location;
                return expandedChild;
            }
        }
        return current;
    }

    protected AwareMCTSNode selectBestChild() {
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
        AwareMCTSNode current = this;
        while (current.parent != null) {
            current.totalValue += value;
            current.numberOfVisits++;
            current.locationScores.merge(current.lastLocation, value, Double::sum);
            current = current.parent;
        }
        current.totalValue += value;
        current.numberOfVisits++;
    }

    private AwareMCTSNode expand(CoopGame state) {
        int bestAction = 0;
        double bestValue = -Double.MAX_VALUE;
        if (children == null) children = new AwareMCTSNode[childLength];
        Random random = mcts.random;
        for (int i = 0; i < children.length; i++) {
            double x = random.nextDouble();
            if (x > bestValue && children[i] == null) {
                bestAction = i;
                bestValue = x;
            }
        }
 
        Action[] allActions = state.getLegalActions(0);
        children[bestAction] = new AwareMCTSNode(this, allActions[bestAction], state.getActionLength());
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

    public AwareMCTSNode getParent() {
        return parent;
    }

    public AwareMCTSNode[] getChildren() {
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
