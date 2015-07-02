package Controllers.VariGA;

import Controllers.Controller;
import FastGame.Action;
import FastGame.CoopGame;

import java.util.Random;

/**
 * Do a 1 + 1 ES
 * Created by Piers on 01/07/2015.
 */
public class VariGA extends Controller {

    boolean first;
    private int iterations;
    private double numChance = 0.1;
    private double lengthChance = 0.3;
    private double actionChance = 0.1;
    private ActionSequence currentBest;
    private int currentUsage = 0;
    private ActionSequence parent  = new ActionSequence(1, 4, 1, 5);
    private double parentFitness;
    private CoopGame fastForwardedGame;

    public VariGA(boolean first, int iterations) {
        this.first = first;
        this.iterations = iterations;
    }

    @Override
    public Action get(CoopGame game) {
        if (currentBest == null || currentUsage >= currentBest.getTotalLength()) {
            currentBest = parent;
            parent = null;
            // Fast forward the fast forward to the right place
            fastForwardedGame = game.getClone();
            for(int i = 0 ;!fastForwardedGame.hasWon() &&  i < currentBest.getTotalLength(); i++){
                if(first){
                    fastForwardedGame.update(currentBest.getActionAt(i), Action.getRandom());
                }else{
                    fastForwardedGame.update(Action.getRandom(), currentBest.getActionAt(i));
                }
            }
            currentUsage = 0;
        }
        calculate(game);
//        System.out.println(currentBest);
        if (currentBest == null) return Action.NOOP;
//        System.out.println(currentBest.getActionAt(currentUsage));
        return currentBest.getActionAt(currentUsage++);
    }

    private void calculate(CoopGame game) {
//        System.out.println("Gets here");
        if (parent == null) {
            parent = new ActionSequence(3, 6, 1, 5);
            parentFitness = parent.evaluate(fastForwardedGame.getClone(), first);
        }
        for (int i = 0; i < iterations; i++) {
//            System.out.println("Iteration: " + i);
            ActionSequence child = parent.getClone();
            child.mutate(numChance, lengthChance, actionChance);
            double childFitness = child.evaluate(fastForwardedGame.getClone(), first);
            if (childFitness > parentFitness) {
                parent = child;
                parentFitness = childFitness;
            }
        }
    }
}

class ActionSequence {
    private static Random random = new Random();
    private int numberOfActions;
    // length of each action
    private int[] lengths;
    // Indicies of Action.allActions
    private int[] actions;

    private int minLength;
    private int maxLength;
    private int minNum;
    private int maxNum;

    // New random sequence
    public ActionSequence(int minNum, int maxNum, int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.minNum = minNum;
        this.maxNum = maxNum;

        randomize();
    }

    private ActionSequence() {

    }

    public ActionSequence getClone() {
        ActionSequence other = new ActionSequence();
        other.numberOfActions = this.numberOfActions;
        other.lengths = new int[numberOfActions];
        System.arraycopy(this.lengths, 0, other.lengths, 0, numberOfActions);
        other.actions = new int[numberOfActions];
        System.arraycopy(this.actions, 0, other.actions, 0, numberOfActions);
        other.minLength = this.minLength;
        other.maxLength = this.maxLength;
        other.minNum = this.minNum;
        other.maxNum = this.maxNum;

        return other;
    }

    private void randomize() {
        numberOfActions = random.nextInt(maxNum - minNum) + minNum;
        lengths = new int[numberOfActions];
        actions = new int[numberOfActions];

        for (int i = 0; i < numberOfActions; i++) {
            lengths[i] = random.nextInt(maxLength - minLength) + minLength;
            actions[i] = random.nextInt(Action.allActions.length);
        }
    }

    public double evaluate(CoopGame state, boolean first) {
        int totalLength = getTotalLength();
        for (int i = 0; !state.hasWon() && i < totalLength; i++) {
            if (first) {
                state.update(getActionAt(i), Action.getRandom());
            } else {
                state.update(Action.getRandom(), getActionAt(i));
            }
        }
        return state.getScore();
    }

    /**
     * Mutate the item as gently as you like
     *
     * @param numChance    - Chance that the number of action pieces will be increased or decreased by 1 within the limits
     * @param lengthChance - Chance that the individual length of each parameter will be increased or decreased by 1 within the limits
     * @param actionChance - Chance that the individual action of each parameter will be randomly changed
     */
    public void mutate(double numChance, double lengthChance, double actionChance) {
        double test = random.nextDouble();
        if (test < numChance) {
            if (numberOfActions == maxLength) {
                shrinkSequenceLength();
            } else if (numberOfActions == minLength) {
                growSequenceLength();
            } else {
                if (random.nextBoolean()) {
                    shrinkSequenceLength();
                } else {
                    growSequenceLength();
                }
            }
        }

        for (int i = 0; i < numberOfActions; i++) {
            if (random.nextDouble() < lengthChance) {
                // mutate it
                if (lengths[i] == maxLength) {
                    lengths[i]--;
                } else if (lengths[i] == minLength) {
                    lengths[i]++;
                } else {
                    lengths[i] += (random.nextBoolean()) ? 1 : -1;
                }
            }
            if (random.nextDouble() < actionChance) {
                actions[i] = random.nextInt(Action.allActions.length);
            }
        }
    }

    private void growSequenceLength() {
        numberOfActions++;
        int[] newLengths = new int[numberOfActions];
        int[] newActions = new int[numberOfActions];
        System.arraycopy(lengths, 0, newLengths, 0, lengths.length);
        System.arraycopy(actions, 0, newActions, 0, actions.length);
        lengths = newLengths;
        actions = newActions;
        lengths[numberOfActions - 1] = random.nextInt(maxLength - minLength) + minLength;
        actions[numberOfActions - 1] = random.nextInt(Action.allActions.length);
    }

    private void shrinkSequenceLength() {
        numberOfActions--;
        int[] newLengths = new int[numberOfActions];
        int[] newActions = new int[numberOfActions];
        System.arraycopy(lengths, 0, newLengths, 0, newLengths.length);
        System.arraycopy(actions, 0, newActions, 0, newActions.length);
        lengths = newLengths;
        actions = newActions;
    }

    public int getTotalLength() {
        int total = 0;
        for (int length : lengths) {
            total += length;
        }
        return total;
    }

    public Action getActionAt(int position) {
        int runningTotal = 0;
        for (int i = 0; i < numberOfActions; i++) {
            runningTotal += lengths[i];
            if (position < runningTotal) return Action.allActions[actions[i]];
        }
        return Action.NOOP;
    }


}
