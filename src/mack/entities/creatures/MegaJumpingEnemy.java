package mack.entities.creatures;

import mack.entities.Animation;

/**
 * 
 * Klassen arver klassen Creature og h�ndterer en stor fiende som hopper.
 * 
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 * 
 */
public class MegaJumpingEnemy extends Creature {
	public static final float JUMP_SPEED = -0.7f;
	public static final float MAX_SPEED = 0.05f;

	public MegaJumpingEnemy(Animation left, Animation right, Animation deadLeft, Animation deadRight) {
		super(left, right, deadLeft, deadRight);
		setJumpSpeed(JUMP_SPEED);
		setMaxSpeed(MAX_SPEED);
		setImmortal(true);
	}

	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		jump();
	}

	@Override
	public void collideVertical() {
		if (getDY() > 0) {
			setOnGround(true);
		}
		setDY(0);
	}

	/**
	 * Metode som f�r vesenet til � hoppe ved � sett DY lik jumpSpeed.
	 */
	private void jump() {
		if (isOnGround()) {
			setOnGround(false);
			setDY(getJumpSpeed());
		}
	}
}