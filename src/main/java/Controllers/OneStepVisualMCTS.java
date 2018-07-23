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
public class OneStepVisualMCTS extends Controller {

    protected Random random = new Random();
    private int maxUCTDepth = 5;
    private int maxRolloutDepth = 30;
    private int iterationLimit = 0;
    private OneStepMCTSNode root;

    private boolean first;

    public OneStepVisualMCTS(boolean first, int iterationLimit, int maxUCTDepth, int maxRolloutDepth) {
        this.first = first;
        this.iterationLimit = iterationLimit;
        this.maxUCTDepth = maxUCTDepth;
        this.maxRolloutDepth = maxRolloutDepth;
    }

    public OneStepVisualMCTS(boolean first, int iterationLimit) {
        this.first = first;
        this.iterationLimit = iterationLimit;
    }

    @Override
    public Action get(GameState game) {
        CoopGame actualState = (CoopGame) game;
        root = new OneStepMCTSNode(2.0, this, game.getActionLength());
        OneStepMCTSNode travel;
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

        OneStepMCTSNode[] children = root.getChildren();

    }

}

class OneStepMCTSNode {

    private static final double EPSILON = 1e-6;

    private double explorationConstant;
    private Action moveToThisState;

    private double totalValue;
    private int numberOfVisits;
    private int currentDepth;
    private int childrenExpandedSoFar = 0;

    private OneStepMCTSNode parent;
    private OneStepMCTSNode[] children;
    private int childLength;

    private OneStepVisualMCTS mcts;

    // Root
    public OneStepMCTSNode(double explorationConstant, OneStepVisualMCTS mcts, int actionLength) {
        this.explorationConstant = explorationConstant;
        this.currentDepth = 0;
        this.mcts = mcts;
        this.childLength = actionLength;
    }

    // Child
    public OneStepMCTSNode(OneStepMCTSNode parent, Action moveToThisState, int actionLength) {
        this.parent = parent;
        this.explorationConstant = parent.explorationConstant;
        this.moveToThisState = moveToThisState;
        this.currentDepth = parent.currentDepth + 1;
        this.mcts = parent.mcts;
        this.childLength = actionLength;
    }

    protected OneStepMCTSNode select(CoopGame state) {
        OneStepMCTSNode current = this;
        while (current.currentDepth < mcts.getMaxUCTDepth() && !state.hasWon()) {
            if (current.isFullyExpanded()) {
                current = current.selectBestChild();
                if (mcts.isFirst()) {
                    state.update(current.getMoveToThisState(), Action.getRandom(1, state));
                } else {
                    state.update(Action.getRandom(0, state), current.getMoveToThisState());
                }
            } else {
                /// Expand
                OneStepMCTSNode expandedChild = current.expand(state);
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

    protected OneStepMCTSNode selectBestChild() {
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
        OneStepMCTSNode current = this;
        while (current.parent != null) {
            current.totalValue += value;
            current.numberOfVisits++;
            current = current.parent;
        }
        current.totalValue += value;
        current.numberOfVisits++;
    }

    private OneStepMCTSNode expand(CoopGame state) {
        int bestAction = 0;
        double bestValue = -Double.MAX_VALUE;
        if (children == null) children = new OneStepMCTSNode[childLength];
        Random random = mcts.random;
        for (int i = 0; i < children.length; i++) {
            double x = random.nextDouble();
            if (x > bestValue && children[i] == null) {
                bestAction = i;
                bestValue = x;
            }
        }

        Action[] allActions = state.getLegalActions(0);
        children[bestAction] = new OneStepMCTSNode(this, allActions[bestAction], state.getActionLength());
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

    public OneStepMCTSNode getParent() {
        return parent;
    }

    public OneStepMCTSNode[] getChildren() {
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
