package FastGame;

import Controllers.Controller;

import java.awt.*;

public class ControllerViewer extends Viewer{

    private final CoopGame coopGame;
    private final int gridSize = 50;
    private final Controller c1;
    private final Controller c2;

    public ControllerViewer(CoopGame game, Controller c1, Controller c2) {
        super(game);
        this.coopGame = game;
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if(c1 != null) c1.paint(g, coopGame.getPos(0), gridSize);
        if(c2 != null) c2.paint(g, coopGame.getPos(1), gridSize);
    }
}
