import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

enum GraphicLayer {
    BACKGROUND, GAME, EFFECTS, UI,
}

public class GraphicSystem extends JPanel {
    private static GraphicSystem instance;
    private Map<GraphicLayer, ArrayList<Drawable>> drawables;

    private static boolean showFPS = false;
    private long lastTime;

    // GraphicsSystem variables
    private GraphicsConfiguration graphicsConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    private BufferedImage imageBuffer;
    private Graphics2D graphics;

    private GraphicSystem() {
        // TODO: Make it depend on the monitor resolution
        this.setSize(Constants.WORLDPART_WIDTH, Constants.WORLDPART_HEIGHT);
        this.imageBuffer = this.graphicsConf.createCompatibleImage(this.getWidth(), this.getHeight());
        this.graphics = this.imageBuffer.createGraphics();

        // initialize Listeners
        this.addMouseListener(InputSystem.getInstance());
        this.addMouseMotionListener(InputSystem.getInstance());
        this.addKeyListener(InputSystem.getInstance());

        this.drawables = new HashMap<>();
        this.lastTime = System.currentTimeMillis();
    }

    /**
     * @return The instance of the singleton or newly created if first access.
     */
    public static synchronized GraphicSystem getInstance() {
        if (instance == null) {
            instance = new GraphicSystem();
        }

        return instance;
    }

    /**
     * Register a drawable component for the visualization
     * 
     * @param component The component to register
     * @param layer The Layer the component should be drawn in
     */
    public void registerComponent(Drawable component) {
        drawables.putIfAbsent(component.getLayer(), new ArrayList<>());
        drawables.get(component.getLayer()).add(component);
    }

    /**
     * Unregister a drawable component from the visualization
     * 
     * @param component The component to unregister
     */
    public void unregisterComponent(Drawable component) {
        drawables.forEach((l, d) -> {
            if (d.remove(component)) {
                return;
            }
        });
    }

    /**
     * Clear the screen before drawing on it
     */
    public void clear() {
        this.graphics.setColor(Color.LIGHT_GRAY);
        this.graphics.fillRect(0, 0, Constants.WORLDPART_WIDTH, Constants.WORLDPART_HEIGHT);
    }

    /**
     * Set the style
     */
    private void setStyle(DrawStyle style) {
        graphics.setColor(style.color());
        graphics.setStroke(style.stroke());
        graphics.setFont(style.font());
    }

    public void update() {
        if (InputSystem.getInstance().isPressed(Action.SHOW_FPS)) {
            GraphicSystem.showFPS = !GraphicSystem.showFPS;
        }
    }

    /**
     * Draw the entities on the Screen
     */
    public void draw() {
        drawables.getOrDefault(GraphicLayer.BACKGROUND, new ArrayList<>()).forEach(entity -> entity.draw());
        drawables.getOrDefault(GraphicLayer.GAME, new ArrayList<>()).forEach(entity -> entity.draw());
        drawables.getOrDefault(GraphicLayer.EFFECTS, new ArrayList<>()).forEach(entity -> entity.draw());
        drawables.getOrDefault(GraphicLayer.UI, new ArrayList<>()).forEach(entity -> entity.draw());
    }

    // TODO: Draw position by center coordinate

    /**
     * Renders the text of the specified {@code String}, using the current text attribute state in the {@code Graphics2D} context.
     * The baseline of the first character is at position (<i>x</i>,&nbsp;<i>y</i>) in the User Space.
     * The rendering attributes applied include the {@code Clip}, {@code Transform}, {@code Paint}, {@code Font} and {@code Composite} attributes. For characters in script systems such as Hebrew and Arabic, the glyphs can be rendered from right to left, in which case the coordinate supplied is the location of the leftmost character on the baseline.
     * 
     * @param str the string to be rendered
     * @param x the x coordinate of the location where the {@code String} should be rendered
     * @param y the y coordinate of the location where the {@code String} should be rendered
     * @param style the style of the string to be drawn.
     */
    public void drawString(String str, int x, int y, DrawStyle style) {
        this.setStyle(style);
        this.graphics.drawString(str, x, y);
    }

