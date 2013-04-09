package mack.menu;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import mack.highscore.Score;

/**
 * Dette er en klasse som tegner en undermeny for å se gjennom de 10 beste
 * gjennomløpene av spillet. En såkalt highscore-meny.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
 * 
 */
public class HighscoreRenderer {
	Image background;
	Image title;
	float menuCounter;

	/**
	 * Konstruktør som genererer highscoremenyen med tilhørende bakgrunn og
	 * tittel
	 */
	public HighscoreRenderer() {
		background = new ImageIcon("images/cloudybg.png").getImage();
		title = new ImageIcon("images/title_highscores.png").getImage();
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
	 * @param scores
	 *            : Score-tabell - Tabell med de aktuelle navnene og tilhørende
	 *            poengsum
	 */
	public void draw(Graphics2D g, int scrnWidth, int scrnHeight, Score[] scores) {
		// Scrollende bakgrunn
		int bgW = background.getWidth(null);
		int bgX = Math.round(menuCounter);
		int yOffset = 110;
		int yIncrement = 23;

		g.drawImage(background, bgX, 0, null);
		g.drawImage(background, bgW + bgX, 0, null);
		if (-bgX >= bgW) {
			menuCounter = 0;
		}
		menuCounter -= 0.5f; // Setter hastigheten på scrollende bakgrunn

		g.drawImage(title, 420, 50, null);

		// Tegner top 10 liste med highscores
		String[] scoresToScreen = convertToStringArray(scores);
		g.setFont(new Font("Lucida Console", Font.BOLD, 15));
		for (int i = 0, y = 0; i < scoresToScreen.length; i++, y += yIncrement) {
			if (scoresToScreen[i] != null) {
				if (i <= 19) {
					g.drawString(scoresToScreen[i], 100, yOffset + y);
				}
			}
		}
		for (int i = 20, y = 0; i < scoresToScreen.length; i++, y += yIncrement) {
			if (scoresToScreen[i] != null) {
				if (i <= 39) {
					g.drawString(scoresToScreen[i], 400, yOffset + y);
				}
			}
		}

		for (int i = 40, y = 0; i < scoresToScreen.length; i++, y += yIncrement) {
			if (scoresToScreen[i] != null) {
				if (i <= 59) {
					g.drawString(scoresToScreen[i], 700, yOffset + y);
				}
			}
		}

	}

	/**
	 * Skal ikke brukes ved ferdig highscoreRenderer.
	 * 
	 * @param scores
	 *            : Score[] - Tabell som konverteres fra type Score til String
	 * @return scores: String[] - Tegnbar liste med poengsummer.
	 */
	private String[] convertToStringArray(Score[] scores) {
		String[] s = new String[scores.length];
		for (int i = 0; i < s.length; i++) {
			String OK;
			if (scores[i].getMD5())
				OK = " ";
			else
				OK = "x";

			String u;
			String spaces = "";
			String negSpaces = "";
			if (i < 9)
				u = (i + 1) + " ";
			else
				u = "" + (i + 1);

			if (scores[i].getScore() < 100000)
				spaces += " ";
			if (scores[i].getScore() < 10000)
				spaces += " ";
			if (scores[i].getScore() < 1000)
				spaces += " ";
			if (scores[i].getScore() < 100)
				spaces += " ";
			if (scores[i].getScore() < 10)
				spaces += " ";
			if (scores[i].getScore() < 0)
				spaces += " ";

			if (scores[i].getScore() > 0) {
				s[i] = OK + u + spaces + scores[i].getScore() + " : " + scores[i].getName();
			}

			if (scores[i].getScore() < -1000 && scores[i].getScore() > -10000)
				negSpaces += " ";
			if (scores[i].getScore() < -100 && scores[i].getScore() > -1000)
				negSpaces += "  ";
			if (scores[i].getScore() < -10 && scores[i].getScore() > -100)
				negSpaces += "   ";

			if (scores[i].getScore() < 0) {
				s[i] = OK + u + negSpaces + scores[i].getScore() + " : " + scores[i].getName();
			}

		}
		return s;
	}
}