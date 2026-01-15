package ZombieGame.Sprites;

import java.awt.image.BufferedImage;
import java.io.File;
import java.security.InvalidParameterException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import ZombieGame.Systems.Graphic.MissingTexture;

public class SpriteManager {
    private static final HashMap<String, BufferedImage> cache = new HashMap<>();

    private SpriteManager() {
    }

    /**
     * @param path The path to the file which should be used as sprite
     * @return The buffered image read from the cache if already loaded or load from file.
     */
    public static BufferedImage getSprite(String path) {
        return cache.computeIfAbsent(path, p -> {
            try {
                File file = new File(path);
                File parentPath = file.getParentFile();
                if(parentPath == null) {
                    throw new InvalidParameterException("No parent path");
                }

                if(!parentPath.exists()) {
                    throw new InvalidParameterException("Path does not exist");
                }

                return ImageIO.read(file);
            } catch (Exception e) {
                System.err.println("Failed to load file: " + p);
                System.err.println(e);
                return MissingTexture.getTexture();
            }
        });
    }
}
