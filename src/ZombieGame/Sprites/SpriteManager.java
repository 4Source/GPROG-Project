package ZombieGame.Sprites;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
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
                URL url = SpriteManager.class.getClassLoader().getResource(path);

                if (url == null) {
                    throw new RuntimeException("Sprite not found");
                }

                return ImageIO.read(url);
            } catch (Exception e) {
                System.err.println("Failed to load file: " + p);
                System.err.println(e);
                return MissingTexture.getTexture();
            }
        });
    }
}
