package mack.entities;

import java.awt.Image;
import java.util.ArrayList;

/**
 * 
 * Denne klassen håndterer bilder som er brukt i bevegelige enheter som spiller,
 * fiender og ting. Den vil animere objektene som er på skjermen.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
 * 
 */
public class Animation {

	private ArrayList<AnimationFrame> frames;
	private int currentFrame;
	private long totalDuration;
	private long animationTime;

	/**
	 * Oppretter en tom ArrayList uten noe bilder
	 */
	public Animation() {
		this.frames = new ArrayList<AnimationFrame>(0);
	}

	/**
	 * Oppretter en ArrayList som inneholder bildene til en entitet.
	 * 
	 * @param frames
	 *            : ArrayList<AnimationFrame> - Tabellen som inneholder bildene
	 *            tilhørende animasjon
	 * @param totDuration
	 *            : long - Den totale varigheten av animasjonen
	 */
	public Animation(ArrayList<AnimationFrame> frames, long totDuration) {
		this.frames = frames;
		this.totalDuration = totDuration;
	}

	/**
	 * Kloner dette objektet.
	 */
	public Object clone() {
		return new Animation(frames, totalDuration);
	}

	/**
	 * Legger til et bilde i animasjonsrekkefølgen.
	 * 
	 * @param image
	 *            : Image - Henter bilde til bruk i animasjonsrekkefølge
	 * @param duration
	 *            : long - Varigheten på animasjonen
	 */
	public synchronized void addFrame(Image image, long duration) {
		totalDuration += duration;
		frames.add(new AnimationFrame(image, totalDuration));
	}

	/**
	 * Starter animasjonen.
	 */
	public synchronized void start() {
		animationTime = 0;
		currentFrame = 0;
	}

	/**
	 * Oppdaterrer animasjonen. Dette er selve animeringsprossesen.
	 * 
	 * @param elapsedTime
	 *            : long - Tidspunkt for oppdatering av animasjonen
	 */
	public synchronized void update(long elapsedTime) {
		if (frames.size() > 1) {
			animationTime += elapsedTime;

			if (animationTime >= totalDuration) {
				animationTime = animationTime % totalDuration;
				currentFrame = 0;
			}

			while (animationTime > getFrame(currentFrame).endTime) {
				currentFrame++;
			}
		}
	}

	/**
	 * Returnerer bildet. Om animasjonen er tom returneres det null.
	 * 
	 * @return i: Image - Henter animasjonsbilde
	 */
	public synchronized Image getImage() {
		if (frames.size() == 0) {
			return null;
		} else {
			return getFrame(currentFrame).image;
		}
	}

	/**
	 * Returnerer et spesifikt bilde i animasjonen.
	 * 
	 * @param i
	 *            : int - Bestemmer hvilket bilde i animasjonen som skal sendes
	 * @return frame: AnimationFrame
	 */
	private AnimationFrame getFrame(int i) {
		return (AnimationFrame) frames.get(i);
	}

	/**
	 * Objekt for å ta vare på et stillbilde og hvor i animasjonen bildet
	 * ligger.
	 */
	private class AnimationFrame {
		Image image;
		long endTime;

		/**
		 * Tar inn bildet og tidspunktet i animasjonen bildet er på.
		 * 
		 * @param image
		 *            : Image - Henter inn stillbilder fra animasjon
		 * @param endTime
		 *            : long - Henter inn tidspunkt for animasjonsfremgang
		 */
		public AnimationFrame(Image image, long endTime) {
			this.image = image;
			this.endTime = endTime;
		}
	}

}