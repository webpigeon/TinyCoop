package uk.me.webpigeon.phd.tinycoop.abstraction;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uk.me.webpigeon.phd.tinycoop.api.GameObject;
import uk.me.webpigeon.phd.tinycoop.engine.Filters;
import uk.me.webpigeon.phd.tinycoop.engine.SimpleGame;
import uk.me.webpigeon.phd.tinycoop.engine.level.GameLevel;
import uk.me.webpigeon.phd.tinycoop.engine.level.LevelParser;

public class RoomExtractor {
	private final SimpleGame game;
	private final int[][] regions;
	private int region;
	private final List<ObjectData> objects;
	private final List<RegionLink> links;

	public static void main(String[] args) throws IOException {
		GameLevel levelRel = LevelParser.buildParser("data/norm_maps/cloverleaf.txt");
		levelRel.setLegalMoves("relative", Filters.getAllRelativeActions());
		
		RoomExtractor room = new RoomExtractor(new SimpleGame(levelRel));
		room.calculateRegions();
	}
	
	public RoomExtractor(SimpleGame game) {
		this.game = game;
		this.regions = new int[game.getWidth()][game.getHeight()];
		this.objects = new ArrayList<>();
		this.links = new ArrayList<>();
		this.region = 1;
	}
	
	public void calculateRegions() {
		
		for (int x=0; x<game.getWidth(); x++) {
			for (int y=0; y<game.getHeight(); y++) {
				if (regions[x][y] == 0){
					recusiveFloodFill(new Point(x, y), 0, region++);
				}
				
				GameObject object = game.getObject(x, y);
				if (object != null) {
					ObjectData data = new ObjectData(x,y,object.getType(),object.getSignal());
					objects.add(data);
				}
			}
		}
		
		processObjects();
		printRegionLabels();
		Map<Integer, List<Point>> regions = extractRegions();
		
		//print out region data
		for (Entry<Integer, List<Point>> region : regions.entrySet()) {
			Integer type = region.getKey();
			List<Point> points = region.getValue();
			System.out.println(String.format("%d: %d cells", type, points.size()));
		}
		
		System.out.println(objects);
		System.out.println(links);
	}
	
	public Map<Integer, List<Point>> extractRegions() {
		Map<Integer, List<Point>> regionMap = new HashMap<>();
		
		for (int x=0; x<game.getWidth(); x++) {
			for (int y=0; y<game.getHeight(); y++) {
				int colour = regions[x][y];
				List<Point> region = regionMap.get(colour);
				if (region == null) {
					region = new ArrayList<Point>();
					regionMap.put(colour, region);
				}
				region.add(new Point(x, y));
			}
		}
		
		return regionMap;
	}
	
	public void recusiveFloodFill(Point start, int target, int replacement) {
		if (target == replacement) return;
		
		//check that we're not out of bounds
		if (start.x < 0 || start.y < 0 || start.x >= game.getWidth() || start.y >= game.getHeight()) {
			return;
		}
		
		//if the square is not walkable, tag it as -1
		if (!game.isWalkable(0, start.x, start.y)) {
			regions[start.x][start.y] = -1;
			return;
		}
		
		//set the colour to the target colour if the replacement is known
		int colour = regions[start.x][start.y];
		if (colour != target) {
			return;
		}
		
		if (colour == target) {
			regions[start.x][start.y] = replacement;
		}
		
		//perform recursive flood fill in all directions
		recusiveFloodFill(new Point(start.x - 1, start.y), target, replacement);
		recusiveFloodFill(new Point(start.x + 1, start.y), target, replacement);
		recusiveFloodFill(new Point(start.x, start.y + 1), target, replacement);
		recusiveFloodFill(new Point(start.x, start.y - 1), target, replacement);
	}
	
	public void processObjects() {
		
		for (ObjectData data : objects) {
			
			switch (data.type){
				//A door connects two (or more) regions
				case DOOR:
					processDoor(data.x, data.y, data.signal);
					
				//We don't care about this object type
				default:
			}
			
		}
		
	}
	
	private void processDoor(int x, int y, int signal) {
		RegionLink link = new RegionLink();
		link.signal = signal;
		link.top = regions[x][y-1];
		link.bottom = regions[x][y+1];
		link.left = regions[x-1][y];
		link.right = regions[x+1][y];
		links.add(link);
	}
	
	public void printRegionLabels(){
		for (int y = 0; y<game.getHeight(); y++) {
			for (int x = 0; x<game.getWidth(); x++) {
				System.out.print(regions[x][y]+" ");
			}
			System.out.println();
		}
	}
	
}
