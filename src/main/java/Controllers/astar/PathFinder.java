package Controllers.astar;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import api.Action;
import api.ObservableGameState;

/**
 * An implementation of A* for path finding
 */
public class PathFinder {

	public static Queue<Point> getPath(ObservableGameState world, Point start, Point end, int agentID) {
    	List<Point> closedSet = new ArrayList<Point>();
    	List<Point> openSet = new ArrayList<Point>();
    	openSet.add(start);
    	Map<Point, Point> cameFrom = new HashMap<>();
    	Map<Point, Double> gScores = new HashMap<>();
    	Map<Point, Double> fScores = new HashMap<>();
    	
    	
    	while(!openSet.isEmpty()) {
    		Point current = openSet.get(0);
    		if (current.equals(end)) {
    			return buildPath(current, cameFrom);
    		}
    		openSet.remove(current);
    		closedSet.add(current);
    		
    		for (Point neighbor : getAvailableMoves(agentID, current, world)) {
    			if (closedSet.contains(neighbor)) {
    				continue;
    			}
    			Double gScore = gScores.get(current);
    			if (gScore == null) {
    				gScore = 0.0;
    			}
    			gScore += 1;
    			
    			if ( !openSet.contains(neighbor) || gScore > gScores.get(neighbor) ) {
    				cameFrom.put(neighbor, current);
    				gScores.put(neighbor, gScore);
    				fScores.put(neighbor, gScore + getDistance(neighbor, end));
    				if (!openSet.contains(neighbor)) {
    					openSet.add(neighbor);
    				}
    			}
    		}
    	}
    	
    	return new LinkedList<Point>();
	}

	public static LinkedList<Point> buildPath(Point current, Map<Point,Point> cameFrom) {
		LinkedList<Point> path = new LinkedList<>();
		path.addFirst(current);
		
		while (cameFrom.containsKey(current)) {
			current = cameFrom.get(current);
			path.addFirst(current);
		}
		
		return path;
	}
	
	public static List<Point> getAvailableMoves(int agentID, Point point, ObservableGameState world){
		List<Point> nextActions = new ArrayList<Point>();
		
		Action[] possibleActions = world.getLegalActions(agentID);
		
		for (Action a : possibleActions) {
			Point p = new Point(point);
			
			if (a.isMovement()) {
				p.x += a.getX();
				p.y += a.getY();
				if (world.isWalkable(agentID, p.x, p.y)) {
					nextActions.add(p);
				}
			}
			
		}
		
		return nextActions;
	}
	
	public static double getDistance(Point start, Point end) {
		return (start.x - end.x) + (start.y - end.y);
	}

}
