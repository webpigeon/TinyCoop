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
public class MCTS extends Controller {

    protected Random random = new Random();
    private int maxUCTDepth = 5;
    private int maxRolloutDepth = 30;
    private int iterationLimit = 0;

    private boolean first;
    private MCTSNode root;

    public MCTS(boolean first, int iterationLimit, int maxUCTDepth, int maxRolloutDepth) {
        this.first = first;
        this.iterationLimit = iterationLimit;
        this.maxUCTDepth = maxUCTDepth;
        this.maxRolloutDepth = maxRolloutDepth;
    }

    public MCTS(boolean first, int iterationLimit){
        this.first = first;
        this.iterationLimit = iterationLimit;
    }

    @Override
    public Action get(GameState game) {
        root = new MCTSNode(2.0, this, game.getLegalActions(first ? 0 : 1).length);
        MCTSNode travel;
        GameState workingGame;
        int iterations = 0;
        while (iterations < iterationLimit) {
            workingGame = game.getClone();
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
        return "MCTS: (" + iterationLimit + ";" + maxUCTDepth + ";" + maxRolloutDepth + ")";
    }

    @Override
    public void paint(Graphics g, Point pos, int gridSize) {
        Map<Point, Integer> visits = calculateVisits(root, pos, new HashMap<>());
        Map<Point, Double> scores = calculateScores(root, pos, new HashMap<>());
        Map<Point, Double> averages = new HashMap<>();
        for(Map.Entry<Point, Double> entry : scores.entrySet()){
            averages.put(entry.getKey(), entry.getValue() / visits.getOrDefault(entry.getKey(), 1));
        }

        int radius = gridSize / 3;
        FontMetrics metrics = g.getFontMetrics();
        // Draw these
        for(Map.Entry<Point, Double> entry : averages.entrySet()){
            Point location = entry.getKey();
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
    }

    public Map<Point, Integer> calculateVisits(MCTSNode node, Point pos, Map<Point, Integer> visits){
        visits.put(pos, visits.getOrDefault(pos, 0) + node.getNumberOfVisits());
        if(node.getChildren() == null) return visits;
        for(MCTSNode child : node.getChildren()){
            if(child == null) continue;
            Point childPos = new Point(pos.x + child.getMoveToThisState().getX(), pos.y + child.getMoveToThisState().getY());
            calculateVisits(child, childPos, visits);
        }
        return visits;
    }

    public Map<Point, Double> calculateScores(MCTSNode node, Point pos, Map<Point, Double> scores){
        scores.put(pos, scores.getOrDefault(pos, 0.0) + node.getTotalValue());
        if(node.getChildren() == null) return scores;
        for(MCTSNode child : node.getChildren()){
            if(child == null) continue;
            Point childPos = new Point(pos.x + child.getMoveToThisState().getX(), pos.y + child.getMoveToThisState().getY());
            calculateScores(child, childPos, scores);
        }
        return scores;
    }
}

class MCTSNode {

    private static final double EPSILON = 1e-6;

    private double explorationConstant;
    private Action moveToThisState;

    private double totalValue;
    private int numberOfVisits;
    private int currentDepth;
    private int childrenExpandedSoFar = 0;

    private MCTSNode parent;
    private MCTSNode[] children;
    private int childLength;

    private MCTS mcts;

    // Root
    public MCTSNode(double explorationConstant, MCTS mcts, int actionLength) {
        this.explorationConstant = explorationConstant;
        this.currentDepth = 0;
        this.mcts = mcts;
        this.childLength = actionLength;
    }

    // Child
    public MCTSNode(MCTSNode parent, Action moveToThisState, int actionLength) {
        this.parent = parent;
        this.explorationConstant = parent.explorationConstant;
        this.moveToThisState = moveToThisState;
        this.currentDepth = parent.currentDepth + 1;
        this.mcts = parent.mcts;
        this.childLength = actionLength;
    }

    protected MCTSNode select(GameState state) {
        MCTSNode current = this;
        while (current.currentDepth < mcts.getMaxUCTDepth() && !state.hasWon()) {
            MCTSNode expandedChild = current.expand(state);
            if(expandedChild == current){
//            if (current.isFullyExpanded(state)) {
                current = current.selectBestChild();
                if (mcts.isFirst()) {
                    state.update(current.getMoveToThisState(), Action.getRandom(1, state));
                } else {
                    state.update(Action.getRandom(0, state), current.getMoveToThisState());
                }
            } else {
                /// Expanded so will do this instead
                if (mcts.isFirst()) {
                    state.update(expandedChild.getMoveToThisState(), Action.getRandom(1, state));
                } else {
                    state.update(Action.getRandom(0, state), expandedChild.getMoveToThisState());
                }
                return expandedChild;
            }
        }
        return current;
    }

    protected MCTSNode selectBestChild() {
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;
        for (int child = 0; child < children.length; child++) {
            if(children[child] == null) continue;
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
        MCTSNode current = this;
        while (current.parent != null) {
            current.totalValue += value;
            current.numberOfVisits++;
            current = current.parent;
        }
        current.totalValue += value;
        current.numberOfVisits++;
    }

    private MCTSNode expand(GameState state) {
        int bestAction = -1;
        double bestValue = -Double.MAX_VALUE;
        if (children == null) children = new MCTSNode[childLength];
        Action[] allActions = getAllActions(state);
        Random random = mcts.random;
        for (int i = 0; i < children.length; i++) {
            if(allActions[i] == null) continue; // Wasn't possible this time
            double x = random.nextDouble();
            if (x > bestValue && children[i] == null) {
                bestAction = i;
                bestValue = x;
            }
        }
        if(bestAction == -1) return this;

        children[bestAction] = new MCTSNode(this, allActions[bestAction], state.getActionLength());
        childrenExpandedSoFar++;
        return children[bestAction];
    }

    public double rollout(GameState state) {
        int rolloutDepth = this.currentDepth;
        while (!state.hasWon() && rolloutDepth < mcts.getMaxRolloutDepth()) {
            state.update(Action.getRandom(0, state), Action.getRandom(0, state));
            rolloutDepth++;
        }
        return state.getScore();
    }

    private Action[] getAllActions(GameState state){
        return state.getLegalActions(mcts.isFirst() ? 0 : 1);
    }

    private boolean isFullyExpanded() {
        return childrenExpandedSoFar == childLength;
    }

    private boolean isFullyExpanded(GameState state){
        return childrenExpandedSoFar >= getAllActions(state).length;
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

    public MCTSNode getParent() {
        return parent;
    }

    public MCTSNode[] getChildren() {
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
