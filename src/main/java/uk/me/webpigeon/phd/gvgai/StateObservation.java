package uk.me.webpigeon.phd.gvgai;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.me.webpigeon.phd.tinycoop.api.Action;
import uk.me.webpigeon.phd.tinycoop.api.GameObject;
import uk.me.webpigeon.phd.tinycoop.api.controller.GameObservation;
import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 13/11/13
 * Time: 15:37
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class StateObservation {
    /**
     * This is the model of the game, used to apply an action and
     * get to the next state. This model MUST NOT be public.
     */
    protected GameObservation model;
    protected int player;

    /**
     * Constructor for StateObservation. Requires a forward model
     *
     * @param a_model forward model of the game.
     */
    public StateObservation(GameObservation a_model) {
        model = a_model;
    }

    /**
     * Returns an exact copy of the state observation object.
     *
     * @return a copy of the state observation.
     */
    public StateObservation copy() {
        StateObservation copyObs = new StateObservation((GameObservation)model.getClone());
        return copyObs;
    }

    /**
     * Advances the state using the action passed as the move of the agent.
     * It updates all entities in the game. It modifies the object 'this' to
     * represent the next state after the action has been executed and all
     * entities have moved.
     * <p/>
     * Note: stochastic events will not be necessarily the same as in the real game.
     *
     * @param action agent action to execute in the next cycle.
     */
    public void advance(Action action)
    {
        model.update(action, null);
    }

    /**
     * Sets a new seed for the forward model's random generator (creates a new object)
     *
     * @param seed the new seed.
     */
    public void setNewSeed(int seed)
    {
        model.setNewSeed(seed);
    }

    /**
     * Returns the actions that are available in this game for
     * the avatar.
     * @return the available actions.
     */
    public List<Action> getAvailableActions()
    {
        return model.getLegalActions(player);
    }

    /**
     * Returns the actions that are available in this game for
     * the avatar. If the parameter 'includeNIL' is true, the array contains the (always available)
     * NIL action. If it is false, this is equivalent to calling getAvailableActions().
     * @param includeNIL true to include Types.ACTIONS.ACTION_NIL in the array of actions.
     * @return the available actions.
     */
    public List<Action> getAvailableActions(boolean includeNIL)
    {
        return model.getLegalActions(player);
    }


    /**
     * Returns the number of players in the game.
     */
    public int getNoPlayers() { return 2; }

    /**
     * Gets the score of the game at this observation.
     * @return score of the game.
     */
    public double getGameScore()
    {
        return model.getScore();
    }

    /**
     * Returns the game tick of this particular observation.
     * @return the game tick.
     */
    public int getGameTick()
    {
        return -1;
    }

    /**
     * Indicates if there is a game winner in the current observation.
     * Possible values are Types.WINNER.PLAYER_WINS, Types.WINNER.PLAYER_LOSES and
     * Types.WINNER.NO_WINNER.
     * @return the winner of the game.
     */
    public int getGameWinner()
    {
    	if (model.hasWon()) {
    		return Constants.PLAYER_WINS;
    	} else {
    		return Constants.NO_WINNER;
    	}
    }

    /**
     * Indicates if the game is over or if it hasn't finished yet.
     * @return true if the game is over.
     */
    public boolean isGameOver()
    {
        return model.hasWon();
    }

    /**
     * Returns the world dimensions, in pixels.
     * @return the world dimensions, in pixels.
     */
    public Dimension getWorldDimension()
    {
        return new Dimension(model.getWidth(), model.getHeight());
    }

    /**
     * Indicates how many pixels form a block in the game.
     * @return how many pixels form a block in the game.
     */
    public int getBlockSize()
    {
        return 1;
    }

    //Methods to retrieve the state of the avatar, in the game...

    /**
     * Returns the position of the avatar. If the game is finished, we cannot guarantee that
     * this position reflects the real position of the avatar (the avatar itself could be
     * destroyed). If game finished, this returns Types.NIL.
     * @return position of the avatar, or Types.NIL if game is over.
     */
    public Vector2d getAvatarPosition()
    {
    	Point location = model.getPos(player);
    	return new Vector2d(location.x, location.y);
    }

    /**
     * Returns the speed of the avatar. If the game is finished, we cannot guarantee that
     * this speed reflects the real speed of the avatar (the avatar itself could be
     * destroyed). If game finished, this returns 0.
     * @return orientation of the avatar, or 0 if game is over.
     */
    public double getAvatarSpeed()
    {
        return 1;
    }

    /**
     * Returns the orientation of the avatar. If the game is finished, we cannot guarantee that
     * this orientation reflects the real orientation of the avatar (the avatar itself could be
     * destroyed). If game finished, this returns Types.NIL.
     * @return orientation of the avatar, or Types.NIL if game is over.
     */
    public Vector2d getAvatarOrientation() {
        return new Vector2d(0,0);
    }

    /**
     * Returns the resources in the avatar's possession. As there can be resources of different
     * nature, each entry is a key-value pair where the key is the resource ID, and the value is
     * the amount of that resource type owned. It should be assumed that there might be other resources
     * available in the game, but the avatar could have none of them.
     * If the avatar has no resources, an empty HashMap is returned.
     * @return resources owned by the avatar.
     */
    public Map<Integer, Integer> getAvatarResources() {
        return Collections.emptyMap();
    }

    /**
     * Returns the avatar's last move. At the first game cycle, it returns ACTION_NIL.
     * Note that this may NOT be the same as the last action given by the agent, as it may
     * have overspent in the last game cycle.
     * @return the action that was executed in the real game in the last cycle. ACTION_NIL
     * is returned in the very first game step.
     */
    public Action getAvatarLastAction()
    {
        return null;
    }

    /**
     * Returns the avatar's type. In case it has multiple types, it returns the most specific one.
     * @return the itype of the avatar.
     */
    public int getAvatarType()
    {
        return 1;
    }

    /**
     * Returns the health points of the avatar. A value of 0 doesn't necessarily
     * mean that the avatar is dead (could be that no health points are in use in that game).
     * @return a numeric value, the amount of remaining health points.
     */
    public int getAvatarHealthPoints() { return 1; }

    /**
     * Returns the maximum amount of health points.
     * @return the maximum amount of health points the avatar ever had.
     */
    public int getAvatarMaxHealthPoints() { return 1; }

    /**
     * Returns the limit of health points this avatar can have.
     * @return the limit of health points the avatar can have.
     */
    public int getAvatarLimitHealthPoints() {return 1; }

    /**
     * returns true if the avatar is alive.
     * @return true if the avatar is alive.
     */
    public boolean isAvatarAlive() { return true; }

    /**
     * Compares if this and the received StateObservation state are equivalent.
     * DEBUG ONLY METHOD.
     * @param o Object to compare this to.
     * @return true if o has the same components as this.
     */
    @Deprecated
    public boolean equiv(Object o)
    {
        System.out.println("StateObservation.equiv() is a Deprecated Method. And it always returns False, now.");
        return false;
    }

}
