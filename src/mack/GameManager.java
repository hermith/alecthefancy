package mack;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Window;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;

import mack.display.DisplayManager;
import mack.engine.GameCore;
import mack.entities.Entity;
import mack.entities.creatures.Creature;
import mack.entities.creatures.FlyingEnemy;
import mack.entities.creatures.JumpingEnemy;
import mack.entities.creatures.Player;
import mack.entities.creatures.WalkingEnemy;
import mack.entities.items.Checkpoint;
import mack.entities.items.InvulnerabilityUpgrade;
import mack.entities.items.Item;
import mack.entities.items.JumpUpgrade;
import mack.entities.items.Pearl;
import mack.entities.items.SpeedUpgrade;
import mack.highscore.Highscore;
import mack.highscore.Score;
import mack.level.MapRenderer;
import mack.level.TileMap;
import mack.listeners.GameListener;
import mack.listeners.MenuListener;
import mack.menu.HighscoreRenderer;
import mack.menu.MenuRenderer;
import mack.menu.OptionsRenderer;
import mack.sound.SoundPlayer;

/**
 * Denne klassen arver GameCore og h�ndterer alt som har med spillets
 * kjernefunksjoner � gj�re. Klassen har metoder som h�ndterer oppdatering,
 * tegning, sjekk av input og initiering av spillet komponenter.
 *
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 */
public class GameManager extends GameCore {

    // Komponenter
    private GameListener inp;
    private MenuListener inpMenu;
    private MapRenderer renderer;
    private MenuRenderer mRenderer;
    private HighscoreRenderer hRenderer;
    private OptionsRenderer oRenderer;
    private TileMap map;
    private Highscore highscore;
    private SoundPlayer sound;
    private Preferences prefs;

    // Konstanter
    public static final float ACCEL = 0.001f;
    public static final float GRAVITY = 0.0015f;
    public static final int MAX_RIGHT = 560;
    public static final int MAX_LEFT = 440;
    public static final int START_X = 27;
    public static final int START_Y = 14;

    public static final int GAME_RUNNING = 1;
    public static final int GAME_PAUSED = 4;
    public static final int GAME_MENU = 0;
    public static final int GAME_HIGHSCORE = 2;
    public static final int GAME_OPTIONS = 3;
    public static final int PLAYER_DEAD = -2;

    public static final int JUMP_DURATION = 10000;
    public static final int INVUL_DURATION = 10000;
    public static final int SPEED_DURATION = 10000;

    public static final String PLAYER_NAME = "player_name";

    // Poengvariabler
    private int points = 0;
    private int distance = 0;
    private long time = 0;
    private long startTime = 0;
    private int bonusPoints = 0;
    private String[] variables = new String[3];

    // Bevegelsesvariabler
    private long jumpStartTime;
    private boolean canWallJump;

    // Menyvariabler
    private final int MENU_LOW = 0;
    private final int MENU_HIGH = 3;
    private boolean hasPressed = false;

    // PowerUp variabler
    private float jumpMultiplier = 1.0f;
    private float speedMultiplier = 1.0f;
    private boolean invulnerable = false;
    private long jumpUpgStart = 0;
    private long speedUpgStart = 0;
    private long invulUpgStart = 0;
    private int[] activeBuffs = {-1, -1, -1};
    private Point checkpoint = new Point();

    // Statusvariables
    private int game_state = GAME_MENU;
    private long pauseStartTime = 0;
    private long pauseEndTime = 0;
    private long playerDieTime = 0;
    private long playerRestartTime = 0;
    private long totalTimePaused = 0;

    // Highscore variabler
    String trueMD5 = "e71e103f5f1076261d21d304a0a0a96d";

    @Override
    protected void init() {
        super.init();

        // Set up input manager
        inp = new GameListener();
        inpMenu = new MenuListener();
        Window window = getDm().getWindow();
        window.addKeyListener(inpMenu);

        // Initialisererer andre rendererer
        mRenderer = new MenuRenderer();
        hRenderer = new HighscoreRenderer();
        oRenderer = new OptionsRenderer();

        // Oppretter lydtr�den
        sound = new SoundPlayer();
        sound.playSong();

        // Starter selve spillet og laster inn kartet
        initGame();
    }

    /**
     * Initierer alt som har med selve spillet � gj�re.
     */
    protected void initGame() {
        // Set up renderer
        renderer = new MapRenderer();
        renderer.setBackground(new ImageIcon("images/cloudybg.png").getImage(),
                new ImageIcon("images/mountain.png").getImage());

        // load tile map
        checkpoint.setLocation(START_X, START_Y);
        map = renderer.loadMap("main.map", checkpoint);

        // Initialiserer MD5 sjekkeren og tar f�rste sjekk
        highscore = new Highscore("maps/main.map", trueMD5);
    }

