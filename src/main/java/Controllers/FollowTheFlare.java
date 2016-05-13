package Controllers;

import java.awt.Point;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.Flare;
import uk.me.webpigeon.phd.tinycoop.api.GameState;
import uk.me.webpigeon.phd.tinycoop.api.ObservableGameState;

public class FollowTheFlare extends PiersController {

	private int myID;

	@Override
	public Action get(GameState game) {
		ObservableGameState obs = (ObservableGameState) game;

		Flare flare = obs.getFlare(myID == GameState.PLAYER_0 ? GameState.PLAYER_1 : GameState.PLAYER_0);
		if (flare == null) {
			return Action.NOOP;
		}

		// this shouldn't happen
		if (flare.pid != myID) {
			System.err.println("error, target pid doesn't match mine - did someone build a 3 player version?!");
			return Action.NOOP;
		}

		Point currPos = obs.getPos(myID);
		Point targetPos = flare.toAbs(obs.getPos(flare.pid));

		for (Action action : obs.getLegalActions(myID)) {

			if (action.isMovement()) {
				Point movementPos = new Point(action.getX(), action.getY());
				if (action.isRelative()) {
					movementPos.x += currPos.x;
					movementPos.y += currPos.y;
				}

				if (targetPos.equals(movementPos)) {
					return action;
				}
			}
		}

		// strange, no action matched
		System.err.println("flare failed to find matching action.");
		return super.get(game);
	}

	@Override
	public String getSimpleName() {
		return "FollowTheFlare";
	}

	@Override
	public void startGame(int agentID) {
		super.startGame(agentID);
		this.myID = agentID;
	}

}
