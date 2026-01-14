package ZombieGame;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

// (c) Thorsten Hasbargen

import javax.swing.*;

import ZombieGame.Systems.Graphic.GraphicSystem;

class Frame extends JFrame {
	private static final long serialVersionUID = 2L;
	// Switch between window and fullscreen mode
	private static final boolean BORDERLESS_FULLSCREEN = true;

	// BUG: in window mode the ui elements get NOT repositioned and therefore are not longer in visible range when reducing size
	// TODO: Different window size should require different scales

	public Frame() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		Rectangle bounds = gc.getBounds(); // kompletter Monitor

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

		Viewport.setScreenWidth(this.getWidth());
		Viewport.setScreenHeight(this.getHeight());

		this.setContentPane(GraphicSystem.getInstance());

		// for keyboard input
		GraphicSystem.getInstance().setFocusable(true);
	}

	public void displayOnScreen() {
		this.validate();
		this.setVisible(true);

		// Fokus erst wenn sichtbar
		GraphicSystem.getInstance().requestFocusInWindow();
	}

}
