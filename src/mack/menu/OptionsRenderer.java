package mack.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import mack.GameManager;

/**
 * Dette er en klasse som tegner en undermeny for å se gjennom de 10 beste
 * gjennomløpene av spillet. En såkalt highscore-meny.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
 * 
 */
public class OptionsRenderer {

	// Definerer menyvalg
	public static final int MENU_SOUND = 0;
	public static final int MENU_NAME = 1;
	public static final int MENU_BACK = 2;

	public static final String SOUND_ENABLED = "sound_enabled";

	private final Color cSel = Color.BLUE;
	private final Color cNot = Color.BLACK;

	private int menu_highlight;
	private boolean soundEnabled;
	String sSound;

	// Definerer globale verdier til navninput
	Preferences prefs;
	JTextField name;
	JDialog dia;
	Input input;

	// For scrollende bakgrunnsbilde
	Image background;
	Image title;
	float menuCounter;

	// Brukes til navnesjekk for å hindre at vinduet blir tegnet mer enn 1 gang
	boolean hasBeenOpened = false;
	boolean runOnce = false;

	/**
	 * Genererer Options-menyen med tilhørende lyd og bilder
	 */
	public OptionsRenderer() {
		menu_highlight = MENU_SOUND;

		prefs = Preferences.userRoot().node("mack.GameManager");

		// Laster inn standardverdier
		soundEnabled = prefs.getBoolean(SOUND_ENABLED, true);
		if (soundEnabled) {
			sSound = "Sound: enabled";
		} else
			sSound = "Sound: disabled";

		input = new Input();
		dia = new JDialog();
		background = new ImageIcon("images/cloudybg.png").getImage();
		title = new ImageIcon("images/title_options.png").getImage();
		menuCounter = 0;
	}

	/**
	 * 
	 * @param g
	 *            : Graphics2D - Grafikk for scrollende bakgrunn
	 * @param scrnWidth
	 *            : int - Skjermbredde
	 * @param scrnHeight
	 *            : int - Skjermhøyde
	 */
	public void draw(Graphics2D g, int scrnWidth, int scrnHeight) {
		// Scrollende bakgrunn
		int bgW = background.getWidth(null);
		int bgX = Math.round(menuCounter);

		g.drawImage(background, bgX, 0, null);
		g.drawImage(background, bgW + bgX, 0, null);
		if (-bgX >= bgW) {
			menuCounter = 0;
		}
		menuCounter -= 0.5f; // Setter hastigheten på scrollende bakgrunn

		g.drawImage(title, 450, 50, null);

		// Tegner menyvalg
		String sName = "Change name: " + prefs.get(GameManager.PLAYER_NAME, "Player");
		g.setFont(new Font("Lucida Console", Font.BOLD, 20));

		if (menu_highlight == MENU_SOUND)
			g.setColor(cSel);
		else
			g.setColor(cNot);

		g.drawString(sSound, 100, 140);

		if (menu_highlight == MENU_NAME)
			g.setColor(cSel);
		else
			g.setColor(cNot);

		g.drawString(sName, 100, 160);
	}

	/**
	 * Metode for navn-endring. Dette er navnet som vises i highscore-lista.
	 */
	public void initName() {
		dia.setSize(200, 50);
		dia.setTitle("Input plz");
		dia.setLocationRelativeTo(null);
		dia.setUndecorated(true);
		dia.setLayout(new GridLayout(2, 1));
		dia.setBackground(new Color(0f, 0f, 0f, 0f));
		name = new JTextField();
		name.addKeyListener(input);
		name.requestFocus();
		dia.addKeyListener(input);
		dia.add(new JLabel("What is your name?"));
		dia.add(name);
		// dia.setModal(true);
		dia.setVisible(true);
		hasBeenOpened = true;
		runOnce = true;

		name.setText(prefs.get(GameManager.PLAYER_NAME, "Player"));
	}

	/**
	 * Lager tekstboks for endring av spillernavn. Legger generisk spillernavn i
	 * tekstboksen.
	 */
	public void showNameInput() {
		if (!dia.isVisible() && !runOnce && !hasBeenOpened) {
			initName();
		} else if (!hasBeenOpened) {
			name.setText(prefs.get(GameManager.PLAYER_NAME, "Player"));
			dia.setVisible(true);
			hasBeenOpened = true;
		}
	}

	public void exitOptions() {
		hasBeenOpened = false;
	}

	public void setSound() {
		try {
			prefs.putBoolean(SOUND_ENABLED, soundEnabled);
			prefs.flush();
		} catch (BackingStoreException e) {
			System.out.println(e.getMessage());
		}
	}

	public void setName() {
		try {
			prefs.put(GameManager.PLAYER_NAME, name.getText());
			prefs.flush();
		} catch (BackingStoreException e) {
			System.out.println(e.getMessage());
		}
	}

	public void select() {
		switch (menu_highlight) {
		case MENU_NAME:
			showNameInput();
			break;
		case MENU_SOUND:
			if (soundEnabled) {
				soundEnabled = false;
				sSound = "Sound: disabled";
			} else {
				soundEnabled = true;
				sSound = "Sound: enabled";
			}
			setSound();
			break;
		}
	}

	/**
	 * Metode som setter menu_highlight-verdien.
	 * 
	 * @param toSet
	 *            : int
	 */
	public void setHighlight(int toSet) {
		this.menu_highlight = toSet;
	}

	/**
	 * Metode som returnerer menu_highlight-variabelen.
	 * 
	 * @return menu_highlight: int
	 */
	public int getHighlight() {
		return menu_highlight;
	}

	private class Input implements KeyListener {
		@Override
		public void keyPressed(KeyEvent arg0) {
			int keyCode = arg0.getKeyCode();
			if (keyCode == KeyEvent.VK_ESCAPE) {
				dia.setVisible(false);
				exitOptions();
			} else if (keyCode == KeyEvent.VK_ENTER) {
				setName();
				dia.setVisible(false);
				exitOptions();
			}

		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// Ignorer
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// Ignorer
		}
	}
}