
// (c) Thorsten Hasbargen

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

class Panel extends JPanel implements GraphicSystem {
	// constants
	private static final long serialVersionUID = 1L;
	private static final Font font = new Font("Arial", Font.PLAIN, 24);

	// InputSystem is an external instance
	private ClickInputSystem inputSystem = new ClickInputSystem();
	private World world = null;

	// GraphicsSystem variables
	private GraphicsConfiguration graphicsConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	private BufferedImage imageBuffer;
	private Graphics graphics;

	public Panel() {
		// TODO: Make it depend on the monitor resolution
		this.setSize(Constants.WORLDPART_WIDTH, Constants.WORLDPART_HEIGHT);
		this.imageBuffer = this.graphicsConf.createCompatibleImage(this.getWidth(), this.getHeight());
		this.graphics = this.imageBuffer.getGraphics();

		// initialize Listeners
		this.addMouseListener(this.inputSystem);
		this.addMouseMotionListener(this.inputSystem);
		this.addKeyListener(this.inputSystem);
	}

	public void clear() {
		this.graphics.setColor(Color.LIGHT_GRAY);
		this.graphics.fillRect(0, 0, Constants.WORLDPART_WIDTH, Constants.WORLDPART_HEIGHT);
	}

	public final void draw(GameObject gameObject) {
		int x = (int) (gameObject.posX - gameObject.radius - this.world.worldPartX);
		int y = (int) (gameObject.posY - gameObject.radius - this.world.worldPartY);
		int d = (int) (gameObject.radius * 2);

		this.graphics.setColor(gameObject.color);
		this.graphics.fillOval(x, y, d, d);
		this.graphics.setColor(Color.DARK_GRAY);
		this.graphics.drawOval(x, y, d, d);
	}

	public final void draw(TextObject textObject) {
		this.graphics.setFont(Panel.font);
		this.graphics.setColor(Color.DARK_GRAY);
		this.graphics.drawString(textObject.toString(), (int) textObject.posX + 1, (int) textObject.posY + 1);
		this.graphics.setColor(textObject.color);
		this.graphics.drawString(textObject.toString(), (int) textObject.posX, (int) textObject.posY);
	}

	public void swapBuffers() {
		this.getGraphics().drawImage(this.imageBuffer, 0, 0, this);
	}

	public final InputSystem getInputSystem() {
		return this.inputSystem;
	}

	public final void setWorld(World world) {
		this.world = world;
	}
}
