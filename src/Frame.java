
// (c) Thorsten Hasbargen

import javax.swing.*;

class Frame extends JFrame {
	private static final long serialVersionUID = 2L;

	private Panel panel = null;

	public Frame() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		// TODO: Make it depend on the monitor resolution
		this.setSize(Constants.WORLDPART_WIDTH + 2, Constants.WORLDPART_HEIGHT + 2);

		// Make the game full screen
		this.setAlwaysOnTop(true);
		this.setUndecorated(true);
		this.setResizable(false);

		// TODO: Should not be hardcoded here
		this.panel = new Panel();

		// needed for Keyboard input !!!
		this.panel.setFocusable(true);
		this.panel.requestFocusInWindow();

		this.setContentPane(this.panel);
	}

	public void displayOnScreen() {
		this.validate();
		this.setVisible(true);
	}

	public GraphicSystem getGraphicSystem() {
		return this.panel;
	}

	public InputSystem getInputSystem() {
		return this.panel.getInputSystem();
	}
}
