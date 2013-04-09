package mack.entities.items;

import mack.entities.Animation;

/**
 * 
 * Klassen arver klassen Item og fungerer som en belønning som spilleren kan
 * plukke opp for å få ekstra poeng i spillet.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
 * 
 */
public class Pearl extends Item {

	/**
	 * Konstruktør som tar Animation som parameter
	 * 
	 * @param currAnim
	 *            : Animation - Animasjonen som kjøres i det aktuelle
	 *            tidspunktet
	 */
	public Pearl(Animation currAnim) {
		super(currAnim);
	}
}