package Controllers.VariGA;

import Controllers.Controller;
import actions.Action;
import FastGame.CoopGame;
import gamesrc.GameState;

import java.util.Random;

/**
 * Do a 1 + 1 ES
 * Created by Piers on 01/07/2015.
 */
public class VariGA extends Controller {

    private boolean first;
    private int iterations;
    private double numChance = 0.25;
    private double lengthChance = 0.8;
    private double actionChance = 0.75;
    private ActionSequence currentBest;
    private int currentUsage = 0;
    private ActionSequence parent;
    private double parentFitness;
    private GameState fastForwardedGame;
    private Random random;

    private int[] params;

    public VariGA(boolean first, int iterations) {
        this.first = first;
        this.iterations = iterations;
        this.random = new Random();
    }

    public VariGA(boolean first, int iterations, double[] chances, int[] params){
        this(first, iterations);
        numChance = chances[0];
        lengthChance = chances[1];
        actionChance = chances[2];
        this.params = params;
    }

    public VariGA getClone(){
        VariGA other = new VariGA(this.first, this.iterations);
        other.params = this.params;
        other.actionChance = this.actionChance;
        other.lengthChance = this.lengthChance;
        other.numChance = this.numChance;
        return other;
    }
    // int minNum, int maxNum, int minLength, int maxLength
    private ActionSequence getNewParent(Action[] actionList){
        if(params == null)  return new ActionSequence(3, 10, 1, 5, actionList);
        return new ActionSequence(params[0], params[1], params[2], params[3], actionList);
    }

    public Action getRandom(int pid, GameState state) {
    	Action[] legalActions = state.getLegalActions(pid);
    	return legalActions[random.nextInt(legalActions.length)];
    }
    
    public Action getOppMove(int pid, GameState state) {
    	return getRandom(pid, state);
    }
    
    @Override
    public Action get(GameState game) {
        if (currentBest == null || currentUsage >= currentBest.getFirstActionLength()) {
            currentBest = parent;
            parent = null;
            // Fast forward the fast forward to the right place
            fastForwardedGame = game.getClone();
            for(int i = 0 ;!fastForwardedGame.hasWon() &&  i < currentBest.getFirstActionLength(); i++){
                if(first){
                	Action[] actionList = fastForwardedGame.getLegalActions(0);
                    fastForwardedGame.update(currentBest.getActionAt(i, actionList), getRandom(1, game));
                }else{
                	Action[] actionList = fastForwardedGame.getLegalActions(1);
                    fastForwardedGame.update(getRandom(0, game), currentBest.getActionAt(i, actionList));
                }
            }
            currentUsage = 0;
        }
        calculate(game);
//        System.out.println(currentBest);
        if (currentBest == null) return Action.NOOP;
//        System.out.println(currentBest.getActionAt(currentUsage));
        return currentBest.getActionAt(currentUsage++, game.getLegalActions(0));
    }

    private void calculate(GameState game) {
//        System.out.println("Gets here");
        if (parent == null) {
            parent = getNewParent(game.getLegalActions(0));
            parentFitness = parent.evaluate(fastForwardedGame.getClone(), first);
        }
        for (int i = 0; i < iterations; i++) {
//            System.out.println("Iteration: " + i);
            ActionSequence child = parent.getClone();
            child.mutate(numChance, lengthChance, actionChance, game.getLegalActions(0));
            double childFitness = child.evaluate(fastForwardedGame.getClone(), first);
            if (childFitness > parentFitness || (childFitness == parentFitness && Math.random() > 0.75)) {
                parent = child;
                parentFitness = childFitness;
            }
        }
    }

    @Override
    public String getSimpleName() {
        return "VariGA + (" + iterations + ";" + parent.toString() + ")";
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
    public ActionSequence(int minNum, int maxNum, int minLength, int maxLength, Action[] actionList) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.minNum = minNum;
        this.maxNum = maxNum;

        randomize(actionList);
    }

    private ActionSequence() {

    }

    @Override
    public String toString() {
        return "params{" +
                "minLength=" + minLength +
                "; maxLength=" + maxLength +
                "; minNum=" + minNum +
                "; maxNum=" + maxNum +
                '}';
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

    private void randomize(Action[] actionList) {
        numberOfActions = random.nextInt(maxNum - minNum) + minNum;
        lengths = new int[numberOfActions];
        actions = new int[numberOfActions];

        for (int i = 0; i < numberOfActions; i++) {
            lengths[i] = random.nextInt(maxLength - minLength) + minLength;
            actions[i] = random.nextInt(actionList.length);
        }
    }

    public Action getRandom(int pid, GameState state) {
    	Action[] legalActions = state.getLegalActions(pid);
    	return legalActions[random.nextInt(legalActions.length)];
    }
    
    public double evaluate(GameState state, boolean first) {
        int totalScore = 0;
        int totalLength = getTotalLength();

        
        for(int j = 0; j < 3; j++) {
            GameState game = state.getClone();
            for (int i = 0; !game.hasWon() && i < totalLength; i++) {
                if (first) {
                    Action[] actionList = state.getLegalActions(0);
                    game.update(getActionAt(i, actionList), getRandom(1, state));
                } else {
                    Action[] actionList = state.getLegalActions(1);
                    game.update(getRandom(0, state), getActionAt(i, actionList));
                }
            }
            totalScore += game.getScore();
        }
        return totalScore / 3.0;
    }

    /**
     * Mutate the item as gently as you like
     *
     * @param numChance    - Chance that the number of action pieces will be increased or decreased by 1 within the limits
     * @param lengthChance - Chance that the individual length of each parameter will be increased or decreased by 1 within the limits
     * @param actionChance - Chance that the individual action of each parameter will be randomly changed
     */
    public void mutate(double numChance, double lengthChance, double actionChance, Action[] actionList) {
        double test = random.nextDouble();
        if (test < numChance) {
            if (numberOfActions == maxLength) {
                shrinkSequenceLength();
            } else if (numberOfActions == minLength || numberOfActions == 1) {
                growSequenceLength(actionList);
            } else {
                if (random.nextBoolean()) {
                    shrinkSequenceLength();
                } else {
                    growSequenceLength(actionList);
                }
            }
        }

        for (int i = 0; i < numberOfActions; i++) {
            if (random.nextDouble() < lengthChance) {
                // mutate it
                if (lengths[i] == maxLength) {
                    lengths[i]--;
                } else if (lengths[i] == minLength || lengths[i] == 1) {
                    lengths[i]++;
                } else {
                    lengths[i] += (random.nextBoolean()) ? 1 : -1;
                }
            }
            if (random.nextDouble() < actionChance) {
            	actions[i] = random.nextInt(actionList.length);
            }
        }
    }

    private void growSequenceLength(Action[] actionsList) {
        numberOfActions++;
        int[] newLengths = new int[numberOfActions];
        int[] newActions = new int[numberOfActions];
        System.arraycopy(lengths, 0, newLengths, 0, lengths.length);
        System.arraycopy(actions, 0, newActions, 0, actions.length);
        lengths = newLengths;
        actions = newActions;
        lengths[numberOfActions - 1] = random.nextInt(maxLength - minLength) + minLength;
        actions[numberOfActions - 1] = random.nextInt(actions.length);
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

    public int getFirstActionLength(){
        return lengths[0];
    }

    public Action getActionAt(int position, Action[] actionList) {
        int runningTotal = 0;
        for (int i = 0; i < numberOfActions; i++) {
            runningTotal += lengths[i];
            if (position < runningTotal) return actionList[actions[i]];
        }
        return Action.NOOP;
    }
}
