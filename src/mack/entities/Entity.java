package mack.entities;

import java.awt.Image;

/**
 * Dette er superklassen til alle enheter i spillet. Klassen h�nderer fart,
 * posisjon, animasjoner og st�relse til enheten.
 * 
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 * 
 */
public abstract class Entity {

	protected Animation currAnim;

	private float x;
	private float y;
	private float dx;
	private float dy;

	public Entity(Animation currAnim) {
		this.currAnim = currAnim;
	}

	/**
	 * Henter enhetens x-posisjon (pixel)
	 */
	public float getX() {
		return x;
	}

	/**
	 * Henter enhetens y-posisjon (pixel)
	 */
	public float getY() {
		return y;
	}

	/**
	 * Setter enhetens x-posisjon (pixel)
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Setter enhetens y-posisjon (pixel)
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Henter enhetens horisontale hastighet i pixel per millisekund
	 */
	public float getDX() {
		return dx;
	}

	/**
	 * Setter enhetens horisontale hastighet i pixel per millisekund
	 */
	public void setDX(float dx) {
		this.dx = dx;
	}

	/**
	 * Henter enhetens vertikale hastighet i pixel per millisekund
	 */
	public float getDY() {
		return dy;
	}

	/**
	 * Setter enhetens vertikale hastighet i pixel per millisekund
	 */
	public void setDY(float dy) {
		this.dy = dy;
	}

	/**
	 * Henter enhetens bredde, basert p� st�rrelsen til n�v�rende bilde
	 */
	public int getWidth() {
		return currAnim.getImage().getWidth(null);
	}

	/**
	 * Henter enhetens h�yde, basert p� st�rrelsen til n�v�rende bilde
	 */
	public int getHeight() {
		return currAnim.getImage().getHeight(null);
	}

	/**
	 * Metode som returnerer det n�v�rende bildet i den n�v�rende animasjonen.
	 * 
	 * @return currAnim.getImage: Image - Henter det aktuelle bildet i den
	 *         aktuelle animasjonen
	 */
	public Image getImage() {
		return currAnim.getImage();
	}

	/**
	 * Metode som oppdaterer enhetens animasjon.
	 * 
	 * @param elapsedTime
	 *            : long - Tid siden animasjonen startet
	 */
	public void update(long elapsedTime) {
		currAnim.update(elapsedTime);
	}

	/**
	 * Klone-metode, men alle objekt skal klones forskjellig.
	 */
	public abstract Object clone();

	/**
	 * Klone-metode for kloning av items.
	 */
	public abstract Object cloneOther();
}