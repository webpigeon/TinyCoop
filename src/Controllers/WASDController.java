package Controllers;

import FastGame.Action;
import FastGame.CoopGame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by pwillic on 23/06/2015.
 */
public class WASDController extends Controller implements KeyListener {
    Action[] actions = new Action[]{Action.NOOP, Action.DOWN, Action.LEFT, Action.RIGHT, Action.UP};

    int actionID = 0;

    @Override
    public Action get(CoopGame game) {
        int id = actionID;
        actionID = 0;
        return actions[id];
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'a':
                actionID = 2;
                break;
            case 's':
                actionID = 1;
                break;
            case 'd':
                actionID = 3;
                break;
            case 'w':
                actionID = 4;
                break;
        }
//        System.out.println("Action: " + actions[actionID]);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
