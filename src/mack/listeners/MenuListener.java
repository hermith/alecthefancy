package mack.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 
 * Denne klassen håndterer input fra brukeren ute i menyen. Klassen
 * implementerer KeyListener.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
 * 
 */
public class MenuListener implements KeyListener {
	// "Bevegelsesvariabler"
	private boolean left;
	private boolean right;
	private boolean select;
	private boolean esc;

	public MenuListener() {
		left = false;
		right = false;
		select = false;
		esc = false;
	}

	/**
	 * Sjekker hvilke taster som blir holdt nede og setter rette
	 * bevegelsesvariabler til true.
	 */
	@Override
	public void keyPressed(KeyEvent kp) {
		int keyCode = kp.getKeyCode();
		if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_W
				|| keyCode == KeyEvent.VK_UP) {
			left = true;
		}
		if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_S
				|| keyCode == KeyEvent.VK_DOWN) {
			right = true;
		}
		if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
			select = true;
		}
		if (keyCode == KeyEvent.VK_ESCAPE) {
			esc = true;
		}
	}

	/**
	 * Sjekker hvilke taster som blir sluppet og setter rette
	 * bevegelsesvariabler til false.
	 */
	@Override
	public void keyReleased(KeyEvent kp) {
		int keyCode = kp.getKeyCode();
		if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
			left = false;
		}
		if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
			right = false;
		}
		if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
			select = false;
		}
		if (keyCode == KeyEvent.VK_ENTER) {
			esc = false;
		}
	}

	/**
	 * Gjør ingenting i dette tilfellet.
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// Ignorer
	}

	public void releaseAllKeys() {
		left = false;
		right = false;
		select = false;
		esc = false;
	}

	/**
	 * Metode som returnerer left-variabelen.
	 * 
	 * @return left: boolean
	 */
	public boolean isLeft() {
		return left;
	}

	/**
	 * Metode som returnerer right-variabelen.
	 * 
	 * @return right: boolean
	 */
	public boolean isRight() {
		return right;
	}

	/**
	 * Metode som returnerer select-variabelen.
	 * 
	 * @return select: boolean
	 */
	public boolean isSelect() {
		return select;
	}

	/**
	 * 
	 * @return esc: boolean
	 */
	public boolean isEsc() {
		return esc;
	}
}