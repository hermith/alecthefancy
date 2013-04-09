package mack.highscore;

import java.io.Serializable;

/**
 * 
 * Score tar vare p� poengsum, spillernavn og om kartet er valid.
 * 
 * @author Christer Olsen, Maria S�rlie, Karl J�rgen Over�, Aleksander L. Rasch
 * 
 */

@SuppressWarnings("serial")
public class Score implements Comparable<Score>, Serializable {

	String name;
	boolean MD5;
	int score;

	/**
	 * Konstrukt�r som tar inn navn, boolean og score.
	 * 
	 * @param n
	 *            : String - Navn p� spiller
	 * @param md5
	 *            : String - MD5 sum som ble lest under spilling
	 * @param s
	 *            : Int - Summen
	 */
	public Score(String n, boolean md5, int s) {
		this.name = n;
		this.MD5 = md5;
		this.score = s;
	}

	/**
	 * Returnerer navnet p� spilleren.
	 * 
	 * @return : String - Navn
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returnerer om forrige MD5 sjekk
	 * 
	 * @return boolean : Valid eller ikke
	 */
	public boolean getMD5() {
		return MD5;
	}

	/**
	 * Returnerer poengsummen.
	 * 
	 * @return : int - Poengsum
	 */
	public int getScore() {
		return score;
	}

	/**
	 * compareTo som tilh�rer Comparable-klassen. Brukes ved sortering av
	 * tabell. Skal sorteres fra h�y til lav.
	 */
	@Override
	public int compareTo(Score o) {
		if (score > o.getScore()) {
			return -1;
		} else if (score == o.getScore()) {
			return 0;
		} else {
			return 1;
		}
	}

}
