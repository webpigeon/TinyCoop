package FastGame;

/**
 * Created by pwillic on 25/06/2015.
 */
public interface GroundTypes {
    int GROUND = 0;
    int WALL = 1;
    int WATER = 2;

    boolean[] WALKABLE = new boolean[]{true, false, true};
    boolean[] DANGEROUS = new boolean[]{false, false, true};
}
