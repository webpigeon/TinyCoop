package gamesrc.viewer;

import javax.swing.*;

import api.Flare;
import api.GameObject;
import api.GameState;
import api.ObservableGameState;
import gamesrc.SimpleGame;

import java.awt.*;

import static FastGame.GroundTypes.GROUND;
import static FastGame.GroundTypes.WALL;
import static FastGame.ObjectTypes.OBJECT_COLOURS;
import static FastGame.ObjectTypes.TEXT_COLOURS;

/**
 * Created by pwillic on 25/06/2015.
 */
public class Viewer extends JComponent {

    private SimpleGame game;
    public final static Integer GRID_SIZE = 50;

    public Viewer(SimpleGame game) {
        this.game = game;
    }

    public void setState(SimpleGame game) {
    	this.game = game;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (game == null) {
        	return;
        }
        
        for (int x = 0; x < game.getWidth(); x++) {
            for (int y = 0; y < game.getHeight(); y++) {
                int groundValue = game.getFloor(x, y);

                switch (groundValue) {
                    case GROUND:
                        g.setColor(Color.LIGHT_GRAY);
                        break;
                    case WALL:
                        g.setColor(Color.BLACK);
                        break;
                }
                g.fillRect(x * GRID_SIZE, y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
                                
                GameObject object = game.getObject(x, y);
                if (object != null) {
                	switch (object.getType()) {
                		case 3:
                			paintDoor(g, game, object, x, y);
                			break;
                		case 2:
                		case 4:
                			paintObject(g, game, object, x, y);
                			break;
                		default:
                			System.err.println("something blew up!");
                	}
                }
            }
        }
        
        // Draw the grid
        g.setColor(Color.BLACK);
        for (int x = 0; x < game.getWidth(); x++) {
            for (int y = 0; y < game.getHeight(); y++) {
                g.drawRect(x * GRID_SIZE, y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
            }
        }
        
        for (int i=0; i<2; i++) {
        	Point p = game.getPos(i);
        	g.setColor(Color.WHITE);
        	g.fillOval(p.x * GRID_SIZE, p.y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
        	g.setColor(Color.BLACK);
        	g.drawString("" + i, p.x * GRID_SIZE + GRID_SIZE / 2, (p.y * GRID_SIZE) + GRID_SIZE / 2);
        }
        
        paintDebugInfo(g);
    }
    
	private void paintObject(Graphics g, ObservableGameState state, GameObject o, int x, int y) {
		g.setColor(OBJECT_COLOURS[o.getType()]);
        g.fillRect(x * GRID_SIZE, y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
    	
    	g.setColor(TEXT_COLOURS[o.getType()]);
        g.drawString("" + o.getSignal(), x * GRID_SIZE + GRID_SIZE / 2, (y * GRID_SIZE) + GRID_SIZE / 2);
	}
    
    private void paintDoor(Graphics g, ObservableGameState state, GameObject o, int x, int y) {
    	g.setColor(OBJECT_COLOURS[o.getType()]);
        if (game.isSignalHigh(o.getSignal())) {
        	g.drawRect(x * GRID_SIZE, y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
        } else{
        	g.fillRect(x * GRID_SIZE, y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
        }
        	
        g.setColor(TEXT_COLOURS[o.getType()]);
        g.drawString("" + o.getSignal(), x * GRID_SIZE + GRID_SIZE / 2, (y * GRID_SIZE) + GRID_SIZE / 2);
    }
    
    protected void paintDebugInfo(Graphics g) {
        for (int i=0; i<10; i++) {
        	g.setColor(Color.CYAN);
        	if (game.isSignalHigh(i)) {
        		g.fillRect(i*10, 0, 10, 10);
        	} else {
        		g.drawRect(i*10, 0, 10, 10);
        	}
        }
    	
        for (int goalID=0; goalID<game.getGoalsCount(); goalID++) {
        	for (int agent=0; agent<2; agent++) {
	        	g.setColor(Color.YELLOW);
	        	if (game.hasVisited(agent, goalID)) {
	        		g.fillRect(goalID*10, 10 + agent*10, 10, 10);
	        	} else {
	        		g.drawRect(goalID*10, 10 + agent*10, 10, 10);
	        	}
        	}
        }
        
        for (int agent=0; agent<2; agent++) {
        	g.setColor(Color.RED);
        	if (game.getFlare(agent) != null) {
        		Flare f = game.getFlare(agent);
        		
            	if (f != null) {
            		Point p = f.toAbs(game.getPos(f.pid));
            		g.setColor(Color.WHITE);
	            	g.drawRect(p.x * GRID_SIZE, p.y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
            	}
        		
        		g.fillRect(agent*10, 30, 10, 10);
        	} else {
            	g.setColor(Color.RED);
        		g.drawRect(agent*10, 30, 10, 10);
        	}
        	
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(game.getWidth() * GRID_SIZE, game.getHeight() * GRID_SIZE);
    }
}
