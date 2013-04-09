package mack.entities.items;

import mack.entities.Animation;

/**
 * 
 * Klassen arver klassen Item og fungerer som en bel�nning som spilleren kan
 * plukke opp for � f� ekstra poeng i spillet.
 * 
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 * 
 */
public class Pearl extends Item {

	/**
	 * Konstrukt�r som tar Animation som parameter
	 * 
	 * @param currAnim
	 *            : Animation - Animasjonen som kj�res i det aktuelle
	 *            tidspunktet
	 */
	public Pearl(Animation currAnim) {
		super(currAnim);
	}
}