    /**
     * En metode som kaller p� draw-metodene i driverse tegneklasser alt
     * ettersom hvilken status spillet befinner. Metoden tegner grafikken og f�r
     * DisplayManager til � oppdaterer bildet.
     *
     * @param g : Graphics2D - Objektet som grafikken skal tegnes p� (canvas).
     */
    @Override
    protected void draw(Graphics2D g) {
        Score[] scores;

        switch (game_state) {
            case GAME_MENU:
                scores = highscore.getTopTenLocal();
                mRenderer.draw(g, DisplayManager.WIDTH, DisplayManager.HEIGHT, scores);
                oRenderer.exitOptions();
                break;
            case GAME_HIGHSCORE:
                scores = highscore.getScoreArray();
                hRenderer.draw(g, DisplayManager.WIDTH, DisplayManager.HEIGHT, scores);
                break;
            case GAME_OPTIONS:
                oRenderer.draw(g, DisplayManager.WIDTH, DisplayManager.HEIGHT);
                break;
            default:
                renderer.draw(g, map, DisplayManager.WIDTH, DisplayManager.HEIGHT, map.getOffsetX(), variables, game_state,
                        activeBuffs);
                break;
        }
    }

    /**
     * En metode som blir kalt p� i gameLoop-metode. Metoden kaller p� alle
     * andre metoder som brukes i oppdatering av spillet bortsett fra tegning av
     * grafikk.
     *
     * @param elapsedTime : long - Tiden som er g�tt siden forrige gjennoml�p av
     *                    gameLoop().
     */
    @Override
    protected void update(long elapsedTime) {
        Player player = (Player) map.getPlayer();

        // sjekker om spilleren er i livet.
        if (player.getState() == Player.STATE_DEAD) {
            game_state = PLAYER_DEAD;
        }

        // Sjekker input fra bruker
        if (game_state == GAME_MENU || game_state == GAME_HIGHSCORE || game_state == GAME_OPTIONS) {
            checkMenuInput();
        } else {
            checkInput(elapsedTime);
        }

        if (!isPaused()) {
            // Oppdaterer spiller
            updatePlayer(elapsedTime, player);

            // Oppdaterer andre dyr og power ups
            updateOtherEntities(elapsedTime);
        } else {
            updatePausedTime();
        }
    }

    /**
     * Starter spillet for f�rste gang og setter det til kj�rende. Viktig for �
     * f� timeren til � starte p� 0.
     */
    private void startGame() {
        restartGame();
        game_state = GAME_RUNNING;
        Window window = getDm().getWindow();
        window.removeKeyListener(inpMenu);
        window.addKeyListener(inp);
        highscore.reCheck();
        startTime = System.currentTimeMillis();
    }

    /**
     * Restarter spillet og resetter timeren og variabler.
     */
    private void restartGame() {
        game_state = GAME_RUNNING;
        checkpoint.setLocation(START_X, START_Y);
        map = renderer.loadMap("main.map", checkpoint);
        highscore.reCheck();
        resetVariables();
    }

    /**
     * Restarter spillet fra forrige checkpoint og resetter timeren og
     * variabler.
     */
    private void restartCheckpoint() {
        game_state = GAME_RUNNING;
        map = renderer.loadMap("main.map", checkpoint);
        highscore.reCheck();
        bonusPoints -= 500;
        removeUpgrades();
    }

    /**
     * Metode som setter spillet tilbake i meny-modus hvis 'esc' blir trykket
     * under spilling.
     */
    private void enterMenu() {
        highscore.saveLocal();
        inp.releaseAllKeys();
        inpMenu.releaseAllKeys();
        Window window = getDm().getWindow();
        window.removeKeyListener(inp);
        window.addKeyListener(inpMenu);
        game_state = GAME_MENU;
    }

    /**
     * Metode som oppdaterer total tid som er blitt brukt i pause stadiet. Denne
     * tiden blir brukt til � beregne totalt tid brukt spillende og deretter
     * antall poeng i updateVariables().
     */
    private void updatePausedTime() {
        if (pauseStartTime != 0 && !isPaused() && game_state != GAME_MENU) {
            pauseEndTime = System.currentTimeMillis();
            totalTimePaused += pauseEndTime - pauseStartTime;
            pauseEndTime = 0;
            pauseStartTime = 0;
        } else if (pauseStartTime == 0 && isPaused() && game_state != GAME_MENU) {
            pauseStartTime = System.currentTimeMillis();
        }
    }

