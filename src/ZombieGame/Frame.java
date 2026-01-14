package ZombieGame;

// (c) Thorsten Hasbargen

import javax.swing.*;

import ZombieGame.Systems.Graphic.GraphicSystem;

class Frame extends JFrame {
	private static final long serialVersionUID = 2L;
	//hier kann zwischen Fenstermodus und Fullscreen gewechselt werden!
	private static final boolean BORDERLESS_FULLSCREEN = true;


	public Frame() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
		java.awt.GraphicsDevice gd = ge.getDefaultScreenDevice();
		java.awt.GraphicsConfiguration gc = gd.getDefaultConfiguration();
		java.awt.Rectangle bounds = gc.getBounds(); // kompletter Monitor

		if (BORDERLESS_FULLSCREEN) {
			// Borderless fullscreen
			this.setUndecorated(true);
			this.setResizable(false);
			this.setAlwaysOnTop(false);

			this.setBounds(bounds);
		} else {
			this.setUndecorated(false);
			this.setResizable(true);
			this.setAlwaysOnTop(false);

			// Startgröße wird danach maximiert
			this.setSize(1280, 720);
			this.setLocationRelativeTo(null);

			// kein setBounds(bounds) hier
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}


		this.setContentPane(GraphicSystem.getInstance());

		// for keyboard input
		GraphicSystem.getInstance().setFocusable(true);
	}


	public void displayOnScreen() {
		this.validate();
		this.setVisible(true);

		Viewport.setScreenWidth(this.getContentPane().getWidth());
		Viewport.setScreenHeight(this.getContentPane().getHeight());

		// Fokus erst wenn sichtbar
		GraphicSystem.getInstance().requestFocusInWindow();
	}


}
