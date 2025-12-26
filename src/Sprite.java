
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public abstract class Sprite {
    private BufferedImage sprite;
    private final int width;
    private final int height;
    private final double scale;

    protected final int columnCount;
    protected int columnIndex;
    protected final int rowCount;
    protected int rowIndex;

    /**
     * @param spritePath The path to the file which should be used as sprite
     * @param columnCount The number of Sprites positioned side by side
     * @param rowCount The number of Sprites positioned below each other
     * @param scale The Factor about what the Sprite should be scaled to display it
     */
    Sprite(String spritePath, int columnCount, int rowCount, double scale) {
        try {
            File file = new File(spritePath);
            sprite = ImageIO.read(file);
        } catch (Exception e) {
            System.err.println("Failed to load file: " + spritePath);
            System.err.println(e);
            sprite = MissingTexture.getTexture();
        }

        this.columnCount = columnCount;
        this.rowCount = rowCount;
        this.width = sprite.getWidth() / columnCount;
        this.height = sprite.getHeight() / rowCount;
        this.scale = scale;
        this.columnIndex = 0;
        this.rowIndex = 0;
    }

    /**
     * Update the sprite using delta time to get constant change with varying fps
     * 
     * @param deltaTime The time since last frame in seconds
     */
    public abstract void update(double deltaTime);

    /**
     * Draw the sprite in the graphics system.
     */
    public void draw(double posX, double posY) {
        GraphicSystem.getInstance().drawSprite(this.sprite, (int) posX, (int) posY, this.columnIndex, this.rowIndex, this.scale, this.width, this.height);
    }

    public void setColumnIndex(int index) {
        this.columnIndex = index;
    }

    public void setRowIndex(int index) {
        this.rowIndex = index;
    }
}
