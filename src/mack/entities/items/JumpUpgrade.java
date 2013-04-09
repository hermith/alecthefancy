package mack.entities.items;

import mack.entities.Animation;

/**
 * 
 * Klassen arver klassen Item og fungerer som en oppgradering som spilleren kan
 * plukke opp for � kunne dobbel-hoppe i en kort periode.
 * 
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 * 
 */
public class JumpUpgrade extends Item {

	/**
	 * Konstrukt�r som tar Animation som parameter
	 * 
	 * @param currAnim
	 *            : Animation - Animasjonen som kj�res i det aktuelle
	 *            tidspunktet
	 */
	public JumpUpgrade(Animation currAnim) {
		super(currAnim);
	}
}
