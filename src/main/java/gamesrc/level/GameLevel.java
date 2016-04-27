package gamesrc.level;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import api.Action;
import api.GameObject;
import gamesrc.Filters;
import gamesrc.SimpleGame;


public class GameLevel {
	private final String name;
	private final Integer width;
	private final Integer height;
	private final Point[] spawnLocations;
	private final int[] floors;
	private final AbstractGameObject[] objects;
	private Integer goalCount = 0;
	
	private String actionSetName;
	private List<Action> legalMoves;
	
	public GameLevel(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.spawnLocations = new Point[2];
		this.floors = new int[width*height];
		this.objects = new AbstractGameObject[width*height];
		this.goalCount = 0;
		
		this.legalMoves = Filters.getAllActions(width, height);
		this.actionSetName = "fullActions";
	}
	
	public List<Action> getLegalMoves() {
		return Collections.unmodifiableList(legalMoves);
	}
	
	public void setLegalMoves(String name, List<Action> legalMoves) {
		this.actionSetName = name;
		this.legalMoves = legalMoves;
	}

	public boolean isWalkable(int pid, Point pos, SimpleGame state) {
		if (pos.getX() < 0 || pos.getY() < 0 || pos.getX() >= width || pos.getY() >= height) {
			return false;
		}
		
		assert pos.getX() >= 0 && pos.getY() >= 0 : "player "+pos+" position is negative!";
		assert pos.getX() < width && pos.getY() < height : "player "+pos+" position is too high!";
		
		GameObject object = getObject(pos.x, pos.y);
		
		return floors[pos.x * height + pos.y] == 0 && (object == null || object.isWalkable(state, pid));
	}

	public int getGoalCount() {
		return goalCount;
	}

	/**
	 * Get the number of players in this level
	 * 
	 * @return the numbers of players which have spawn locations in this level
	 */
	public int getPlayerCount() {
		return spawnLocations.length;
	}

	/**
	 * Marks a position as a starting point for the player
	 * 
	 * @param point the player's starting position
	 * @param playerId the player's ID
	 */
	public void setSpawnPoint(Point point, int playerId) {
		spawnLocations[playerId] = point;		
	}


	/**
	 * A goal must be visited by agents in order to increase their score.
	 * 
	 * @param point the x,y position for the goal
	 */
	public void setGoal(Point point) {
		setObject(point.x, point.y, new Goal(goalCount++));
	}
	
	/**
	 * Set the object data.
	 * 
	 * @param x x position of the object
	 * @param y y position of the object
	 * @param object the object to store
	 */
	protected void setObject(int x, int y, AbstractGameObject object){
		assert objects[x * height + y] == null : "object already set!";
		objects[x * height + y] = object;
	}
	
	public AbstractGameObject getObject(int x, int y){
		return objects[x * height + y];
	}

	public void onStep(SimpleGame state, int pid, Point oldPos, Point newPos) {
		AbstractGameObject oldObject = getObject(oldPos.x, oldPos.y);
		AbstractGameObject newObject = getObject(newPos.x, newPos.y);
		
		if (oldObject != null) oldObject.onContactEnd(state, pid);
		if (newObject != null) newObject.onContact(state, pid);
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public int getFloor(int x, int y) {
		return floors[x * height + y];
	}

	protected void setFloor(int x, int y, int floorId) {
		floors[x * height + y] = floorId;
	}

	public Point getSpawnLocation(int i) {
		return spawnLocations[i];
	}
	
	@Override
	public String toString(){
		return name;
	}

	public String getLevelName() {
		return name;
	}

	public String getActionSetName() {
		return actionSetName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionSetName == null) ? 0 : actionSetName.hashCode());
		result = prime * result + Arrays.hashCode(floors);
		result = prime * result + ((goalCount == null) ? 0 : goalCount.hashCode());
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((legalMoves == null) ? 0 : legalMoves.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(objects);
		result = prime * result + Arrays.hashCode(spawnLocations);
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (this != obj) 
			throw new RuntimeException("flyweight cloned: tripped debug exception!");
		
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameLevel other = (GameLevel) obj;
		if (actionSetName == null) {
			if (other.actionSetName != null)
				return false;
		} else if (!actionSetName.equals(other.actionSetName))
			return false;
		if (!Arrays.equals(floors, other.floors))
			return false;
		if (goalCount == null) {
			if (other.goalCount != null)
				return false;
		} else if (!goalCount.equals(other.goalCount))
			return false;
		if (height == null) {
			if (other.height != null)
				return false;
		} else if (!height.equals(other.height))
			return false;
		if (legalMoves == null) {
			if (other.legalMoves != null)
				return false;
		} else if (!legalMoves.equals(other.legalMoves))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Arrays.equals(objects, other.objects))
			return false;
		if (!Arrays.equals(spawnLocations, other.spawnLocations))
			return false;
		if (width == null) {
			if (other.width != null)
				return false;
		} else if (!width.equals(other.width))
			return false;
		return true;
	}
	
	
	
}
