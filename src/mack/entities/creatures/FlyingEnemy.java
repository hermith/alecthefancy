package mack.entities.creatures;

import mack.entities.Animation;

/**
 * 
 * Klassen arver klassen Creature og h�ndterer en fiende som flyver.
 * 
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 * 
 */
public class FlyingEnemy extends Creature {

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
	public FlyingEnemy(Animation left, Animation right, Animation deadLeft, Animation deadRight) {
		super(left, right, deadLeft, deadRight);
		setIsFlying(true);
	}
}