package gamesrc;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class LevelParser {
	
	public static GameLevel buildParser(String filename) throws IOException {
		Scanner scanner = new Scanner(new FileInputStream(filename));
		
		int width = scanner.nextInt();
		int height = scanner.nextInt();
		
		GameLevel level = new GameLevel(width, height);
		
		for (int row=0; row<height; row++) {
			for (int col=0; col<width; col++) {
				int val = scanner.nextInt();
				level.setFloor(col, row, val);
			}
		}
		
		boolean isObjects = false;
		scanner.nextLine();
		String objectStr = scanner.nextLine();
		if ("BEGIN OBJECTS".equals(objectStr)) isObjects = true;
		
		while(isObjects) {
			objectStr = scanner.nextLine();

			if ("END OBJECTS".equals(objectStr)) {
				isObjects = true;
				break;
			}
			
			Scanner lineScanner = new Scanner(objectStr);
			parseObject(level, lineScanner.nextInt(), lineScanner.nextInt(), lineScanner.nextInt(), lineScanner.nextInt());
			lineScanner.close();
		}
		scanner.close();
		
		return level;
	}
	
	public static void parseObject(GameLevel level, int type, int x, int y, int extra) {
		
		switch(type) {
			case 1:
				level.setSpawnPoint(new Point(x,y), extra);
				break;
			case 4:
				level.setGoal(new Point(x, y));
				break;
			case 2:
				level.setObject(x, y, new Button(extra));
				break;
			case 3:
				level.setObject(x, y, new Door(extra));
				break;
			case 5:
				level.setObject(x, y, new TrapDoor(extra));
				break;
		}
		
	}

}
