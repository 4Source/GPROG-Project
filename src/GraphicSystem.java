import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class GraphicSystem extends JPanel {
    private static GraphicSystem instance;

    // GraphicsSystem variables
    private GraphicsConfiguration graphicsConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    private BufferedImage imageBuffer;
    public Graphics graphics;

    private GraphicSystem() {
        // TODO: Make it depend on the monitor resolution
        this.setSize(Constants.WORLDPART_WIDTH, Constants.WORLDPART_HEIGHT);
        this.imageBuffer = this.graphicsConf.createCompatibleImage(this.getWidth(), this.getHeight());
        this.graphics = this.imageBuffer.getGraphics();

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
     * Draw a GameObject on the Screen
     * 
     * @param gameObject The game object to draw
     */
    public void draw(GameObject gameObject) {
        gameObject.draw();
    }

    /**
     * Draw the objects to screen
     */
    public void swapBuffers() {
        this.getGraphics().drawImage(this.imageBuffer, 0, 0, this);
    }
}
