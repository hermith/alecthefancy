package mack.display;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferStrategy;
import java.awt.image.MemoryImageSource;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * 
 * Dette er klassen som håndterer rammen og canvaset som grafikken kan tegnes
 * på.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
 * 
 */
public class DisplayManager {

	public static final int WIDTH = 1024;
	public static final int HEIGHT = 576;
	private JFrame frame;

	/**
	 * Konstruktøren. Initierer rammen og canvaset som det skal tegnes i.
	 * 
	 * @param gameName
	 *            : String - Tittel på JFrame
	 * 
	 */
	public DisplayManager(String gameName) {
		frame = new JFrame(gameName);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setIgnoreRepaint(true);
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setCursor(getInvisibleCursor());
		frame.setIconImage(new ImageIcon("images/ico.ico").getImage());

		// Oppretter en buffer for å hindre "tearing" i grafikken.
		try {
			EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					frame.createBufferStrategy(2);
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * En metode som returnerer rammen som spillet tegnes i.
	 * 
	 * @return frame: JFrame - Hovedvinduet
	 */
	public JFrame getWindow() {
		return frame;
	}

	/**
	 * En metode som returnerer canvaset som grafikken kan tegnes på.
	 * 
	 * @return g: Graphics2D - Grafikken som blir tegnet
	 */
	public Graphics2D getGraphics() {
		Window window = getWindow();
		if (window != null) {
			BufferStrategy strategy = window.getBufferStrategy();
			return (Graphics2D) strategy.getDrawGraphics();
		}
		return null;
	}

	/**
	 * En metode som viser grafikken som er blitt tegnet på canvaset.
	 */
	public void update() {
		Window window = getWindow();
		if (window != null) {
			BufferStrategy strategy = window.getBufferStrategy();
			if (!strategy.contentsLost()) {
				strategy.show();
			}
		}
		Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * Metode som gjør at musen blir usynelig når det befinner seg over
	 * spillvinduet.
	 * 
	 * @return c: Cursor - En tom peker på 16x16 piksler.
	 */
	public Cursor getInvisibleCursor() {
		int[] pixels = new int[16 * 16];
		Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
		Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0),
				"invisibleCursor");
		return transparentCursor;
	}
}
