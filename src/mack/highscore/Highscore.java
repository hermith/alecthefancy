package mack.highscore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Highscoreklassen håndterer lagring av variabler og serialisering av
 * highscore-listen. De lagres først lokalt og serialiseres kun når man går inn
 * og ut av menyer eller lukker spillet. De blir lagret hver gang man dør.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
 * 
 */
public class Highscore {
	// Lokale variabler
	boolean MD5OK = false;
	String trueMD5;
	String filename;

	// Lokal highscore lagring
	LocalHighscore localHighscore;
	String serializeName = "localscores.scr";
	boolean isLoaded;

	/**
	 * Konstruktør som sjekker MD5 summet ved opprettelse
	 * 
	 * @param filename
	 *            : String - Navnet på filen som skal MD5-sjekkes (krever mappe
	 *            også, f.eks maps/map.txt).
	 * @param trueMD5
	 *            : String - Den ekte MD5 summen som den skal sjekkes opp mot.
	 */
	public Highscore(String filename, String trueMD5) {
		this.filename = filename;
		this.trueMD5 = trueMD5;
		this.MD5OK = MD5checker(filename, trueMD5);
		loadLocal();

	}

	/**
	 * Laster inn lokale poengsummer fra serialisert fil.
	 * 
	 * @return : Boolean - Om det var vellykket eller ikke.
	 */
	public boolean loadLocal() {
		try {
			System.out.println("Loading highscores..");
			FileInputStream inpStream = new FileInputStream(serializeName);
			ObjectInputStream oInp = new ObjectInputStream(inpStream);
			localHighscore = (LocalHighscore) oInp.readObject();
			System.out.println("Success!");
			return true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}
		System.out.println("Nothing to load, creating new save.");

		/**
		 * Om ingen fil var funnet oppretter den et nytt objekt med et par
		 * start-scores.
		 */
		localHighscore = new LocalHighscore();
		localHighscore.addScore("Karl[Dev]", true, 1000);
		localHighscore.addScore("Maria[Dev]", true, 800);
		localHighscore.addScore("Christer[Dev]", true, 500);
		localHighscore.addScore("Aleks[Dev]", true, 300);
		return false;
	}

	/**
	 * Serialiserer lokale scores til fil.
	 * 
	 * @return : boolean - Om den var velykket eller ikke.
	 */
	public boolean saveLocal() {
		if (localHighscore != null) {
			System.out.println("Trying to save highscores..");
			try {
				FileOutputStream outStream = new FileOutputStream(serializeName);
				ObjectOutputStream oOut = new ObjectOutputStream(outStream);
				oOut.writeObject(localHighscore);
				oOut.close();
				System.out.println("Success!");
				return true;
			} catch (FileNotFoundException e) {
				System.out.println("File not found");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error writing file");
			}
		} else {
			System.out.println("Nothing to save");
		}
		return false;
	}

	/**
	 * Legger til en ny score i score-listen.
	 * 
	 * @param s
	 *            : Score - Et objekt som inneholder nåværende score, navn og
	 *            MD5sjekk.
	 */
	public void addScore(Score s) {
		localHighscore.addScore(s);
	}

	/**
	 * Legger til en ny score i score-listen.
	 * 
	 * @param navn
	 *            : String - Navn på spilleren.
	 * @param score
	 *            : Int - Antall poeng
	 */
	public void addScore(String navn, int score) {
		localHighscore.addScore(new Score(navn, MD5OK, score));
	}

	/**
	 * Returnerer et Score-array som inneholder alle summer.
	 * 
	 * @return : Score[] - Tabell med alle Score
	 */
	public Score[] getScoreArray() {
		ArrayList<Score> sc = localHighscore.getEntireArray();
		Score[] score = new Score[sc.size()];

		sc.toArray(score);

		return score;
	}

	/**
	 * Returnerer en top-10 tabell med de 10 øverste Score-objekt.
	 * Score-objektene er sortert via Comparable. Om det ikke er nok
	 * Score-objekter returneres alle ferdig sorter.
	 * 
	 * @return : Score[] - Top 10 scores basert på score-summen.
	 */
	public Score[] getTopTenLocal() {
		ArrayList<Score> sc = localHighscore.getEntireArray();

		Collections.sort(sc);

		Score[] score;
		if (sc.size() > 9) {
			score = new Score[10];
			sc.subList(0, 10).toArray(score);
		} else {
			score = new Score[sc.size()];
			sc.subList(0, sc.size()).toArray(score);
		}

		return score;
	}

	/**
	 * Resjekker MD5 summen i lagrede verdier.
	 */
	public void reCheck() {
		MD5OK = MD5checker(filename, trueMD5);
	}

	/**
	 * Returnerer om MD5-summen er valid eller ikke.
	 * 
	 * @return boolean : Valid eller ikke
	 */
	public boolean getValidity() {
		return MD5OK;
	}

	/**
	 * Sjekker om MD5 summen stemmer overens med den fastsatte MD5 summen.
	 * 
	 * @param filename
	 *            : String - Filnavnet på filen som ska sjekkes
	 * @param trueMD5
	 *            : String - Den reelle MD5-summen
	 * @return boolean : Returnerer suksess eller ikke
	 */
	@SuppressWarnings("finally")
	public boolean MD5checker(String filename, String trueMD5) {
		File f = new File(filename);
		MessageDigest digest;
		InputStream is;
		boolean ok = false;
		try {
			digest = MessageDigest.getInstance("MD5");
			is = new FileInputStream(f);
		} catch (NoSuchAlgorithmException e9) {
			e9.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		byte[] buffer = new byte[8192];
		int read = 0;
		try {
			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String output = bigInt.toString(16);
			// System.out.println("MD5: " + output);
			if (trueMD5.equals(output))
				ok = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				return ok;
			}
		}
	}

}
