package mack.entities.items;

import java.lang.reflect.Constructor;

import mack.entities.Animation;
import mack.entities.Entity;

/**
 * 
 * Dette er superklassen til alle ting som kan plukkes opp i spillet. Klassen
 * arver Entity og h�ndterer.....
 * 
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 * 
 */
public abstract class Item extends Entity {
	Animation anim;

	/**
	 * Konstrukt�r som tar Animation som parameter
	 * 
	 * @param currAnim
	 *            : Animation - Animasjonen som kj�res i det aktuelle
	 *            tidspunktet
	 */
	public Item(Animation currAnim) {
		super(currAnim);
		this.anim = currAnim;

	}

	@Override
	public Object cloneOther() {
		Constructor<?> constructor = getClass().getConstructors()[0];
		try {
			return constructor.newInstance(new Object[] { (Animation) anim.clone() });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object clone() {
		return null;
	}
}
