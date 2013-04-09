package mack.level;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import mack.GameManager;
import mack.entities.Animation;
import mack.entities.Entity;
import mack.entities.creatures.Creature;
import mack.entities.creatures.FlyingEnemy;
import mack.entities.creatures.JumpingEnemy;
import mack.entities.creatures.MegaJumpingEnemy;
import mack.entities.creatures.Player;
import mack.entities.creatures.WalkingEnemy;
import mack.entities.items.Checkpoint;
import mack.entities.items.InvulnerabilityUpgrade;
import mack.entities.items.Item;
import mack.entities.items.JumpUpgrade;
import mack.entities.items.Pearl;
import mack.entities.items.SpeedUpgrade;

/**
 * Denne klassen h�ndterer tegning p� canvas, innlasting av kart fra tekstfil,
 * samt innlasting av alle enheter som igjen fungerer som vert-enheter.
 *
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 */
public class MapRenderer {

    public static final int TILE_SIZE = 32;

    // Bilder brukt til bakgrunner og GUI
    private ArrayList<Image> tiles;
    private Image background;
    private Image forebackground;
    private Image tutorial;
    private Image paused;
    private Image[] buffImages;

    // Vertsentititeter (alle fiender blir kloner fra verten).
    private Player player;
    private FlyingEnemy flyingEnemy;
    private WalkingEnemy walkingEnemy;
    private JumpingEnemy jumpingEnemy;
    private MegaJumpingEnemy boss;
    private InvulnerabilityUpgrade invulUpgrad;
    private JumpUpgrade jumpUpgrade;
    private SpeedUpgrade speedUpgrade;
    private Pearl pearl;
    private Checkpoint checkpoint;

    /**
     * Konstrukt�r. Kaller p� alle load-metodene som henter inn alle
     * vert-variablene og andre enheter som brukes til tegning.
     */
    public MapRenderer() {
        tiles = new ArrayList<Image>(0);
        loadTiles();
        loadEnemies();
        loadItems();
        loadPlayer();
        loadGUI();
    }

