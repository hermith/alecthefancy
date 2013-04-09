package mack.entities.creatures;

import java.lang.reflect.Constructor;

import mack.entities.Animation;
import mack.entities.Entity;

/**
 * 
 * Dette er superklassen til alle bevegelige vesener i spillet. Klassen arver
 * Entity og er selv abstrakt. Klassen h�ndterer mer avanserte animasjoner,
 * hastighet, samt er rekke stadier.
 * 
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 * 
 */
public abstract class Creature extends Entity {
	public static final int STATE_ALIVE = 0;
	public static final int STATE_DYING = 1;
	public static final int STATE_DEAD = 2;
	public static final int DIE_TIME = 1000;

	// Animasjoner
	private Animation left;
	private Animation right;
	private Animation jump;
	private Animation jumpR;
	private Animation idleLeft;
	private Animation idleRight;
	private Animation deadLeft;
	private Animation deadRight;

	// Bevegelsesvariabler
	private float maxSpeed;
	private float jumpSpeed;

	// Statusvariabler
	private int state;
	private long stateTime;
	private long creep_died;
	private boolean onGround;
	private boolean isFlying;
	private boolean isImmortal;

	/**
	 * Lager et nytt vesen med spesifikke animasjoner.
	 * 
	 * @param left
	 *            : Animation - Animasjonen som skal vises n�r vesenet g�r mot
	 *            venstre.
	 * @param right
	 *            : Animation - Animasjonen som skal vises n�r vesenet g�r mot
	 *            h�yre.
	 * @param deadLeft
	 *            : Animation - Animasjonen som skal vises n�r vesenet er d�d i
	 *            retning venstre.
	 * @param deadRight
	 *            : Animation - Animasjonen som skal vises n�r vesenet er d�d i
	 *            retning h�yre.
	 * @param idleLeft
	 *            : Animation - Animasjonen som skal vises n�r vesenet st�r i ro
	 *            og ser mot venstre.
	 * @param idleRight
	 *            : Animation - Animasjonen som skal vises n�r vesenet st�r i ro
	 *            og ser mot h�yre.
	 * @param jump
	 *            : Animation - Animasjonen som skal vises n�r vesenet hopper
	 *            mot venstre.
	 * @param jumpR
	 *            : Animation - Animasjonen som skal vises n�r vesenet hopper
	 *            mot h�yre.
	 */
	public Creature(Animation left, Animation right, Animation deadLeft, Animation deadRight, Animation idleLeft,
			Animation idleRight, Animation jump, Animation jumpR) {
		super(right);
		this.left = left;
		this.right = right;
		this.deadLeft = deadLeft;
		this.deadRight = deadRight;
		this.idleLeft = idleLeft;
		this.idleRight = idleRight;
		this.jump = jump;
		this.jumpR = jumpR;
		state = STATE_ALIVE;
	}

	/**
	 * Lager et nytt vesen med spesifikke animasjoner.
	 * 
	 * @param left
	 *            : Animation - Animasjonen som skal vises n�r vesenet g�r mot
	 *            venstre.
	 * @param right
	 *            : Animation - Animasjonen som skal vises n�r vesenet g�r mot
	 *            h�yre.
	 * @param deadLeft
	 *            : Animation - Animasjonen som skal vises n�r vesenet er d�d i
	 *            retning venstre.
	 * @param deadRight
	 *            : Animation - Animasjonen som skal vises n�r vesenet er d�d i
	 *            retning h�yre.
	 */
	public Creature(Animation left, Animation right, Animation deadLeft, Animation deadRight) {
		super(right);
		this.left = left;
		this.right = right;
		this.deadLeft = deadLeft;
		this.deadRight = deadRight;
		state = STATE_ALIVE;
		creep_died = 0;
	}

