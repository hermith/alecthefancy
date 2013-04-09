package mack.menu;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;

import mack.highscore.Score;

/**
 * Dette er en klasse som h�nderer tegning av hovedmenyen p� et canvas gitt av
 * GameManager og DisplayManager.
 *
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 */
public class MenuRenderer {
    // Definerer bilder
    private Image titlescreen, background, start, highscores, options, exit, start_sel, highscores_sel, options_sel,
            exit_sel;

    // Definerer menyvalg
    public static final int MENU_START_GAME = 0;
    public static final int MENU_HIGHSCORES = 1;
    public static final int MENU_OPTIONS = 2;
    public static final int MENU_EXIT = 3;

    // Definerer andre verdier
    private int menu_highlight;
    private float menuCounter;

    /**
     * Initialiserer verdier og laster inn bilder.
     */
    public MenuRenderer() {
        menu_highlight = MENU_START_GAME;
        menuCounter = 0;
        loadImages();
    }

    /**
     * Metode som setter menu_highlight-verdien.
     *
     * @param toSet : int - Bestemmer hvilket menyvalg som skal highlightes
     */
    public void setHighlight(int toSet) {
        this.menu_highlight = toSet;
    }

    /**
     * Metode som returnerer menu_highlight-variabelen.
     *
     * @return menu_highlight: int - Hvilket menyvalg som skal highlightes
     */
    public int getHighlight() {
        return menu_highlight;
    }

    /**
     * Laster inn bildene til hovedmenyen
     */
    private void loadImages() {
        titlescreen = new ImageIcon("images/titlescreen.png").getImage();
        background = new ImageIcon("images/cloudybg.png").getImage();
        start = new ImageIcon("images/b_start.png").getImage();
        start_sel = new ImageIcon("images/b_start_sel.png").getImage();
        highscores = new ImageIcon("images/b_highscores.png").getImage();
        highscores_sel = new ImageIcon("images/b_highscores_sel.png").getImage();
        options = new ImageIcon("images/b_options.png").getImage();
        options_sel = new ImageIcon("images/b_options_sel.png").getImage();
        exit = new ImageIcon("images/b_exit.png").getImage();
        exit_sel = new ImageIcon("images/b_exit_sel.png").getImage();

    }

    /**
     * Tegner hovedmenyen
     *
     * @param g          : Graphics2D - Grafikken tilh�rende hovedmenyen
     * @param scrnWidth  : int - Skjermbredde
     * @param scrnHeight : int - Skjermh�yde
     */
    public void draw(Graphics2D g, int scrnWidth, int scrnHeight, Score[] scores) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB); // Setter

        // Scrollende bakgrunn
        int bgW = background.getWidth(null);
        int bgX = Math.round(menuCounter);

        g.drawImage(background, bgX, 0, null);
        g.drawImage(background, bgW + bgX, 0, null);
        if (-bgX >= bgW) {
            menuCounter = 0;
        }
        menuCounter -= 0.5f; // Setter hastigheten p� scrollende bakgrunn

        // Tegner tittellogo og nedre tekst
        g.drawImage(titlescreen, 0, 0, null);

        if (menu_highlight == MENU_START_GAME) {
            g.drawImage(start_sel, 110, 300, null);
            g.drawImage(highscores, 320, 300, null);
            g.drawImage(options, 530, 300, null);
            g.drawImage(exit, 735, 300, null);
        }
        if (menu_highlight == MENU_HIGHSCORES) {
            g.drawImage(start, 110, 300, null);
            g.drawImage(highscores_sel, 320, 300, null);
            g.drawImage(options, 530, 300, null);
            g.drawImage(exit, 735, 300, null);
        }
        if (menu_highlight == MENU_OPTIONS) {
            g.drawImage(start, 110, 300, null);
            g.drawImage(highscores, 320, 300, null);
            g.drawImage(options_sel, 530, 300, null);
            g.drawImage(exit, 735, 300, null);
        }
        if (menu_highlight == MENU_EXIT) {
            g.drawImage(start, 110, 300, null);
            g.drawImage(highscores, 320, 300, null);
            g.drawImage(options, 530, 300, null);
            g.drawImage(exit_sel, 735, 300, null);
        }

    }

}