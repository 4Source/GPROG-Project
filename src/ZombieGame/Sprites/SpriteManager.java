package ZombieGame.Sprites;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import ZombieGame.MissingTexture;

public class SpriteManager {
    private static final Map<String, BufferedImage> cache = new HashMap<>();

    private SpriteManager() {
    }

    /**
     * @param path The path to the file which should be used as sprite
     * @return The buffered image read from the cache if already loaded or load from file.
     */
    public static BufferedImage getSprite(String path) {
        return cache.computeIfAbsent(path, p -> {
            try {
                return ImageIO.read(new File(path));
            } catch (Exception e) {
                System.err.println("Failed to load file: " + p);
                System.err.println(e);
                return MissingTexture.getTexture();
            }
        });
    }
}