    /**
     * Draws the outline of an oval.
     * The result is a circle or ellipse that fits within the rectangle specified by the {@code x}, {@code y}, {@code width}, and {@code height} arguments.
     * <p>
     * The oval covers an area that is
     * <code>width&nbsp;+&nbsp;1</code> pixels wide
     * and <code>height&nbsp;+&nbsp;1</code> pixels tall.
     * 
     * @param x the x coordinate of the upper left corner of the oval to be drawn.
     * @param y the y coordinate of the upper left corner of the oval to be drawn.
     * @param width the width of the oval to be drawn.
     * @param height the height of the oval to be drawn.
     * @param style the style of the oval to be drawn.
     */
    public void drawFillOval(int x, int y, int width, int height, DrawStyle style) {
        this.setStyle(style);
        this.graphics.fillOval(x, y, width, height);
    }

    /**
     * Fills an oval bounded by the specified rectangle with the current color.
     * 
     * @param x the x coordinate of the upper left corner of the oval to be filled.
     * @param y the y coordinate of the upper left corner of the oval to be filled.
     * @param width the width of the oval to be filled.
     * @param height the height of the oval to be filled.
     * @param style the style of the oval to be drawn.
     */
    public void drawOval(int x, int y, int width, int height, DrawStyle style) {
        this.setStyle(style);
        this.graphics.drawOval(x, y, width, height);
    }

    /**
     * Draws the outline of the specified rectangle.
     * The left and right edges of the rectangle are at {@code x} and <code>x&nbsp;+&nbsp;width</code>.
     * The top and bottom edges are at {@code y} and <code>y&nbsp;+&nbsp;height</code>.
     * The rectangle is drawn using the graphics context's current color.
     * 
     * @param x the x coordinate of the rectangle to be drawn.
     * @param y the y coordinate of the rectangle to be drawn.
     * @param width the width of the rectangle to be drawn.
     * @param height the height of the rectangle to be drawn.
     * @param style the style of the rectangle to be drawn.
     */
    public void drawFillRect(int x, int y, int width, int height, DrawStyle style) {
        this.setStyle(style);
        this.graphics.fillRect(x, y, width, height);
    }

    /**
     * Draws the outline of the specified rectangle. The left and right edges of the rectangle are at x and x + width. The top and bottom edges are at y and y + height. The rectangle is drawn using the graphics context's current color.
     * 
     * @param x the x coordinate of the rectangle to be drawn.
     * @param y the y coordinate of the rectangle to be drawn.
     * @param width the width of the rectangle to be drawn.
     * @param height the height of the rectangle to be drawn.
     * @param style the style of the rectangle to be drawn.
     */
    public void drawRect(int x, int y, int width, int height, DrawStyle style) {
        this.setStyle(style);
        this.graphics.drawRect(x, y, width, height);
    }

    /**
     * Draws as much of the specified image as has already been scaled to fit inside the specified rectangle.
     * 
     * @param sprite The specified image to be drawn. This method does nothing if img is null.
     * @param spriteX The x coordinate.
     * @param spriteY The y coordinate.
     * @param spriteWidth The width of the rectangle.
     * @param spriteHeight The height of the rectangle.
     */
    public void drawSprite(BufferedImage sprite, int drawX, int drawY, int drawWidth, int drawHeight, int spriteX, int spriteY, int spriteWidth, int spriteHeight) {
        this.graphics.drawImage(sprite, drawX, drawY, drawX + drawWidth, drawY + drawHeight, spriteX, spriteY, spriteWidth, spriteHeight, null);
    }

    public void drawSprite(BufferedImage sprite, int posX, int posY, int index, double scale, int spriteWidth, int spriteHeight) {
        int columnCount = sprite.getWidth() / spriteWidth;
        int rowCount = sprite.getHeight() / spriteHeight;
        int columnIndex = index % columnCount;
        int rowIndex = (index / columnCount) % rowCount;

        int drawWidth_2 = (int) (spriteWidth * scale / 2);
        int drawHeight_2 = (int) (spriteHeight * scale / 2);

        this.graphics.drawImage(sprite, posX - drawWidth_2, posY - drawHeight_2, posX + drawWidth_2, posY + drawHeight_2, (columnIndex * spriteWidth), (rowIndex * spriteHeight), ((columnIndex + 1) * spriteWidth), ((rowIndex + 1) * spriteHeight), null);
    }

    /**
     * Draw the objects to screen
     */
    public void swapBuffers() {
        if (showFPS) {
            long currentTime = System.currentTimeMillis();
            long diff = currentTime - lastTime;
            lastTime = currentTime;

            this.drawString("FPS: " + (int) Math.ceil(1000.0 / diff), 20, 40, new DrawStyle().color(Color.MAGENTA));
        }

        this.getGraphics().drawImage(this.imageBuffer, 0, 0, this);
    }
}