	/**
	 * Kloner objektet.
	 */
	@Override
	public Object clone() {
		Constructor<?> constructor = getClass().getConstructors()[0];
		try {
			return constructor.newInstance(new Object[] { (Animation) left.clone(), (Animation) right.clone(),
					(Animation) deadLeft.clone(), (Animation) deadRight.clone(), (Animation) idleLeft.clone(),
					(Animation) idleRight.clone(), (Animation) jump.clone(), (Animation) jumpR.clone() });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Kloner objektet.
	 */
	@Override
	public Object cloneOther() {
		Constructor<?> constructor = getClass().getConstructors()[0];
		try {
			return constructor.newInstance(new Object[] { (Animation) left.clone(), (Animation) right.clone(),
					(Animation) deadLeft.clone(), (Animation) deadRight.clone() });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Henter maksimum hastighet.
	 * 
	 * @return maxSpeed: float - Gjeldende maksimal hastighet.
	 */
	public float getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * Setter maksimum hastighet.
	 * 
	 * @param maxSpeed
	 *            : float - Ny maksimal hastighet for vesenet.
	 */
	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	/**
	 * Henter hoppehastighet.
	 * 
	 * @return jumpSpeed: float - Gjeldende maksimal hoppehastighet.
	 */
	public float getJumpSpeed() {
		return jumpSpeed;
	}

	/**
	 * setter maks hoppehastighet.
	 */
	public void setJumpSpeed(float jumpSpeed) {
		this.jumpSpeed = jumpSpeed;
	}

	/**
	 * Henter tilstanden til vesenet. Tilstanden kan v�re i live, d�ende eller
	 * d�d.
	 * 
	 * @return state: int - Status p� dyret.
	 */
	public int getState() {
		return state;
	}

	/**
	 * Setter tilstanden til vesenet. Tilstanden er enten i live, d�ende eller
	 * d�d.
	 * 
	 * @param state
	 *            : int - Ny status p� dyret.
	 */
	public void setState(int state) {
		if (this.state != state) {
			this.state = state;
			stateTime = 0;
			if (state == STATE_DYING) {
				setDX(0);
				setDY(0);
			}
		}

	}

	/**
	 * Metode som sjekker om enheten befinner seg p� bakken.
	 * 
	 * @return onGround: boolean - befinner vesenet seg p� bakken?
	 */
	public boolean isOnGround() {
		return onGround;
	}

	/**
	 * Metode som setter om enheten befinner seg p� bakken.
	 * 
	 * @param onGround
	 *            : boolean - Ny sannhetsverdi om vesenet befinner seg p�
	 *            bakken.
	 */
	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	/**
	 * Vekker opp vesenet slik at det begynner � bevege p� seg.
	 */
	public void wakeUp() {
		if (isAlive() && getDX() == 0) {
			setDX(-getMaxSpeed());
		}
	}

	/**
	 * Setter vesenet i et sovestadie slik at det slutter � bevege p� seg.
	 */
	public void sleep() {
		if (isAlive()) {
			setDX(0);
		}
	}

	/**
	 * Sjekker om vesenet er i live.
	 * 
	 * @return isAlive: boolean - Er vesenet i live?
	 */
	public boolean isAlive() {
		return (state == STATE_ALIVE);
	}

	/**
	 * Sjekker om vesenet flyr.
	 * 
	 * @return isFlyeing: boolean - Er vesenet flyvende?
	 */
	public boolean isFlying() {
		return isFlying;
	}

	/**
	 * Bestemmer om vesenet flyr eller ikke.
	 * 
	 * @param isFlying
	 *            : boolean - ny status om veset er flyvende eller ikke.
	 */
	public void setIsFlying(boolean isFlying) {
		this.isFlying = isFlying;
	}

	/**
	 * Oppdaterer animasjonen for dette vesenet ved � sjekke hastigheter og
	 * status.
	 * 
	 * @param elapsedTime
	 *            : long - Tiden brukt siden forrige gjennoml�p av gameLoop().
	 */
	public void update(long elapsedTime) {
		Animation newAnim = currAnim;
		if (this instanceof Player) {
			if (getState() == STATE_ALIVE) {
				// Sjekker om dyret hopper og om det hopper til h�yre eller
				// venstre
				if (!onGround && (currAnim == left || currAnim == jump || currAnim == idleLeft)) {
					newAnim = jump;
				} else if (!onGround && (currAnim == right || currAnim == jumpR) || currAnim == idleLeft) {
					newAnim = jumpR;
				} else

				// Sjekker om dyret springer eller st�r stille, venstre eller
				// h�yre
				if (getDX() < 0) {
					newAnim = left;
				} else if (getDX() > 0) {
					newAnim = right;
				}

				// Sjekker om spilleren st�r stille og i hvilken retning
				if (getDX() == 0 && (currAnim == right || currAnim == jumpR || currAnim == idleRight)) {
					newAnim = idleRight;
				} else if (getDX() == 0 && (currAnim == left || currAnim == jump || currAnim == idleLeft)) {
					newAnim = idleLeft;
				}

			}

			// Sjekker om spilleren er d�d
			if (state == STATE_DYING && (currAnim == left || currAnim == jump || currAnim == idleLeft)) {
				newAnim = deadRight;
			} else if (state == STATE_DYING && (currAnim == right || currAnim == jumpR || currAnim == idleRight)) {
				newAnim = deadLeft;
			}

			stateTime += elapsedTime;
			if (state == STATE_DYING && stateTime >= DIE_TIME) {
				setState(STATE_DEAD);
			}

		} else if (this instanceof Creature) {
			// Sjekker om vesenet g�r mot h�yre eller venstre.
			if (getDX() < 0) {
				newAnim = left;
			} else if (getDX() > 0) {
				newAnim = right;
			}

			// Sjekker om vesenet er d�d eller d�ende.
			if (getState() == STATE_DYING && creep_died == 0) {
				if (currAnim == right)
					newAnim = deadLeft;
				else
					newAnim = deadRight;
				creep_died = System.currentTimeMillis();
			} else if (getState() == STATE_DYING && System.currentTimeMillis() - creep_died > 1000) {
				setState(STATE_DEAD);
			}
		}

		// Hvis animasjonen er blitt oppdatert, sett n�v�rende til den nye.
		if (currAnim != newAnim) {
			currAnim = newAnim;
			currAnim.start();
		} else {
			// Hvis ikke, oppdater n�v�rende.
			currAnim.update(elapsedTime);
		}
	}

	/**
	 * Metode som f�r vesenet til � snu n�r det kolliderer med en blokk i
	 * x-retning.
	 */
	public void collideHorizontal() {
		setDX(-getDX());
	}

	/**
	 * Metode som setter vesenets DY til 0 n�r det kolliderer med en blokk i
	 * Y-retning.
	 */
	public void collideVertical() {
		setDY(0);
	}

	/**
	 * Sjekker om vesenets er ud�delig eller ikke.
	 * 
	 * @return isImmortal: boolean - Er dyret ud�delig.
	 */
	public boolean isImmortal() {
		return isImmortal;
	}

	/**
	 * Setter vesenets ud�delighetsstatus.
	 * 
	 * @param isImmortal
	 *            : boolean - Ny ud�delighetssatus.
	 */
	public void setImmortal(boolean isImmortal) {
		this.isImmortal = isImmortal;
	}
}