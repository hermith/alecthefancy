package mack.entities.creatures;

import mack.entities.Animation;

/**
 * 
 * Dette er klassen som h�ndterer spilleren. Klassen arver klassen Creature og
 * h�ndterer i tillegg kollisjon og hopp.
 * 
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 * 
 */
public class Player extends Creature {

	// Konstanter
	public static final float JUMP_SPEED = -0.57f;
	public static final float MAX_SPEED = 0.3f;

	/**
	 * Lager en ny spiller med spesifikke animasjoner.
	 * 
	 * @param left
	 *            : Animation - Animasjonen som skal vises n�r spilleren g�r mot
	 *            venstre.
	 * @param right
	 *            : Animation - Animasjonen som skal vises n�r spilleren g�r mot
	 *            h�yre.
	 * @param deadLeft
	 *            : Animation - Animasjonen som skal vises n�r spilleren er d�d
	 *            i retning venstre.
	 * @param deadRight
	 *            : Animation - Animasjonen som skal vises n�r spilleren er d�d
	 *            i retning h�yre.
	 * @param animIdle
	 *            : Animation - Animasjonen som skal vises n�r spilleren st�r i
	 *            ro og ser mot venstre.
	 * @param animIdleR
	 *            : Animation - Animasjonen som skal vises n�r spilleren st�r i
	 *            ro og ser mot h�yre.
	 * @param animJump
	 *            : Animation - Animasjonen som skal vises n�r spilleren hopper
	 *            mot venstre.
	 * @param animJumpR
	 *            : Animation - Animasjonen som skal vises n�r spilleren hopper
	 *            mot h�yre.
	 */
	public Player(Animation left, Animation right, Animation deadLeft, Animation deadRight, Animation animIdle,
			Animation animIdleR, Animation animJump, Animation animJumpR) {
		super(left, right, deadLeft, deadRight, animIdle, animIdleR, animJump, animJumpR);
		super.setMaxSpeed(MAX_SPEED);
		super.setJumpSpeed(JUMP_SPEED);
	}

	/**
	 * Setter farten til 0 ved kollisjon med blokk i x-retning.
	 */
	@Override
	public void collideHorizontal() {
		setDX(0f);
	}

	/**
	 * Setter DY til 0 ved kollisjon med blokk i y-retning.
	 */
	@Override
	public void collideVertical() {
		if (getDY() > 0) {
			setOnGround(true);
		}
		setDY(0);
	}

	/**
	 * Setter ny y-lokasjon
	 * 
	 * @param y
	 *            : float - Ny y-verdi.
	 */
	@Override
	public void setY(float y) {
		if (Math.round(y) > Math.round(getY())) {
			setOnGround(false);
		}
		super.setY(y);
	}

	/**
	 * Gj�r ingenting da dette er en spiller.
	 */
	@Override
	public void wakeUp() {
		// Ikke en fiende, har ingen wake up funksjon
	}

	/**
	 * Gj�r ingenting da dette er en spiller.
	 */
	@Override
	public void sleep() {
		// Ikke en fiende, har ingen sleep up funksjon
	}

	/**
	 * Setter akselerasjon oppover n�r hopp kalles.
	 * 
	 * @param forceJump
	 *            : boolean - Kan spilleren "hoppe" midt i luften.
	 * @param jumpMultiplier
	 *            : float - Variabel som bestemmer om spilleren skal hoppe
	 *            ekstra h�yt.
	 */
	public void jump(boolean forceJump, float jumpMultiplier) {
		if (isOnGround() || forceJump) {
			setOnGround(false);
			setDY(getJumpSpeed() * jumpMultiplier);
		}
	}
}