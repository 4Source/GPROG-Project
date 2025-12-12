
// (c) Thorsten Hasbargen

import java.awt.event.*;

class ClickInputSystem implements InputSystem, KeyListener, MouseListener, MouseMotionListener {
	// UserInput variables
	private UserInput userInput = new UserInput();

	public void mousePressed(MouseEvent event) {
		// an input Event occurs
		this.userInput.isMouseEvent = true;
		this.userInput.mousePressedX = event.getX();
		this.userInput.mousePressedY = event.getY();
		this.userInput.mouseButton = event.getButton();
		this.userInput.isMousePressed = true;
	}

	public void mouseReleased(MouseEvent event) {
		this.userInput.isMousePressed = false;
	}

	public void mouseMoved(MouseEvent event) {
		this.userInput.mouseMovedX = event.getX();
		this.userInput.mouseMovedY = event.getY();
	}

	public void mouseDragged(MouseEvent event) {
		this.userInput.mouseMovedX = event.getX();
		this.userInput.mouseMovedY = event.getY();
	}

	public void keyPressed(KeyEvent event) {
		this.userInput.isKeyEvent = true;
		this.userInput.keyPressed = event.getKeyChar();
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mouseClicked(MouseEvent event) {
	}

	public void keyReleased(KeyEvent event) {
	}

	public void keyTyped(KeyEvent event) {
	}

	public UserInput getUserInput() {
		return this.userInput;
	}
}
