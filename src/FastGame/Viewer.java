package FastGame;

import javax.swing.*;
import java.awt.*;

import static FastGame.GroundTypes.GROUND;
import static FastGame.GroundTypes.WALL;
import static FastGame.ObjectTypes.AGENT;
import static FastGame.ObjectTypes.OBJECT_COLOURS;
import static FastGame.ObjectTypes.TEXT_COLOURS;
import static FastGame.ObjectTypes.DOOR;

/**
 * Created by pwillic on 25/06/2015.
 */
public class Viewer extends JComponent {

    CoopGame game;

    int gridSize = 50;



    public Viewer(CoopGame game) {
        this.game = game;
    }

    @Override
    public void paint(Graphics g) {
        for (int x = 0; x < game.getWidth(); x++) {
            for (int y = 0; y < game.getHeight(); y++) {
                int groundValue = game.get(x, y, 0);

                switch (groundValue) {
                    case GROUND:
                        g.setColor(Color.LIGHT_GRAY);
                        break;
                    case WALL:
                        g.setColor(Color.BLACK);
                        break;
                }
                g.fillRect(x * gridSize, y * gridSize, gridSize, gridSize);
                for (int layer = game.getLayers() - 1; layer >= 1; layer--) {
                    int value = game.get(x, y, layer);
                    if(value != 0) {
                        int itemType = CoopGame.getItemTypeFromValue(value);
                        if(itemType == DOOR && game.doorOpen(CoopGame.getIDFromValue(value))){
                            continue;
                        }

                        g.setColor(OBJECT_COLOURS[itemType]);
                        g.fillRect(x * gridSize, y * gridSize, gridSize, gridSize);

                        g.setColor(TEXT_COLOURS[itemType]);
                        g.drawString("" + CoopGame.getIDFromValue(value), x * gridSize + gridSize / 2, (y * gridSize) + gridSize / 2);
                    }
                }
            }
        }

        // Draw the grid
        g.setColor(Color.BLACK);
        for (int x = 0; x < game.getWidth(); x++) {
            for (int y = 0; y < game.getHeight(); y++) {
                g.drawRect(x * gridSize, y * gridSize, gridSize, gridSize);
            }
        }
        super.paint(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(game.getWidth() * gridSize, game.getHeight() * gridSize);
    }
}
