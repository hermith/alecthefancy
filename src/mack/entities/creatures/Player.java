package mack.entities.creatures;

import mack.entities.Animation;

/**
 * 
 * Dette er klassen som håndterer spilleren. Klassen arver klassen Creature og
 * håndterer i tillegg kollisjon og hopp.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
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
	 *            : Animation - Animasjonen som skal vises når spilleren går mot
	 *            venstre.
	 * @param right
	 *            : Animation - Animasjonen som skal vises når spilleren går mot
	 *            høyre.
	 * @param deadLeft
	 *            : Animation - Animasjonen som skal vises når spilleren er død
	 *            i retning venstre.
	 * @param deadRight
	 *            : Animation - Animasjonen som skal vises når spilleren er død
	 *            i retning høyre.
	 * @param animIdle
	 *            : Animation - Animasjonen som skal vises når spilleren står i
	 *            ro og ser mot venstre.
	 * @param animIdleR
	 *            : Animation - Animasjonen som skal vises når spilleren står i
	 *            ro og ser mot høyre.
	 * @param animJump
	 *            : Animation - Animasjonen som skal vises når spilleren hopper
	 *            mot venstre.
	 * @param animJumpR
	 *            : Animation - Animasjonen som skal vises når spilleren hopper
	 *            mot høyre.
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
	 * Gjør ingenting da dette er en spiller.
	 */
	@Override
	public void wakeUp() {
		// Ikke en fiende, har ingen wake up funksjon
	}

	/**
	 * Gjør ingenting da dette er en spiller.
	 */
	@Override
	public void sleep() {
		// Ikke en fiende, har ingen sleep up funksjon
	}

	/**
	 * Setter akselerasjon oppover når hopp kalles.
	 * 
	 * @param forceJump
	 *            : boolean - Kan spilleren "hoppe" midt i luften.
	 * @param jumpMultiplier
	 *            : float - Variabel som bestemmer om spilleren skal hoppe
	 *            ekstra høyt.
	 */
	public void jump(boolean forceJump, float jumpMultiplier) {
		if (isOnGround() || forceJump) {
			setOnGround(false);
			setDY(getJumpSpeed() * jumpMultiplier);
		}
	}
}