    /**
     * En metode som oppdaterer andre dyr og powerups. Metoden kaller videre p�
     * metoder som sjekker alt fra lokasjoner, vekking, kollisjon og status.
     *
     * @param elapsedTime : long - Tiden som er g�tt siden forrige gjennoml�p av
     *                    gameLoop().
     */
    private void updateOtherEntities(long elapsedTime) {
        ArrayList<Entity> entities = map.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e instanceof Creature) {
                Creature c = (Creature) e;
                if (c.getState() == Creature.STATE_DEAD) {
                    entities.remove(c);
                } else {
                    int cP = Math.round(c.getX() - map.getOffsetX());
                    if (cP > -1024 && cP < 2048) {
                        c.wakeUp();
                    } else {
                        c.sleep();
                    }
                    updateCreature(c, elapsedTime);
                }
            }
            e.update(elapsedTime);
        }
    }

    /**
     * En metode som kaller p� de n�dvendige metodene for � oppdatere spilleren.
     *
     * @param elapsedTime : long - Tiden som er g�tt siden forrige gjennoml�p av
     *                    gameLoop().
     * @param player      : Player - Objektet som representerer spilleren.
     */
    private void updatePlayer(long elapsedTime, Player player) {
        if (player.isAlive()) {
            updateCreature(player, elapsedTime);
            updateVariables();
        }
        updatePausedTime();
        player.update(elapsedTime);
        checkUpgrades();
    }

    /**
     * En metode som oppdaterer et spesifikt vesen i spillet ved � sjekke
     * tilstanden og sette ny x og y posisjon basert p� fart.
     *
     * @param c           : Creature - Bevegelig vesen. Spiller eller fiende.
     * @param elapsedTime : long - Tiden som er g�tt siden forrige gjennoml�p av
     *                    gameLoop().
     */
    private void updateCreature(Creature c, long elapsedTime) {
        applyGravity(c, elapsedTime);

        // Finne ny X posisjon basert p� fart
        float dx = c.getDX();
        float oldX = c.getX();
        float newX = oldX + dx * elapsedTime;
        Point tileX = getTileCollision(c, newX, c.getY());

        // Finner ny X posisjon til spiller basert p� fart for kartet(offset)
        if (c instanceof Player) {
            if (tileX == null) {
                float offOldX = map.getOffsetX();
                float offNewX = offOldX + dx * elapsedTime;

                boolean lockedScroll = false;
                if (newX >= MAX_LEFT && newX <= MAX_RIGHT) {
                    c.setX(newX);
                } else if (!lockedScroll) {
                    map.setOffsetX(offNewX);
                }
                canWallJump = false;
            } else {
                // sjekker om spilleren kan hoppe opp etter veggen
                if (c.isOnGround()) {
                    canWallJump = false;
                } else {
                    canWallJump = true;
                }
                checkStuck(tileX, c);
                c.collideHorizontal();
            }
            // Gj�r det slik at et fall telles som et hopp og at spilleren
            // dermed kan hoppe opp etter veggen
            if (c.isOnGround()) {
                jumpStartTime = 0;
            } else if (jumpStartTime == 0) {
                jumpStartTime = System.currentTimeMillis();
            }
            checkPlayerCollision((Player) c, false);
        } else /* if (c instance of "Enemy") */ {
            if (tileX == null) {
                c.setX(newX);
                if (c instanceof WalkingEnemy) {
                    if (isNearEdge(c)) {
                        c.setDX(-c.getDX());
                    }
                }
            } else {
                checkStuck(tileX, c);
                c.collideHorizontal();
            }
        }

        // Finne ny Y posisjon basert p� fart
        float dy = c.getDY();
        float oldY = c.getY();
        float newY = oldY + dy * elapsedTime;
        Point tileY = getTileCollision(c, c.getX(), newY);
        if (tileY == null) {
            c.setY(newY);
        } else {
            c.collideVertical();
        }

        // Sjekker om spilleren er i stand til � drepe en fiende ved � sjekke om
        // spilleren har fart nedover
        if (c instanceof Player) {
            boolean canKill = (c.getDY() >= 0 && !c.isOnGround());
            checkPlayerCollision((Player) c, canKill);
        }
    }

    /**
     * En metode som oppdaterer ant. poeng, tid brukt og distanse som spilleren
     * har beveget seg siden start.
     */
    private void updateVariables() {
        time = (System.currentTimeMillis() - startTime - totalTimePaused);
        int secondsTotal = (int) time / 1000;
        distance = Math.round(map.getOffsetX() / MapRenderer.TILE_SIZE);
        points = (int) Math.round((distance * 3.8) - (secondsTotal * 3.8)) + bonusPoints;

        // Konvertere 'time' fra sekunder til minutter og sekunder
        int minutes = (int) secondsTotal / 60;
        int seconds = (int) secondsTotal % 60;
        int mili = (int) time % 100;

        // Legger p� 0 om mintter og sekunder er mindre enn 10
        String minStr, secStr;
        if (minutes < 10)
            minStr = "0" + minutes;
        else
            minStr = "" + minutes;

        if (seconds < 10)
            secStr = "0" + seconds;
        else
            secStr = "" + seconds;

        // Setter variablene inn i en tabell som brukes til tegning p� skjerm.
        variables[0] = "" + distance;
        variables[1] = "" + points;
        variables[2] = minStr + ":" + secStr + ":" + mili;
    }

    /**
     * En metode som sjekker om et vesen befinner seg inne i en vegg (stuck) og
     * flytter han til en lokasjon hvor dette ikke er tilfellet lengere.
     *
     * @param tileX : Point - x-y-lokasjonen til blokken som vesenet kolliderer
     *              med.
     * @param c     : Creature - Vesenet som kolliderer med en blokk.
     */
    private void checkStuck(Point tileX, Creature c) {
        float offsetX = map.getOffsetX();
        float X = c.getX();
        float DX = c.getDX();
        int width = c.getWidth();
        int tilePixelX = renderer.tilesToPixels(tileX.x);

        if (c instanceof Player) {
            if (DX > 0 && (Math.round(X + offsetX + width)) == tilePixelX) {
                System.out.println("unstucking");
                c.setX(tilePixelX - width - offsetX - 1);
            } else if (DX < 0 && (Math.round(X + offsetX) + 1) == (tilePixelX + 32)) {
                System.out.println("unstucking");
                c.setX(tilePixelX - offsetX + 32 + 1);
            }
        } else /* if (c instanceof 'Enemy') */ {
            if (DX > 0 && (Math.round(X + width)) == tilePixelX) {
                c.setX(tilePixelX - width - 1);
            } else if (DX < 0 && (Math.round(X) == tilePixelX)) {
                c.setX(tilePixelX + 1);
            }
        }
    }

    /**
     * En metode som sjekker input ved � g� igjeonnom og sjekke hvilke av de
     * gyldige tastene som blir holdt nede.
     *
     * @param elapsedTime : long - Tiden som er g�tt siden forrige gjennoml�p av
     *                    gameLoop().
     */
    private void checkInput(long elapsedTime) {
        Player player = (Player) map.getPlayer();
        float currDX = player.getDX();
        float maxSpeed = Player.MAX_SPEED;
        setPaused(inp.isPaused());

        // Sjekker om 'esc' er trykket inn og returnerer spillet til menyen.
        if (inp.isExit() && game_state != PLAYER_DEAD && game_state != GAME_PAUSED) {
            enterMenu();
        }

        // Sjekker om 'p' er trykket inn og setter spillet i pause eller
        // kj�rende.
        if (isPaused()) {
            game_state = GAME_PAUSED;
        } else {
            if (game_state != PLAYER_DEAD && game_state != GAME_HIGHSCORE && game_state != GAME_OPTIONS
                    && game_state != GAME_MENU) {
                if (game_state == GAME_PAUSED) {
                    inp.releaseAllKeys();
                }
                game_state = GAME_RUNNING;
            }
        }

        // Sjekker om 'enter', 'back-space' eller 'esc' er trykket og starter
        // spillet om igjen om spillerener d�d eller tar spilleren tilbake til
        // menyen.
        if (game_state == PLAYER_DEAD) {
            if (inp.isSelect()) {
                if (checkpoint.x == 27 || renderer.pixelsToTiles(map.getOffsetX() + player.getX()) >= 2522) {
                    restartGame();
                } else {
                    playerRestartTime = System.currentTimeMillis();
                    totalTimePaused += playerRestartTime - playerDieTime;
                    restartCheckpoint();
                }
            }
            if (inp.isExit()) {
                restartGame();
                enterMenu();
            }
            if (inp.isBckSpace()) {
                restartGame();
            }
        }

        // Under sjekkes all input som har med bevegelse � gj�re.
        if (player != null && game_state == GAME_RUNNING) {
            // Sprinter til h�yre
            if (inp.isRight() && inp.isSprint() && !inp.isDuck()) {
                if (currDX >= maxSpeed * 2 * speedMultiplier) {
                    player.setDX(maxSpeed * 2 * speedMultiplier);
                } else {
                    player.setDX(currDX + ACCEL * elapsedTime);
                }
            }

            // Sprinter til venstre
            else if (inp.isLeft() && inp.isSprint() && !inp.isDuck()) {
                if (currDX <= -maxSpeed * 2 * speedMultiplier) {
                    player.setDX(-maxSpeed * 2 * speedMultiplier);
                } else {
                    player.setDX(currDX - ACCEL * elapsedTime);
                }
            }

            // Kryper til h�yre
            else if (inp.isRight() && inp.isDuck()) {
                player.setDX(maxSpeed / 2);
            }

            // Kryper tl venstre
            else if (inp.isLeft() && inp.isDuck()) {
                player.setDX(-(maxSpeed / 2));
            }

            // G�r til h�yre
            else if (inp.isRight()) {
                if (currDX >= maxSpeed) {
                    player.setDX(maxSpeed);
                } else {
                    player.setDX(currDX + ACCEL * elapsedTime);
                }
            }

            // G�r til venstre
            else if (inp.isLeft()) {
                if (currDX <= -maxSpeed) {
                    player.setDX(-maxSpeed);
                } else {
                    player.setDX(currDX - ACCEL * elapsedTime);
                }
            }

            // Spilleren dukker
            else if (inp.isDuck()) {
                // Ubrukt
            } else {
                if (currDX > -0.03f && currDX < 0.03f) {
                    player.setDX(0);
                } else if (currDX < 0) {
                    player.setDX(currDX + ACCEL * elapsedTime);
                } else /* (currDX > 0) */ {
                    player.setDX(currDX - ACCEL * elapsedTime);
                }
            }

            // Hopper
            if (inp.isJump() && !canWallJump & player.isOnGround()) {
                sound.jump();
                player.jump(false, jumpMultiplier);
                jumpStartTime = System.currentTimeMillis();
            }

            // walljump
            else if (inp.isJump() && canWallJump && (System.currentTimeMillis() - jumpStartTime) > 350
                    && (System.currentTimeMillis() - jumpStartTime) < 10000) {

                sound.jump();
                // walljumper hardt til venstre fra h�yre vegg
                if (inp.isRight() && inp.isSprint()) {
                    player.jump(true, jumpMultiplier);
                    player.setDX(-maxSpeed * 1.5f);
                }

                // walljumper hardt til h�yre fra venstre vegg
                else if (inp.isLeft() && inp.isSprint()) {
                    player.jump(true, jumpMultiplier);
                    player.setDX(maxSpeed * 1.5f);
                }

                // walljumper til venstre fra h�yre vegg
                else if (inp.isRight()) {
                    player.jump(true, jumpMultiplier);
                    player.setDX(-maxSpeed);
                }

                // walljumper til h�yre fra venstre vegg
                else if (inp.isLeft()) {
                    player.jump(true, jumpMultiplier);
                    player.setDX(maxSpeed);
                }
                jumpStartTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * En metode som sjekker input n�r spillet befinner seg i hovedmenyen.
     */
    private void checkMenuInput() {
        setPaused(true);

        // Sjekker input i hovedmenyen
        if (game_state == GAME_MENU) {
            int high = mRenderer.getHighlight();
            if (inpMenu.isLeft()) { // Up
                if (!hasPressed && high - 1 >= MENU_LOW) {
                    mRenderer.setHighlight(high - 1);
                    hasPressed = true;
                }
            } else if (inpMenu.isRight()) { // Down
                if (!hasPressed && high + 1 <= MENU_HIGH) {
                    mRenderer.setHighlight(high + 1);
                    hasPressed = true;
                }
            } else {
                hasPressed = false;
            }
        }

        // Sjekker input i options-menyen.
        if (game_state == GAME_OPTIONS) {
            int high = oRenderer.getHighlight();
            if (inpMenu.isLeft()) {
                if (!hasPressed && high - 1 >= MENU_LOW) {
                    oRenderer.setHighlight(high - 1);
                    hasPressed = true;
                }
            } else if (inpMenu.isRight()) {
                if (!hasPressed && high + 1 <= MENU_HIGH - 2) {
                    oRenderer.setHighlight(high + 1);
                    hasPressed = true;
                }
            } else if (inpMenu.isSelect()) {
                oRenderer.select();
                sound.checkSoundPref();
            } else {
                hasPressed = false;
            }
        }

        // Bestemmer hva som skal skje n�r enter trykkes inne p� en meny.
        if (inpMenu.isSelect()) {
            if (inpMenu.isSelect() && game_state == GAME_MENU) {
                int chosen = mRenderer.getHighlight();
                switch (chosen) {
                    case MenuRenderer.MENU_START_GAME:
                        startGame();
                        break;
                    case MenuRenderer.MENU_HIGHSCORES:
                        game_state = GAME_HIGHSCORE;
                        break;
                    case MenuRenderer.MENU_OPTIONS:
                        game_state = GAME_OPTIONS;
                        break;
                    case MenuRenderer.MENU_EXIT:
                        highscore.saveLocal();
                        System.exit(0);
                        break;
                }
            }
        }

        // G�r tilbake til spillets hovedmeny
        if (inpMenu.isEsc()) {
            game_state = GAME_MENU;
        }

        inpMenu.releaseAllKeys();
    }

    /**
     * Metode som sjekker om spilleren kolliderer med en annen Entity, enten det
     * er et dyr eller en power up.
     *
     * @param player  : Player - Objektet som representerer spilleren.
     * @param canKill : boolean - forteller om spilleren kan drepe et annet vesen
     *                under denne skjekken.
     */
    private void checkPlayerCollision(Player player, boolean canKill) {
        if (player.isAlive()) {
            // Henter inn et eventuelt kolliderende vesen.
            Entity collidingEntity = getCollidingEntity(player);
            if (collidingEntity != null) {
                if (collidingEntity instanceof Item) {
                    // Vesenet var et objekt. Objektet plukkes opp.
                    aquireItem((Item) collidingEntity);
                } else if (collidingEntity instanceof Creature) {
                    // Vesenet var et vesen, sjekker hva som skal skje ved
                    // kollisjon.
                    if (canKill && !(((Creature) collidingEntity).isImmortal())) {
                        // Spilleren kan ta livet av et vesenet og verken
                        // vesenet eller spilleren er ikke ud�delig.
                        ((Creature) collidingEntity).setState(Creature.STATE_DYING);
                        sound.creepKilled();
                        addBonus(collidingEntity, false);
                        if (getTileCollision(player, player.getX(), player.getY() + 32) == null) {
                            player.setY(collidingEntity.getY() - player.getHeight());
                        }
                        player.jump(true, 1.2f);
                    } else if (!invulnerable) {
                        // Spilleren kan ikke ta livet av vesenet og er selv
                        // ikke ud�delig.
                        player.setState(Creature.STATE_DYING);
                        sound.playerDies();
                        saveHighscore();
                        playerDieTime = System.currentTimeMillis();
                    } else if (invulnerable) {
                        // Spilleren er ud�delig og tar livet av vesenet uansett
                        // om det ogs� er "ud�delig".
                        ((Creature) collidingEntity).setState(Creature.STATE_DYING);
                        sound.creepKilled();
                        addBonus(collidingEntity, false);
                    }
                }
            }
        }
    }

    /**
     * Metode som sjekker hvilke powerups som e aktiv, og som setter disse til
     * inaktiv dersom de har vart lenge nok
     */
    private void checkUpgrades() {
        if (jumpMultiplier != 1.0f || speedMultiplier != 1.0f || invulnerable != false) {
            long currTime = System.currentTimeMillis();
            if (currTime - jumpUpgStart >= JUMP_DURATION) {
                jumpMultiplier = 1.0f;
                jumpUpgStart = 0;
                activeBuffs[0] = -1;
            }
            if (currTime - speedUpgStart >= SPEED_DURATION) {
                speedMultiplier = 1.0f;
                speedUpgStart = 0;
                activeBuffs[1] = -1;
            }
            if (currTime - invulUpgStart >= INVUL_DURATION) {
                invulnerable = false;
                invulUpgStart = 0;
                activeBuffs[2] = -1;
            }

            // Oppdaterer aktive buffs ints (for tegning)
            if (activeBuffs[0] != -1)
                activeBuffs[0] = Math.round((currTime - jumpUpgStart) / 1000);
            if (activeBuffs[1] != -1)
                activeBuffs[1] = Math.round((currTime - speedUpgStart) / 1000);
            if (activeBuffs[2] != -1)
                activeBuffs[2] = Math.round((currTime - invulUpgStart) / 1000);

        }
    }

    /**
     * En metode som sjekker om en fiende av type WalkingEnemy befinner seg n�r
     * en kant. Returnerer true hvis det er tilfellet. False hvis ikke.
     *
     * @param c : Creature - Dyret som skal sjekkes.
     * @return isNearEdge: boolean - Befinner dyret seg n�r en kant?
     */
    private boolean isNearEdge(Creature c) {
        // Henter dyrets n�v�rende og fremtidige posisjon basert x- og
        // y-verdier, samt fart.
        int currTileX = renderer.pixelsToTiles((c.getX() + (c.getWidth())));
        int currTileY = renderer.pixelsToTiles(c.getY() + (c.getHeight() / 2));
        int toTileX;
        int toTileY = currTileY + 1;
        if (c.getDX() > 0) {
            toTileX = currTileX;
        } else {
            toTileX = currTileX - 1;
        }

        if (map.getTile(toTileX, toTileY) == null) {
            return true;
        }
        return false;
    }

    /**
     * Metode som forteller om to objekter eller vesener kommer til � kollidere
     * eller ikke ved � sjekke om figurenes grenser overlapper i x-y-rom.
     *
     * @return isCollision: boolean - Oppst�r kollisjon?
     */
    private boolean isCollision(Entity e1, Entity e2) {
        // Hvis objektene er det samme, return false.
        if (e1 == e2) {
            return false;
        }

        // Er objektene vesener og er de i livet enda?
        if (e1 instanceof Creature && !((Creature) e1).isAlive()) {
            return false;
        }
        if (e2 instanceof Creature && !(((Creature) e2).isAlive())) {
            return false;
        }

        // Henter inn lokasjonene og grensene basert p� lokasjon i x-y-rom.
        int e1x = Math.round(e1.getX() + map.getOffsetX());
        int e1y = Math.round(e1.getY());
        int e2x = Math.round(e2.getX());
        int e2y = Math.round(e2.getY());

        // Sjekker om grensene overlapper.
        return (e1x < e2x + e2.getWidth() && e2x < e1x + e1.getWidth() - 5 && e1y < e2y + e2.getHeight() && e2y < e1y
                + e1.getHeight() - 8);
    }

    /**
     * Metode som returnerer den aktuelle Entitien som dyret eller spilleren
     * kommer til � kolliderer med.
     *
     * @return collitionEntity: Entity - Objektet som spilleren kommer til �
     *         kollidere med.
     */
    private Entity getCollidingEntity(Entity e1) {
        ArrayList<Entity> entities = map.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity e2 = entities.get(i);
            if (isCollision(e1, e2)) {
                return e2;
            }
        }
        return null;
    }

    /**
     * En metode som sjekker om et spesifikt vesen kolliderer med noen blokker.
     *
     * @param c    : Creature - Vesenet som skal sjekkes.
     * @param newX : float - Vesenets beregnede nye x-verdi.
     * @param newY : float - - Vesenets beregnede nye y-verdi.
     * @return tileX / TileY: Point - x-y-verdien til blokken som vesenet
     *         kolliderer med.
     */
    private Point getTileCollision(Creature c, float newX, float newY) {
        Point point = new Point();

        // Henter og beregner n�v�rende og kommende x-verdi.
        float fromX;
        float fromY = Math.min(c.getY(), newY);
        float toX;
        float toY = Math.max(c.getY(), newY);
        if (c instanceof Player) {
            fromX = Math.min(c.getX(), newX) + map.getOffsetX();
            toX = Math.max(c.getX(), newX) + map.getOffsetX();
            toY = Math.max(c.getY(), newY);
        } else {
            fromX = Math.min(c.getX(), newX);
            toX = Math.max(c.getX(), newX);
        }

        // Henter og beregner n�v�rende og kommende y-verdi.
        int fromTileX = renderer.pixelsToTiles(fromX);
        int fromTileY = renderer.pixelsToTiles(fromY);
        int toTileX = renderer.pixelsToTiles(toX + c.getWidth());
        int toTileY = renderer.pixelsToTiles(toY + c.getHeight() - 7);

        // Hvis den kommende Y-blokken er nummer 18 eller st�rre, set vesenets
        // status til d�d eller d�ende.
        if (toTileY >= 18) {
            c.setState(Creature.STATE_DEAD);
            if (c instanceof Player) {
                c.setState(Creature.STATE_DYING);
                sound.playerDies();
                saveHighscore();
                playerDieTime = System.currentTimeMillis();
            }
        } else {
            // Sjekk mulige blokker for kollisjon.
            for (int x = fromTileX; x <= toTileX; x++) {
                for (int y = fromTileY; y <= toTileY; y++) {
                    if (map.getTile(x, y) != null) {
                        point.setLocation(x, y);
                        return point;
                    }
                }
            }
        }
        // Ingen kollisjon funnet, returner null.
        return null;
    }

    /**
     * Metode som fjerner et objekt fra spillet og legget til effekten.
     *
     * @param collidingEntity : Item - Objektet som spilleren kolliderer med.
     */
    private void aquireItem(Item collidingEntity) {
        if (collidingEntity instanceof Pearl) {
            addBonus(collidingEntity, true);
            sound.playPearl();
        } else if (collidingEntity instanceof JumpUpgrade) {
            jumpMultiplier = 1.4f;
            jumpUpgStart = System.currentTimeMillis();
            activeBuffs[0] = Math.round(System.currentTimeMillis() - jumpUpgStart + (JUMP_DURATION / 1000));
            sound.jumpUpgrade();
        } else if (collidingEntity instanceof SpeedUpgrade) {
            speedMultiplier = 1.5f;
            speedUpgStart = System.currentTimeMillis();
            activeBuffs[1] = Math.round(System.currentTimeMillis() - speedUpgStart + (SPEED_DURATION / 1000));
            sound.speedBoost();
        } else if (collidingEntity instanceof InvulnerabilityUpgrade) {
            invulnerable = true;
            invulUpgStart = System.currentTimeMillis();
            activeBuffs[2] = Math.round(System.currentTimeMillis() - invulUpgStart + (INVUL_DURATION / 1000));
            sound.invulnerability();
        } else if (collidingEntity instanceof Checkpoint) {
            checkpoint.setLocation(renderer.pixelsToTiles(collidingEntity.getX()),
                    renderer.pixelsToTiles(collidingEntity.getY() - 1));
            sound.checkpoint();

        }
        // Ferdig med � legge til effekt av objektet, fjern det.
        map.eraseEntity(collidingEntity);
    }

    /**
     * En metode som legger til poeng n�r spilleren plukker opp en p�rle eller
     * n�r spilleren dreper en fiende.
     *
     * @param c      - Vesenet eller objektet som spilleren kolliderer med.
     * @param isItem - Er 'c' et vesen eller et "Item".
     */
    private void addBonus(Entity c, boolean isItem) {
        if (!isItem) {
            if (c instanceof WalkingEnemy) {
                bonusPoints += 20;
            } else if (c instanceof FlyingEnemy) {
                bonusPoints += 30;
            } else if (c instanceof JumpingEnemy) {
                bonusPoints += 5;
            }
        } else /* if (c instanceof Pearl) */ {
            bonusPoints += 500;
        }

    }

    /**
     * Metode som legger p� gravitasjon p� et vesen.
     *
     * @param c           : Creature - Dyret som skal p�virkes av gravitasjon.
     * @param elapsedTime : Tiden siden forrige gjennoml�p av gameLoop().
     */
    private void applyGravity(Creature c, long elapsedTime) {
        // Sjekker om vesenet er flyvende.
        if (!c.isFlying()) {
            // Sjekker om vesenet befinner seg p� bakken.
            if (!c.isOnGround()) {
                c.setDY(c.getDY() + GRAVITY * elapsedTime);
            } else {
                c.setDY(GRAVITY * elapsedTime);
            }
        }
    }

    /**
     * Tilbakestiller poeng, tid og distanse ved omstart av kart.
     */
    private void resetVariables() {
        inp.releaseAllKeys();
        startTime = System.currentTimeMillis();
        time = 0;
        distance = 0;
        points = 0;
        bonusPoints = 0;
        totalTimePaused = 0;
        pauseStartTime = 0;
        pauseEndTime = 0;
        playerDieTime = 0;
        playerRestartTime = 0;
        removeUpgrades();
    }

    /**
     * En metode som fjerner upgrades n�r spilleren d�r.
     */
    private void removeUpgrades() {
        jumpUpgStart = System.currentTimeMillis() - 10000;
        speedUpgStart = System.currentTimeMillis() - 10000;
        invulUpgStart = System.currentTimeMillis() - 10000;
    }

    /**
     * Metode som lagrer scoren til spilleren n�r spilleren d�r.
     */
    private void saveHighscore() {
        prefs = Preferences.userRoot().node("mack.GameManager");
        String name = prefs.get(PLAYER_NAME, "Player");
        if (variables[1] != null) {
            highscore.addScore(name, Integer.parseInt(variables[1]));
        }
    }
}