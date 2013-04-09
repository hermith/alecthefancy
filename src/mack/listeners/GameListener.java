package mack.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 
 * Denne klassen håndterer input fra brukeren inne i spillet. Klassen
 * implementerer KeyListener.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
 * 
 */
public class GameListener implements KeyListener {
	// Bevegelsesvariabler
	private boolean left;
	private boolean right;
	private boolean jump;
	private boolean jumpRegistered;
	private boolean duck;
	private boolean sprint;
	private boolean pause;
	private boolean exit;
	private boolean select;
	private boolean bckSpace;

	public GameListener() {
		left = false;
		right = false;
		duck = false;
		sprint = false;
		exit = false;
		jump = false;
		pause = false;
		jumpRegistered = false;
		select = false;
		bckSpace = false;
	}

	/**
	 * Sjekker hvilke taster som blir holdt nede og setter rette
	 * bevegelsesvariabler til true.
	 */
	@Override
	public void keyPressed(KeyEvent kp) {
		int keyCode = kp.getKeyCode();
		if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_SPACE) {
			if (!jumpRegistered) {
				jump = true;
				jumpRegistered = true;
			} else {
				jump = false;
			}
		}
		if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
			jump = false;
			left = true;
		}
		if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
			jump = false;
			right = true;
		}
		if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
			jump = false;
			duck = true;
		}
		if (keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL) {
			jump = false;
			sprint = true;
		}
		if (keyCode == KeyEvent.VK_ESCAPE) {
			exit = true;
		}
		if (keyCode == KeyEvent.VK_ENTER) {
			select = true;
		}
		if (keyCode == KeyEvent.VK_P) {
			if (pause)
				pause = false;
			else
				pause = true;
		}
		if (keyCode == KeyEvent.VK_BACK_SPACE) {
			bckSpace = true;
		}
	}

	/**
	 * Sjekker hvilke taster som blir sluppet setter rette bevegelsesvariabler
	 * til false.
	 */
	@Override
	public void keyReleased(KeyEvent kr) {
		int keyCode = kr.getKeyCode();
		if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_SPACE) {
			jump = false;
			jumpRegistered = false;
		}
		if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
			left = false;
		}
		if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
			right = false;
		}
		if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
			duck = false;
		}
		if (keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL) {
			sprint = false;
		}
		if (keyCode == KeyEvent.VK_ENTER) {
			select = false;
		}
		if (keyCode == KeyEvent.VK_ENTER) {
			exit = false;
		}
		if (keyCode == KeyEvent.VK_BACK_SPACE) {
			bckSpace = false;
		}

	}

	/**
	 * Gjør ingenting i dette tilfellet.
	 */
	@Override
	public void keyTyped(KeyEvent kt) {
		// Ignorer
	}

	/**
	 * En metode som returnerer left variabelen.
	 * 
	 * @return left: boolean
	 */
	public boolean isLeft() {
		return left;
	}

	/**
	 * En metode som returnerer right variabelen.
	 * 
	 * @return right: boolean
	 */
	public boolean isRight() {
		return right;
	}

	/**
	 * En metode som returnerer jump variabelen.
	 * 
	 * @return jump: boolean
	 */
	public boolean isJump() {
		return jump;
	}

	/**
	 * En metode som returnerer duck variabelen.
	 * 
	 * @return duck: boolean
	 */
	public boolean isDuck() {
		return duck;
	}

	/**
	 * En metode som returnerer sprint variabelen.
	 * 
	 * @return sprint: boolean
	 */
	public boolean isSprint() {
		return sprint;
	}

	/**
	 * En metode som returnerer pause variabelen.
	 * 
	 * @return exit: boolean
	 */
	public boolean isPaused() {
		return pause;
	}

	/**
	 * En metode som returnerer exit variabelen.
	 * 
	 * @return exit: boolean
	 */
	public boolean isExit() {
		return exit;
	}

	/**
	 * En metode som returnerer select variabelen.
	 * 
	 * @return select: boolean
	 */
	public boolean isSelect() {
		return select;
	}

	/**
	 * En metode som returnerer respawnCheckpoint variabelen.
	 * 
	 * @return respawnCheckpoint: boolean
	 */
	public boolean isBckSpace() {
		return bckSpace;
	}

	/**
	 * En metode som setter alle keys til false.
	 */
	public void releaseAllKeys() {
		left = false;
		right = false;
		duck = false;
		sprint = false;
		exit = false;
		jump = false;
		pause = false;
		jumpRegistered = false;
		select = false;
	}
}