    /**
     * En metode som lager et tegnbart kart ut ifra en gitt tekstfil.
     *
     * @param filename     : String - Filnavn p� filen som skal lastes (
     * @param respawnPoint : Point - Point med hvor spawn skal v�re
     * @return map: TileMap - Kart med alle fiender, spiller, items og tiles.
     */
    public TileMap loadMap(String filename, Point respawnPoint) {
        System.out.println("Loading map..");
        ArrayList<String> lines = new ArrayList<String>();
        tutorial = new ImageIcon("images/tutorial.png").getImage();
        paused = new ImageIcon("images/paused.png").getImage();

        int width = 0;
        int height = 0;

        // Pr�ver � lese kartet fra en tekstfil
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("maps/" + filename));
            line = reader.readLine();
            while (line != null) {
                if (!line.startsWith("//")) {
                    lines.add(line);
                    width = Math.max(width, line.length());
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y = 0; y < height; y++) {
            line = lines.get(y);
            for (int x = 0; x < width; x++) {
                char ch = line.charAt(x);
                int tile = ch - ('A');

                // Legger til tiles:
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, tiles.get(tile));
                }

                // Legger til entities:
                else if (ch == '1') {
                    addEntity(newMap, walkingEnemy, x, y);
                } else if (ch == '2') {
                    addEntity(newMap, flyingEnemy, x, y);
                } else if (ch == '3') {
                    addEntity(newMap, jumpingEnemy, x, y);
                } else if (ch == 'j') {
                    addEntity(newMap, jumpUpgrade, x, y);
                } else if (ch == 'i') {
                    addEntity(newMap, invulUpgrad, x, y);
                } else if (ch == 's') {
                    addEntity(newMap, speedUpgrade, x, y);
                } else if (ch == 'p') {
                    addEntity(newMap, pearl, x, y);
                } else if (ch == 'b') {
                    addEntity(newMap, boss, x, y);
                } else if (ch == 'c') {
                    addEntity(newMap, checkpoint, x, y);
                }
            }
        }
        // Legger til spiller:
        Player newPlayer = (Player) player.clone();
        newPlayer.setX(tilesToPixels(14));
        newPlayer.setY(tilesToPixels(respawnPoint.y));
        newMap.setOffsetX(tilesToPixels(respawnPoint.x - 15));
        newMap.setPlayer(newPlayer);

        System.out.println("Map loaded.");

        return newMap;

    }

    /**
     * En metode som laster inn spilleren og setter den til sin vert-variabel.
     */
    private void loadPlayer() {
        System.out.println("Loading player..");
        File temp = new File("images/player.png");
        BufferedImage mapImage = null;
        try {
            mapImage = ImageIO.read(temp);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        // Oppretter alle animasjoner som skal inn i Player-objektet
        Animation animLeft = new Animation();
        Animation animRight = new Animation();
        Animation animJump = new Animation();
        Animation animJumpR = new Animation();
        Animation animIdle = new Animation();
        Animation animIdleR = new Animation();
        Animation animDead = new Animation();
        Animation animDeadR = new Animation();

        // Oppretter en versjon av bildet som er flippet, for animasjoner som
        // g�r til h�yre.
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-mapImage.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage flippedImage = op.filter(mapImage, null);

        int FRAME_TIME = 50; // Lengden p� hvert bilde i animasjonen
        int pW = 25; // Player width
        int pWs = 27; // Player widt stroke
        int pH = 50; // Player height

        // Laster inn alle bildene via subImage.
        for (int i = 0, u = 6; i < 7; i++, u--) {
            animRight.addFrame(mapImage.getSubimage(1 + (pWs * i), 1 + (0 * pH), pW, pH), FRAME_TIME);
            animLeft.addFrame(flippedImage.getSubimage(1 + (pWs * u), 1 + (0 * pH), pW, pH), FRAME_TIME);

            animJumpR.addFrame(mapImage.getSubimage(1 + (pWs * i), 3 + (1 * pH), pW, pH), FRAME_TIME);
            animJump.addFrame(flippedImage.getSubimage(1 + (pWs * u), 3 + (1 * pH), pW, pH), FRAME_TIME);

            animIdleR.addFrame(mapImage.getSubimage(1 + (pWs * i), 5 + (2 * pH), pW, pH), FRAME_TIME);
            animIdle.addFrame(flippedImage.getSubimage(1 + (pWs * u), 5 + (2 * pH), pW, pH), FRAME_TIME);

            animDead.addFrame(mapImage.getSubimage(1 + (pWs * i), 7 + (3 * pH), pW, pH), FRAME_TIME);
            animDeadR.addFrame(flippedImage.getSubimage(1 + (pWs * u), 7 + (3 * pH), pW, pH), FRAME_TIME);
        }

        // Legger til et siste bilde p� d�d-animasjonen som skal vare lenge.
        animDead.addFrame(mapImage.getSubimage(1 + (pWs * 6), 7 + (3 * pH), pW, pH), 1000000);
        animDeadR.addFrame(flippedImage.getSubimage(1 + (pWs * 0), 7 + (3 * pH), pW, pH), 1000000);

        this.player = new Player(animLeft, animRight, animDead, animDeadR, animIdle, animIdleR, animJump, animJumpR);

        System.out.println("Player loaded.");
    }

    /**
     * En metode som laster inn fiender setter dem til sine tilh�rende
     * vert-variabler
     */
    private void loadEnemies() {
        System.out.println("Loading enemies..");
        File file = new File("images/creatures.png");
        BufferedImage enemyImage = null;
        try {
            enemyImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (enemyImage != null) {
            // Oppretter alle animasjoner
            Animation oneLeftWalk = new Animation();
            Animation oneRightWalk = new Animation();
            Animation oneDead = new Animation();
            Animation oneDeadR = new Animation();

            Animation twoLeftWalk = new Animation();
            Animation twoRightWalk = new Animation();
            Animation twoDead = new Animation();
            Animation twoDeadR = new Animation();

            Animation threeRightWalk = new Animation();
            Animation threeLeftWalk = new Animation();
            Animation threeDead = new Animation();
            Animation threeDeadR = new Animation();

            Animation bossRightWalk = new Animation();
            Animation bossLeftWalk = new Animation();
            Animation bossDead = new Animation();
            Animation bossDeadR = new Animation();

            // Skalerer bildet for omvendte animasjoner.
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-enemyImage.getWidth(), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            BufferedImage flippedImage = op.filter(enemyImage, null);

            int FRAME_TIME = 100;

            for (int i = 0, u = 4; i < 5; i++, u--) {
                // Fiende 1 (32x64) (walking)
                oneRightWalk.addFrame(enemyImage.getSubimage(2 + (34 * i), 2, 32, 64), FRAME_TIME);
                oneLeftWalk.addFrame(flippedImage.getSubimage(2 + (34 * u), 2, 32, 64), FRAME_TIME);
                oneDead.addFrame(enemyImage.getSubimage(2 + (34 * i), (2 + 66), 32, 64), FRAME_TIME);
                oneDeadR.addFrame(flippedImage.getSubimage(2 + (34 * u), (2 + 66), 32, 64), FRAME_TIME);

                // Fiende 2 (32x32) (jumping)
                twoLeftWalk.addFrame(enemyImage.getSubimage(2 + (34 * i), (2 + 132), 32, 32), FRAME_TIME);
                twoRightWalk.addFrame(flippedImage.getSubimage(2 + (34 * u), (2 + 132), 32, 32), FRAME_TIME);
                twoDead.addFrame(enemyImage.getSubimage(2 + (34 * i), (2 + 132 + 34), 32, 32), FRAME_TIME);
                twoDeadR.addFrame(flippedImage.getSubimage(2 + (34 * u), (2 + 132 + 34), 32, 32), FRAME_TIME);

                // Boss (32x32 men skaleres opp til 200x200) (mega jumping)
                bossLeftWalk.addFrame(
                        enemyImage.getSubimage(2 + (34 * i), (2 + 132), 32, 32).getScaledInstance(200, 200, 0),
                        FRAME_TIME);
                bossRightWalk.addFrame(
                        flippedImage.getSubimage(2 + (34 * u), (2 + 132), 32, 32).getScaledInstance(200, 200, 0),
                        FRAME_TIME);
                bossDead.addFrame(
                        enemyImage.getSubimage(2 + (34 * i), (2 + 132 + 34), 32, 32).getScaledInstance(200, 200, 0),
                        FRAME_TIME);
                bossDeadR.addFrame(
                        flippedImage.getSubimage(2 + (34 * u), (2 + 132 + 34), 32, 32).getScaledInstance(200, 200, 0),
                        FRAME_TIME);

            }
            for (int i = 0, u = 2; i < 3; i++, u--) {
                // Fiende 3 (52x32) (flying)
                threeLeftWalk.addFrame(enemyImage.getSubimage(4 + (56 * i), (2 + 200), 52, 32), FRAME_TIME);
                threeLeftWalk.addFrame(enemyImage.getSubimage(4 + (56 * i), (2 + 200 + 34), 52, 32), FRAME_TIME);

                threeRightWalk.addFrame(flippedImage.getSubimage(4 + (56 * u), (2 + 200), 52, 32), FRAME_TIME);
                threeRightWalk.addFrame(flippedImage.getSubimage(4 + (56 * u), (2 + 200 + 34), 52, 32), FRAME_TIME);

                threeDead.addFrame(enemyImage.getSubimage(4 + (56 * i), (2 + 234 + 34), 52, 32), FRAME_TIME);
                threeDeadR.addFrame(flippedImage.getSubimage(4 + (56 * u), (2 + 234 + 34), 52, 32), FRAME_TIME);
            }

            /**
             * Legger til et tomt bilde p� slutten av d�d-animasjoner som varer
             * lenge. Slik at spillet har tid til � fjerne entities f�r
             * animasjonen bynner p� nytt.
             **/
            Image deadBuffer = enemyImage.getSubimage(4, 306, 32, 32);
            oneDead.addFrame(deadBuffer, 2000);
            oneDeadR.addFrame(deadBuffer, 2000);
            twoDead.addFrame(deadBuffer, 2000);
            twoDeadR.addFrame(deadBuffer, 2000);
            threeDead.addFrame(deadBuffer, 2000);
            threeDeadR.addFrame(deadBuffer, 2000);
            bossDead.addFrame(deadBuffer, 2000);
            bossDeadR.addFrame(deadBuffer, 2000);

            walkingEnemy = new WalkingEnemy(oneLeftWalk, oneRightWalk, oneDead, oneDeadR);
            jumpingEnemy = new JumpingEnemy(twoLeftWalk, twoRightWalk, twoDead, twoDeadR);
            flyingEnemy = new FlyingEnemy(threeLeftWalk, threeRightWalk, threeDead, threeDeadR);
            boss = new MegaJumpingEnemy(bossLeftWalk, bossRightWalk, bossDead, bossDeadR);

            System.out.println("Enemies loaded.");
        }

    }

    /**
     * En metode som laster inn objekter setter dem til sine tilh�rende
     * vert-variabler
     */
    private void loadItems() {
        System.out.println("Loading items..");
        File file = new File("images/items.png");
        BufferedImage itemsImage = null;
        try {
            itemsImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Oppretter alle animasjonene.
        Animation jump = new Animation();
        Animation invul = new Animation();
        Animation speed = new Animation();
        Animation pearlA = new Animation();
        Animation check = new Animation();

        int iSize = 32; // St�rrelsen p� items
        int FRAME_TIME = 100; // Lengden p� animasjonen

        for (int i = 0; i < 4; i++) {
            jump.addFrame(itemsImage.getSubimage(1 + ((iSize + 1) * i), 1 + (0 * iSize), iSize, iSize), FRAME_TIME);
            invul.addFrame(itemsImage.getSubimage(1 + ((iSize + 1) * i), 2 + (1 * iSize), iSize, iSize), FRAME_TIME);
            speed.addFrame(itemsImage.getSubimage(1 + ((iSize + 1) * i), 3 + (2 * iSize), iSize, iSize), FRAME_TIME);
            pearlA.addFrame(itemsImage.getSubimage(1 + ((iSize + 1) * i), 4 + (3 * iSize), iSize, iSize), FRAME_TIME);
            check.addFrame(itemsImage.getSubimage(1 + ((iSize + 1) * i), 5 + (4 * iSize), iSize, iSize), FRAME_TIME);
        }

        jumpUpgrade = new JumpUpgrade(jump);
        invulUpgrad = new InvulnerabilityUpgrade(invul);
        speedUpgrade = new SpeedUpgrade(speed);
        pearl = new Pearl(pearlA);
        checkpoint = new Checkpoint(check);

        System.out.println("Items loaded.");
    }

    /**
     * En metode som laster inn blokker og legger dem inn i en to-dimensjonal
     * tile-tabell.
     */
    private void loadTiles() {
        System.out.println("Loading tiles..");
        File tileFilename = new File("images/tiles.png");
        BufferedImage mapImage = null;
        try {
            mapImage = ImageIO.read(tileFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mapImage != null) {
            int rows = mapImage.getHeight(null) / TILE_SIZE;
            int columns = mapImage.getWidth(null) / TILE_SIZE;
            int currX = 1;
            int currY = 1;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    Image subImage = mapImage.getSubimage(currX, currY, TILE_SIZE, TILE_SIZE);
                    tiles.add(subImage);

                    currX += TILE_SIZE + 2;
                }
                currY += TILE_SIZE + 2;
                currX = 1;
            }
        }
        System.out.println("Done loading tiles.");
    }

    /**
     * Laster inn bildene som brukes til "buff-UI". Alts� de bildene som
     * indikerer hvor lenge en oppgradering varer og hvilke som er aktive.
     */
    private void loadGUI() {
        System.out.println("Loading GUI..");
        File buffFile = new File("images/buffmarkers.png");
        BufferedImage buffMarkers = null;
        try {
            buffMarkers = ImageIO.read(buffFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        buffImages = new Image[3];

        for (int i = 0; i < buffImages.length; i++) {
            buffImages[i] = buffMarkers.getSubimage(1 + (50 * i) + i, 1, 50, 50);
        }

        System.out.println("GUI loaded..");

    }

    /**
     * En metode som tar inn et canvas av typen Graphics2D og tegner p� dette.
     *
     * @param g            : Graphics2D - Grafikken som blir tegnet.
     * @param map          : TileMap - Tilemap som inneholder all informasjon om kartet.
     * @param scrnWidth    : int - Skjermbredde
     * @param scrnHeight   : int - Skjermh�yde
     * @param offsetXfloat : float - offset-verdien som en float (hvor kartet er i
     *                     forhold til spilleren.
     * @param variables    : String[] - Variablene som visest p� skjerm. Tid, distance og
     *                     poengsum.
     * @param game_state   : int - Hvilken "status" spillet har, om det kj�rer, er pauset
     *                     eller lignende.
     * @param activeBuffs  : int[] - Hvilke oppgraderinger som er aktive og skal visest
     *                     p� skjerm, samt hvor lenge de har igjen.
     */

    public void draw(Graphics2D g, TileMap map, int scrnWidth, int scrnHeight, float offsetXfloat, String[] variables,
                     int game_state, int[] activeBuffs) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB); // Setter

        // Gj�r om offset til et tall draw-metoden kan bruke.
        int offsetX = Math.round(offsetXfloat);

        // Henter ut hvor lenge hver oppgradering skal vare.
        int jump = activeBuffs[0];
        int speed = activeBuffs[1];
        int invul = activeBuffs[2];

        // Tegner bakgrunn
        int xBG = (int) Math.round(0 - offsetX / 1.8);
        int xBGs = (int) Math.round(0 - offsetX / 1.3);
        int bgWidth = background.getWidth(null);

        // Tegnre f�rste backgrunn, den m� tegnes i starten.
        if (Math.round(offsetX / 1.8) < bgWidth) {
            g.drawImage(background, xBG, 0, null);
        }

        // Tegner andre bakgrunn, denne m� ogs� tegnes fra start, men skal
        // tegnes lengre enn f�rste.
        if (Math.round(offsetX / 1.8) < bgWidth * 2) {
            g.drawImage(background, xBG + bgWidth, 0, null);
        }

        // Tegner alle backgrunner fra 3 og utover til uendelig. Ogs� kjent som
        // "Parralax scrolling".
        if (Math.round(offsetX / 1.8) > 1024) {
            int multiplier = (int) (Math.round(offsetX / 1.8) / (bgWidth));
            g.drawImage(background, xBG + bgWidth * (multiplier), 0, null);
            g.drawImage(background, xBG + bgWidth * (multiplier + 1), 0, null);
        }

        // Tegner sekund�rbakgrunn som ligger lengre frem, samme logikk som
        // over, andre variabler.
        if (Math.round(offsetX / 1.3) < bgWidth) {
            g.drawImage(forebackground, xBGs, 176, null);
        }

        if (Math.round(offsetX / 1.3) < bgWidth * 2) {
            g.drawImage(forebackground, xBGs + bgWidth, 176, null);
        }

        if (Math.round(offsetX / 1.3) > 1024) {
            int multipliers = (int) (Math.round(offsetX / 1.3) / (bgWidth));
            g.drawImage(forebackground, xBGs + bgWidth * (multipliers), 176, null);
            g.drawImage(forebackground, xBGs + bgWidth * (multipliers + 1), 176, null);
        }

        // Tegner tutorial og introtekst
        if (offsetX < 3000) {
            // Tutorialbildet
            g.drawImage(tutorial, 460 - offsetX, 70, null);

            // Tekst under mobs
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 15));
            g.drawString("You can kill these monsters.." + "                         " + "..and these.."
                    + "                               " + "..but not these! Watch out!", 505 - offsetX, 430);

            // Teksttutorial
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 17));

            g.drawString("Press against the wall mid-air and jump to walljump.", 1036 - offsetX, 510);

            g.setFont(new Font("Arial", Font.BOLD, 20));

            g.setColor(Color.black);
            g.drawString("There are three upgrades and a treasure!", 1535 - offsetX, 447);

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("Jump", 1565 - offsetX, 530);
            g.drawString("Speed", 1625 - offsetX, 530);
            g.drawString("Invulnerability", 1675 - offsetX, 530);
            g.drawString("Pearl", 1790 - offsetX, 530);

            g.setFont(new Font("Arial", Font.BOLD, 14));

            g.drawString("And don't fall.", 1965 - offsetX, 450);

            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("Oh yeah..", 2215 - offsetX, 481);

            g.setFont(new Font("Arial", Font.BOLD, 15));
            g.drawString("and", 2355 - offsetX, 450);

            g.setFont(new Font("Arial", Font.BOLD, 23));
            g.drawString("don't", 2467 - offsetX, 418);

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("DIE", 2662 - offsetX, 386);
        }

        // Tegner tiles, men kun tiles som er innenfor hva spilleren ser p�
        // skjerm.
        int drawMinTile = (offsetX / TILE_SIZE) - 2;
        int drawMaxTile = drawMinTile + 32 + 3;
        int tileX = 0;
        int tileY = 0;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Image tile = null;
                if (x < drawMaxTile && x > drawMinTile) {
                    tile = map.getTile(x, y);
                }
                if (tile != null) {
                    g.drawImage(tile, tileX - offsetX, tileY, null);
                }
                tileX += TILE_SIZE;
            }
            tileY += TILE_SIZE;
            tileX = 0;
        }

        // Tegner spilleren
        Creature currPlayer = (Creature) map.getPlayer();
        g.drawImage(currPlayer.getImage(), Math.round(currPlayer.getX()), Math.round(currPlayer.getY()), null);

        // Tegner fiender, men kun fiender som er innefor et fastsatt omr�de.
        ArrayList<Entity> entities = map.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity cur = entities.get(i);
            int cP = Math.round(cur.getX() - offsetXfloat);

            if (cP > -400 && cP < 1400) {
                g.drawImage(cur.getImage(), Math.round(cur.getX()) - offsetX, Math.round(cur.getY()), null);
            }
        }

        // Tegner distanse, poeng og tid �verst p� skjermen.
        int varH = 50;
        g.setColor(new Color(0.11f, 0.45f, 0.1f));
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Distance: " + variables[0], 64, varH);
        g.drawString("Points: " + variables[1], 430, varH);
        g.drawString("Time: " + variables[2], 824, varH);

        // Tegner buffs og inverterer tallet til � telle nedover
        g.setColor(Color.BLACK);
        g.setFont(new Font("Verdana", Font.BOLD, 14));
        int icon_level = 520;
        int font_level = 560;
        if (jump != -1) {
            jump = (GameManager.JUMP_DURATION / 1000) - jump;
            g.drawImage(buffImages[0], 430, icon_level, null);
            g.drawString("" + jump, 434, font_level);
        }
        if (speed != -1) {
            speed = (GameManager.SPEED_DURATION / 1000) - speed;
            g.drawImage(buffImages[2], 480, icon_level, null);
            g.drawString("" + speed, 484, font_level);
        }
        if (invul != -1) {
            invul = (GameManager.INVUL_DURATION / 1000) - invul;
            g.drawImage(buffImages[1], 530, icon_level, null);
            g.drawString("" + invul, 534, font_level);
        }

        // Om spillet er pauset tegnes det en hvit "overlay" over hele skjermen.
        if (game_state == GameManager.GAME_PAUSED) {
            g.drawImage(paused, 0, 0, null);
        }

        // Tegner spiller har d�dd-meny. Dersom spilleren er p� slutten av
        // kartet tegnes en litt annen skjerm.
        if (game_state == GameManager.PLAYER_DEAD) {
            g.setStroke(new BasicStroke(3));
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 44));
            if (Integer.parseInt(variables[0]) > 2475) {
                g.drawRect(65, 250, 900, 110);
                g.setColor(new Color(1f, 1f, 1f, 0.5f));
                g.fillRect(65, 250, 900, 110);
                g.setColor(Color.BLACK);
                g.drawString("CONGRATULATIONS! You are amazing.", 100, 300);
                g.setFont(new Font("Arial", Font.BOLD, 46));
                g.drawString("Your score: " + variables[1], 355, 350);
            } else {
                g.drawRect(270, 150, 550, 230);
                g.setColor(new Color(1f, 1f, 1f, 0.5f));
                g.fillRect(270, 150, 550, 230);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 25));
                g.drawString("Press ENTER to spawn from last checkpoint", 280, 280);
                g.drawString("Press BACKSPACE to spawn at the start", 300, 340);
                g.setFont(new Font("Arial", Font.BOLD, 46));
                g.drawString("Your score: " + variables[1], 355, 200);
            }

        }

        // Tegner distanceindikator
        g.setColor(Color.gray);
        g.setStroke(new BasicStroke(3));
        g.drawLine(30, 565, 1000, 565);

        // Hardkodet i forhold til maplengde
        int presentLength = Math.round(offsetXfloat / 84);
        g.setColor(Color.green);
        g.drawLine(30, 565, 30 + presentLength, 565);

        // Tegner den lille spilleren over distanseindikatoren.
        Image tinyHead = map.getPlayer().getImage();
        tinyHead = tinyHead.getScaledInstance(12, 26, 0);
        g.drawImage(tinyHead, 30 + presentLength, 538, null);
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.setColor(Color.white);
        g.drawString("End", 980, 560);
    }

    /**
     * En metode som igjen kaller p� TileMap sin addEntity-metode. Metoden
     * legger til en enhet p� en bestem lokasjon p� kartet.
     *
     * @param newMap : TileMap - Kart som entity skal legges til p�
     * @param ent    : Entity - Entity som skal legges til
     * @param tileX  : int - X-posisjonen til entity
     * @param tileY  : int - Y-posisjonen til entity
     */
    private void addEntity(TileMap newMap, Entity ent, int tileX, int tileY) {
        if (newMap == null || ent == null) {
            System.out.println("newMap eller ent er null!");
        } else {
            Entity newEntity;
            if (ent instanceof Player)
                newEntity = (Entity) ent.clone();
            else
                newEntity = (Entity) ent.cloneOther();

            newEntity.setX(tilesToPixels(tileX));
            if (!(newEntity instanceof Item))
                newEntity.setY(tilesToPixels(tileY) + 5);
            else
                newEntity.setY(tilesToPixels(tileY));
            if (newEntity instanceof Creature && !(newEntity instanceof Player)) {
                ((Creature) newEntity).setMaxSpeed(0.1f);
            }
            newMap.addEntity(newEntity);
        }
    }

    /**
     * En metode som setter bakgrunnen(e) til et gitt bilde/gitte bilder.
     *
     * @param background : Image - Bakgrunnsbildet som skal settes.
     * @param foreback   : Image - Mellom-bakgrunnsbildet som skal settes.
     */
    public void setBackground(Image background, Image foreback) {
        this.background = background;
        this.forebackground = foreback;
    }

    /**
     * En metode som konverterer et antall blokker til piksler.
     *
     * @param tiles : int - Tile som skal konverteres til piksel-verdi.
     * @return pixels: int - Tile-verdi konvertert til pixel-verdi.
     */
    public int tilesToPixels(int tiles) {
        return tiles * TILE_SIZE;
    }

    /**
     * En metode som konverterer et antall piksler til blokker
     *
     * @param pixels :int - Piksel-verdi som skal konverteres til tile-verdi
     * @return tiles: int - Pixel-verdi konvertert til tile-verdi
     */
    public int pixelsToTiles(int pixels) {
        return pixels / TILE_SIZE;
    }

    /**
     * En metode som konverterer et antall piksler til blokker
     *
     * @param pixels : float - Konverterer en float til tile-verdi
     * @return tiles: int - Float-konvertert til tile-verdi
     */
    public int pixelsToTiles(float pixels) {
        return Math.round(pixels) / TILE_SIZE;
    }
}
