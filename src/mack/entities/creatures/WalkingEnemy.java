package mack.entities.creatures;

import mack.entities.Animation;

/**
 * 
 * Klassen arver klassen Creature og håndterer en fiende som går.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
 * 
 */
public class WalkingEnemy extends Creature {

	/**
	 * Lager et nytt vesen med spesifikke animasjoner.
	 * 
	 * @param left
	 *            : Animation - Animasjonen som skal vises når vesenet går mot
	 *            venstre.
	 * @param right
	 *            : Animation - Animasjonen som skal vises når vesenet går mot
	 *            høyre.
	 * @param deadLeft
	 *            : Animation - Animasjonen som skal vises når vesenet er død i
	 *            retning venstre.
	 * @param deadRight
	 *            : Animation - Animasjonen som skal vises når vesenet er død i
	 *            retning høyre.
	 */
	public WalkingEnemy(Animation left, Animation right, Animation deadLeft, Animation deadRight) {
		super(left, right, deadLeft, deadRight);
	}
}