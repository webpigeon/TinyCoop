package gamesrc;

import java.awt.Point;

public class GameLevel {
	private int width;
	private int height;
	private Point[] spawnLocations;
	private int[] floors;
	private GameObject[] objects;
	private int goalCount = 0;
	
	public GameLevel(int width, int height) {
		this.width = width;
		this.height = height;
		this.spawnLocations = new Point[2];
		this.floors = new int[width*height];
		this.objects = new GameObject[width*height];
		this.goalCount = 0;
	}

	public boolean isWalkable(int pid, Point pos, SimpleGame state) {
		if (pos.getX() < 0 || pos.getY() < 0 || pos.getX() >= width || pos.getY() >= height) {
			return false;
		}
		
		assert pos.getX() >= 0 && pos.getY() >= 0 : "player "+pos+" position is negative!";
		assert pos.getX() < width && pos.getY() < height : "player "+pos+" position is too high!";
		
		GameObject object = state.getObject(pos.x, pos.y);
		
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
	protected void setObject(int x, int y, GameObject object){
		assert objects[x * height + y] == null : "object already set!";
		objects[x * height + y] = object;
	}
	
	protected GameObject getObject(int x, int y){
		return objects[x * height + y];
	}

	public void onStep(ObservableGameState state, int pid, Point oldPos, Point newPos) {
		GameObject oldObject = getObject(oldPos.x, oldPos.y);
		GameObject newObject = getObject(newPos.x, newPos.y);
		
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

	public void setFloor(int x, int y, int floorId) {
		floors[x * height + y] = floorId;
	}

	public Point getSpawnLocation(int i) {
		return spawnLocations[i];
	}

}
