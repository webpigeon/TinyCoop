package FastGame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ViewSaver {

    public static void saveToFile(Viewer viewer, String filename){
        BufferedImage image = new BufferedImage(viewer.getWidth(), viewer.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        viewer.paintAll(g);
        File file = new File(filename);
        try {
            file.createNewFile();
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            System.err.println(file.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
