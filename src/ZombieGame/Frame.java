package ZombieGame;

// (c) Thorsten Hasbargen

import javax.swing.*;

import ZombieGame.Systems.Graphic.GraphicSystem;

class Frame extends JFrame {
	private static final long serialVersionUID = 2L;

	public Frame() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		// TODO: Make it depend on the monitor resolution
		this.setSize(Constants.WORLDPART_WIDTH + 2, Constants.WORLDPART_HEIGHT + 2);
		Viewport.setScreenWidth(this.getWidth());
		Viewport.setScreenHeight(this.getHeight());

		// Make the game full screen
		this.setAlwaysOnTop(true);
		this.setUndecorated(true);
		this.setResizable(false);

		// needed for Keyboard input !!!
		GraphicSystem.getInstance().setFocusable(true);
		GraphicSystem.getInstance().requestFocusInWindow();

		this.setContentPane(GraphicSystem.getInstance());
	}

	public void displayOnScreen() {
		this.validate();
		this.setVisible(true);
	}
}
