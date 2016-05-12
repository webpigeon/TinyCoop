package runner.viewer;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import Controllers.PiersController;
import api.Action;
import api.GameState;
import gamesrc.Filters;
import gamesrc.actions.absolute.AbsoluteFlare;

/**
 * Created by pwillic on 23/06/2015.
 */
public class WASDController extends PiersController implements KeyListener, MouseListener {
	private Action action;
	private Point flarePos;
	private int agentID;

	@Override
	public Action get(GameState game) {
		List<Action> actions = game.getLegalActions(agentID);

		if (flarePos != null) {
			Point flarePos2 = new Point(flarePos);
			flarePos = null;
			return new AbsoluteFlare(flarePos2.x, flarePos2.y);
		}

		Action nextAction = action;
		action = Action.NOOP;
		return nextAction;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyChar()) {
		case 'a':
			action = Filters.MOVE_LEFT;
			break;
		case 's':
			action = Filters.MOVE_DOWN;
			break;
		case 'd':
			action = Filters.MOVE_RIGHT;
			break;
		case 'w':
			action = Filters.MOVE_UP;
			break;
		}
		// System.out.println("Action: " + actions[actionID]);
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		int x = arg0.getX() / Viewer.GRID_SIZE;
		int y = arg0.getY() / Viewer.GRID_SIZE;
		flarePos = new Point(x, y);
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

	@Override
	public void startGame(int agentID) {
		this.action = Action.NOOP;
		this.flarePos = null;
		this.agentID = agentID;
	}
}
