package mack.level;

import java.awt.Image;
import java.util.ArrayList;

import mack.entities.Entity;

/**
 * 
 * TileMap h�ndterer kartet og alt kartet inneholder (tiles, fiender, items).
 * 
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 * 
 */
public class TileMap {

	private Image[][] level;
	private ArrayList<Entity> entities;
	private Entity player;
	private int width;
	private int height;
	private float offsetX;

	/**
	 * 
	 * Konstrukt�r som tar inn bredde og h�yde til et nytt kart.
	 * 
	 * @param width
	 *            : int - Bredden p� kartet
	 * @param height
	 *            : int - H�yden p� kartet
	 */
	public TileMap(int width, int height) {
		this.width = width;
		this.height = height;
		level = new Image[width][height];
		entities = new ArrayList<Entity>();
		offsetX = 0;
	}

	/**
	 * En metode som returnerer offsetten til kartet.
	 * 
	 * @return offsetX: float -
	 */
	public float getOffsetX() {
		return offsetX;
	}

	/**
	 * En metode som returnerer bredden til kartet.
	 * 
	 * @return width: int - Bredden til kartet.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * En metode som returnerer h�yden til kartet.
	 * 
	 * @return height: int - H�yden til kartet.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * En metode som returnerer spilleren til kartet.
	 * 
	 * @return player: Entity - player som Entity super-objekt.
	 */
	public Entity getPlayer() {
		return player;
	}

	/**
	 * En metode som setter spilleren til kartet.
	 * 
	 * @param player
	 *            : Entity - Ny spiller som Entity-objekt.
	 */
	public void setPlayer(Entity player) {
		this.player = player;
	}

	/**
	 * En metode som setter offsetten til kartet.
	 * 
	 * @param offsetX
	 *            : float - Ny offset-verdi (hvor kartet er i forhold til
	 *            spilleren).
	 */
	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	/**
	 * Unders�ker om en tile ligger p� skjermen, og returnerer. Returnerer null
	 * dersom tilen ligger utenfor skjermen.
	 * 
	 * @param x
	 *            : int - x-verdi til bildet som skal hentes.
	 * @param y
	 *            : int - y-verdi til bildet som skal hentes.
	 * 
	 * @return image : Image - Bildet som befinner seg p� den X, Y verdien.
	 */
	public Image getTile(int x, int y) {
		if (x < 0 || x > getWidth() || y < 0 || y > getHeight() || y >= 18) {
			return null;
		}
		return level[x][y];
	}

	/**
	 * En metode som setter en tile til en spesiell X-Y-posisjon p� kartet.
	 * 
	 * @param x
	 *            : int - x-verdi til bildet som skal settes.
	 * @param y
	 *            : int - y-verdi til bildet som skal settes.
	 * @param tile
	 *            : Image - Bildet som skal settes til X,Y posisjonen.
	 */
	public void setTile(int x, int y, Image tile) {
		level[x][y] = tile;
	}

	/**
	 * En metode som legger til en enhet p� kartet.
	 * 
	 * @param entity
	 *            : Entity - Entity som skal legges til i entity-tabellen (som
	 *            inneholder fiender og items).
	 */
	public void addEntity(Entity entity) {
		entities.add(entity);
	}

	/**
	 * En metode som fjerner en enhet fra kartet.
	 * 
	 * @param entity
	 *            : Entity - Entity-en som skal fjernes.
	 */
	public void eraseEntity(Entity entity) {
		entities.remove(entity);
	}

	/**
	 * En metode som returnerer alle enhetene p� kartet.
	 * 
	 * @return ArrayList<Entity>: entities - Returnerer hele entities-tabellen.
	 */
	public ArrayList<Entity> getEntities() {
		return entities;
	}
}