package mack.entities.items;

import mack.entities.Animation;

/**
 * 
 * Klassen arver klassen Item og fungerer som en oppgradering som spilleren kan
 * plukke opp for å springe raskere i en kort periode.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
 * 
 */
public class SpeedUpgrade extends Item {

	/**
	 * Konstruktør som tar Animation som parameter
	 * 
	 * @param currAnim
	 *            : Animation - Animasjonen som kjøres i det aktuelle
	 *            tidspunktet
	 */
	public SpeedUpgrade(Animation currAnim) {
		super(currAnim);
	}
}