package Controllers;

import java.awt.event.KeyEvent;

/**
 * Created by pwillic on 30/06/2015.
 */
public class ArrowController extends WASDController{

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch(keyCode){
            case KeyEvent.VK_UP:
                actionID = 4;
                break;
            case KeyEvent.VK_DOWN:
                actionID = 1;
                break;
            case KeyEvent.VK_LEFT:
                actionID = 2;
                break;
            case KeyEvent.VK_RIGHT:
                actionID = 3;
                break;
        }
//        System.out.println("Action Arrows: " + actions[actionID]);
    }
}
