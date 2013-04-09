package mack.highscore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * LocalHighscore håndterer midlertidig lagring (før serialisering) av
 * Score-objektene. Alt lagres in en ArrayList som senere serialiseres inn og
 * ut.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
 * 
 */

@SuppressWarnings("serial")
class LocalHighscore implements Serializable {

	ArrayList<Score> scores;

	/**
	 * Oppretter ArrayListen.
	 */
	public LocalHighscore() {
		scores = new ArrayList<Score>();
	}

	/**
	 * 
	 * Alternativ konstruktør som oppretter ArrayList av en eksiterende tabell.
	 * 
	 * @param s
	 *            : ArrayList<Score> - En ArrayList av Score-objekter
	 */
	public LocalHighscore(ArrayList<Score> s) {
		this.scores = s;
	}

	/**
	 * Legger til en score av et Score objekt til ArrayListen
	 * 
	 * @param s
	 *            : Score - Score-objekt med navn, MD5 bool og score-sum.
	 */
	public void addScore(Score s) {
		scores.add(s);
	}

	/**
	 * Legger til en score av navn, MD5 bool og score-sum.
	 * 
	 * @param navn
	 *            : String - Spillernavn.
	 * @param MD5
	 *            : boolean - Om MD5 sjekken var OK eller ikke.
	 * @param score
	 *            : int - Score-sum.
	 */
	public void addScore(String navn, boolean MD5, int score) {
		scores.add(new Score(navn, MD5, score));
	}

	/**
	 * Returnerer hele tabellen som en ArrayList av Score-objekter.
	 * 
	 * @return ArrayList<Score> : ArrayList med Score-objekter.
	 */
	public ArrayList<Score> getEntireArray() {
		return scores;
	}

	/**
	 * Brukes for å hente ut en spesifikk Score fra highscore-listen basert på
	 * indeks.
	 * 
	 * @param entry
	 *            : int - Indeks for poengsummen.
	 * @return Score : Score-objekt med navn, md5 og poeng.
	 */
	public Score getSingleEntry(int entry) {
		return scores.get(entry);
	}
}
