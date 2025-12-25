
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Sprite {
    private BufferedImage sprite;
    private final int width;
    private final int height;
    private final double scale;
    private final double frameTime;
    private final int columnCount;
    private double lastUpdate;
    private int columnIndex;

    Sprite(String spritePath, int columnCount, double scale, double frameTime) {
        try {
            File file = new File(spritePath);
            sprite = ImageIO.read(file);
        } catch (Exception e) {
            System.err.println("Failed to load file: " + spritePath);
            System.err.println(e);
            sprite = MissingTexture.getTexture();
        }

        this.columnCount = columnCount;
        this.width = sprite.getWidth() / columnCount;
        this.height = sprite.getHeight();
        this.scale = scale;
        this.frameTime = frameTime;
        this.lastUpdate = 0;
        this.columnIndex = 0;
    }

    public void draw(double posX, double posY) {
        GraphicSystem.getInstance().drawSprite(this.sprite, (int) posX, (int) posY, this.columnIndex, this.scale, this.width, this.height);
    }

    public void update(double deltaTime) {
        this.lastUpdate += deltaTime;
        if (this.lastUpdate > this.frameTime) {
            this.lastUpdate -= this.frameTime;

            this.columnIndex++;

            if (this.columnIndex > this.columnCount) {
                this.columnIndex = 0;
            }
        }
    }

    public void resetIndex() {
        this.columnIndex = 0;
    }
}
