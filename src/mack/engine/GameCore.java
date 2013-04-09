package mack.engine;

import java.awt.Graphics2D;

import mack.display.DisplayManager;

/**
 * 
 * Dette er kj�rneklassen til hele spillet. Klassen er abstrakt og m� derfor
 * arves og utvides for � f� en fungerende spillmotor. Klassen h�ndterer
 * starting, grunnleggende initiering, oppdatering og tegning av et tomt
 * "canvas".
 * 
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 * 
 */
public abstract class GameCore {
	protected boolean isRunning;
	protected boolean isPaused;
	protected DisplayManager dm;

	protected abstract void draw(Graphics2D g);

	protected abstract void update(long elapsedTime);

	/**
	 * En metode som starter spillet. Metoden kaller videre p� init() og
	 * gameLoop().
	 */
	public void start() {
		try {
			init();
			gameLoop();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	/**
	 * En metode som avslutter spillet ved � sette isRunning til false.
	 */
	protected void stop() {
		isRunning = false;
	}

	/**
	 * En metode som h�ndterer grunnleggende initiering av spillets mange
	 * byggeklosser. M� utvides for � fungere optimalt.
	 */
	protected void init() {
		dm = new DisplayManager("Alec the Fancy");
		isRunning = true;
	}

	/**
	 * En metode som h�ndterer kall p� tegning og oppdatering s� lenge isRunning
	 * er true.
	 */
	private void gameLoop() {
		long currTime = System.currentTimeMillis();
		while (isRunning) {
			long elapsedTime = System.currentTimeMillis() - currTime;
			currTime += elapsedTime;

			update(elapsedTime);
			Graphics2D g = dm.getGraphics();
			draw(g);
			g.dispose();
			dm.update();

			long sleepTime = 0;
			if (elapsedTime <= 10) {
				sleepTime = 10 - elapsedTime;
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * En metode som returnerer isRunning variabelen.
	 * 
	 * @return isRunning: boolean - Kj�rer spillet eller ei.
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * En metode som retunerer isPaused variabelen.
	 * 
	 * @return isPaused: boolean - Er spillet i pause.
	 */
	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * En metode som setter isPaused variabelen.
	 * 
	 * @param isPaused
	 *            : boolean - Setter spillets pause-status.
	 */
	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	/**
	 * En metode som returnerer spillets DisplayManager.
	 * 
	 * @return dm: DisplayManager - Innheolder spillets canvas.
	 */
	public DisplayManager getDm() {
		return dm;
	}
}