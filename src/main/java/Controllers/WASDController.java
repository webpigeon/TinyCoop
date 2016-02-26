package Controllers;

import FastGame.Action;
import FastGame.CoopGame;
import FastGame.TalkAction;
import gamesrc.GameState;
import gamesrc.ObservableGameState;
import gamesrc.viewer.Viewer;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by pwillic on 23/06/2015.
 */
public class WASDController extends Controller implements KeyListener, MouseListener {
    private Action action;
    private Point flarePos;
    private int agentID;
    
    @Override
	public void startGame(int agentID) {
    	this.action = Action.NOOP;
    	this.flarePos = null;
    	this.agentID = agentID;
	}

	@Override
    public Action get(GameState game) {
		Action[] actions = game.getLegalActions(agentID);
		
		if (flarePos != null) {
			for (Action action : actions) {
				if (action.isTalk()) {
					return Action.NOOP;
				}
				
				Point flarePos2 = new Point(flarePos);
				flarePos = null;
				return new TalkAction(agentID, flarePos2.x, flarePos2.y);	
			}
		}
		
		Action nextAction = action;
		action = Action.NOOP;
        return nextAction;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'a':
                action = Action.LEFT;
                break;
            case 's':
                action = Action.DOWN;
                break;
            case 'd':
                action = Action.RIGHT;
                break;
            case 'w':
                action = Action.UP;
                break;
            case 'x':
                action = Action.BEEP;
                break;
        }
//        System.out.println("Action: " + actions[actionID]);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

	@Override
	public void mouseClicked(MouseEvent arg0) {
		int x = arg0.getX() / Viewer.GRID_SIZE;
		int y = arg0.getY() / Viewer.GRID_SIZE;
		flarePos = new Point(x,y);
		System.out.println(flarePos);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
