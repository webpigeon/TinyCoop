package FastGame;

import java.awt.Color;

/**
 * Created by pwillic on 25/06/2015.
 */
public interface ObjectTypes {
	int NO_OBJECT = 0;
	int AGENT = 1;
	int BUTTON = 2;
	int DOOR = 3;
	int GOAL = 4;

	int NUMBER_OF_LAYERS = 5;

	Color[] OBJECT_COLOURS = { Color.WHITE, Color.YELLOW.darker(), Color.RED, Color.BLUE, Color.YELLOW };
	Color[] TEXT_COLOURS = { Color.BLACK, Color.RED, Color.YELLOW, Color.GREEN, Color.RED };
}
