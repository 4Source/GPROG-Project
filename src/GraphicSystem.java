import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class GraphicSystem extends JPanel {
    private static GraphicSystem instance;

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

    /**
     * Draw the visual component of the entity on the Screen
     * 
     * @param entity The entity to draw
     */
    public void draw(Entity entity) {
        entity.getComponent(VisualComponent.class).ifPresent(component -> component.draw());
        if (PhysicsSystem.enableDebug) {
            entity.getComponent(PhysicsComponent.class).ifPresent(component -> component.draw());
        }
    }

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
     * Draw the objects to screen
     */
    public void swapBuffers() {
        this.getGraphics().drawImage(this.imageBuffer, 0, 0, this);
    }
}
