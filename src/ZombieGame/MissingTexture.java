package ZombieGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class MissingTexture {
    private static MissingTexture instance;
    private BufferedImage texture;
    private int size;

    private MissingTexture() {
        int tile = 8;
        size = 2 * tile;
        this.texture = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = texture.createGraphics();

        Color pink = new Color(255, 0, 255);
        Color black = Color.BLACK;

        for (int y = 0; y < size; y += tile) {
            for (int x = 0; x < size; x += tile) {
                boolean isPink = ((x / tile) + (y / tile)) % 2 == 0;
                g.setColor(isPink ? pink : black);
                g.fillRect(x, y, tile, tile);
            }
        }

        g.dispose();
    }

    public static BufferedImage getTexture() {
        if (MissingTexture.instance == null) {
            MissingTexture.instance = new MissingTexture();
        }

        return MissingTexture.instance.texture;
    }

    public static int getSize() {
        if (MissingTexture.instance == null) {
            MissingTexture.instance = new MissingTexture();
        }

        return MissingTexture.instance.size;
    }
}
