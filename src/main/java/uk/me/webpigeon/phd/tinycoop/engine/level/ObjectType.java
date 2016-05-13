package uk.me.webpigeon.phd.tinycoop.engine.level;

import java.awt.Color;

public enum ObjectType {
	NO_OBJECT(Color.WHITE, Color.BLACK), AGENT(Color.YELLOW, Color.RED), BUTTON(Color.RED, Color.GREEN), DOOR(Color.BLUE, Color.RED), GOAL(Color.YELLOW, Color.RED);

	private final Color colour;
	private final Color text;
	
	private ObjectType(Color colour, Color text) {
		this.colour = colour;
		this.text = text;
	}
	
	public Color getObjectColor() {
		return colour;
	}

	public Color getTextColor() {
		return text;
	} 
}
