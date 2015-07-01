package Controllers.VariGA;

import Controllers.Controller;
import FastGame.Action;

import java.util.Random;

/**
 * Created by Piers on 01/07/2015.
 */
public class VariGA extends Controller {


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
    private void mutate(double numChance, double lengthChance, double actionChance) {
        double test = random.nextDouble();
        if (test > numChance) {
            if(numberOfActions == maxLength){
                // shrink
            }else if (numberOfActions == minLength){
                // grow
            }else{
                // grow or shrink
            }
        }
    }



}
