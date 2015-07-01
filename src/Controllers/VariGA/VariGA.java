package Controllers.VariGA;

import Controllers.Controller;
import FastGame.Action;
import FastGame.CoopGame;

import java.util.Random;

/**
 *
 * Do a 1 + 1 ES
 * Created by Piers on 01/07/2015.
 */
public class VariGA extends Controller {

    int iterations;

    public VariGA(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public Action get(CoopGame game) {
        ActionSequence parent = new ActionSequence(3, 10, 1, 10);
//        ActionSequence child = parent.mutate();
        for(int i = 0; i < iterations; i++){
            CoopGame workingGame = game.getClone();

        }

    }
}

class ActionSequence {
    private static Random random = new Random();
    int numberOfActions;
    // length of each action
    int[] lengths;
    // Indicies of Action.allActions
    int[] actions;

    int minLength;
    int maxLength;
    int minNum;
    int maxNum;

    // New random sequence
    public ActionSequence(int minNum, int maxNum, int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.minNum = minNum;
        this.maxNum = maxNum;

        randomize();
    }

    private void randomize() {
        numberOfActions = random.nextInt(maxNum - minNum) + minNum;
        lengths = new int[numberOfActions];
        actions = new int[numberOfActions];

        for (int i = 0; i < numberOfActions; i++) {
            lengths[i] = random.nextInt(maxLength - minLength) + minLength;
            actions = new int[random.nextInt(Action.allActions.length)];
        }
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
        if (test > numChance) {
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

        for(int i = 0; i < numberOfActions; i++){
            if(random.nextDouble() > lengthChance){
                // mutate it
                if(lengths[i] == maxLength) {
                    lengths[i]--;
                }else if(lengths[i] == minLength){
                    lengths[i]++;
                }else{
                    lengths[i] += (random.nextBoolean())? 1 : -1;
                }
            }
            if(random.nextDouble() > actionChance){
                actions[i] = random.nextInt(Action.allActions.length);
            }
        }
    }

    // increase size by 1
    private void growSequenceLength() {
        numberOfActions++;
        int[] newLengths = new int[numberOfActions];
        System.arraycopy(lengths, 0, newLengths, 0, lengths.length);
        lengths = newLengths;
    }

    private void shrinkSequenceLength() {
        numberOfActions--;
        int[] newLengths = new int[numberOfActions];
        System.arraycopy(lengths, 0, newLengths, 0, newLengths.length);
        lengths = newLengths;
    }


}
