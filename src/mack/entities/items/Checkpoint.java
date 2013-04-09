package mack.entities.items;

import mack.entities.Animation;

/**
 * 
 * Klassen arver klassen Item og fungerer som en oppgradering som spilleren kan
 * plukke opp for å bli udødelig i en kort periode.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
 * 
 */

public class Checkpoint extends Item {

	/**
	 * Konstruktør som tar Animation som parameter
	 * 
	 * @param currAnim
	 *            : Animation - Animasjonen som kjøres i det aktuelle
	 *            tidspunktet
	 */
	public Checkpoint(Animation currAnim) {
		super(currAnim);
	}
}