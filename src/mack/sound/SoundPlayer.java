package mack.sound;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import mack.menu.OptionsRenderer;

/**
 * Denne klassen håndterer all avspilling av lyd.
 * 
 * @author Christer Olsen, Maria Sørlie, Karl Jørgen Overå, Aleksander L. Rasch
 * 
 */
public class SoundPlayer {

	private Sequencer sequencer;
	private Sequencer items;
	private Sequencer jumpSeq;

	private Sequence themeloop;
	private Sequence jump;
	private Sequence killCreep;
	private Sequence playerDie;
	private Sequence speedBoost;
	private Sequence pearl;
	private Sequence jumpUpgrade;
	private Sequence invulnerability;
	private Sequence checkpoint;

	private Preferences prefs;
	private boolean soundEnabled;

	/**
	 * Konstruktør for avspilling av lyd.
	 */
	public SoundPlayer() {
		try {
			themeloop = MidiSystem.getSequence(new File("sounds/themeloop.mid"));
			jump = MidiSystem.getSequence(new File("sounds/jump.mid"));
			killCreep = MidiSystem.getSequence(new File("sounds/enemydies.mid"));
			playerDie = MidiSystem.getSequence(new File("sounds/playerdies.mid"));
			speedBoost = MidiSystem.getSequence(new File("sounds/speed.mid"));
			pearl = MidiSystem.getSequence(new File("sounds/pearl.mid"));
			jumpUpgrade = MidiSystem.getSequence(new File("sounds/jumpupgrade.mid"));
			invulnerability = MidiSystem.getSequence(new File("sounds/invulnerability.mid"));
			checkpoint = MidiSystem.getSequence(new File("sounds/checkpoint.mid"));

			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(themeloop);

			items = MidiSystem.getSequencer();
			items.open();

			jumpSeq = MidiSystem.getSequencer();
			jumpSeq.open();

			jumpSeq.setSequence(jump);
			jumpSeq.setLoopCount(0);

			prefs = Preferences.userRoot().node("mack.GameManager");
			soundEnabled = true;
			checkSoundPref();

		} catch (InvalidMidiDataException e) {
			// Ignore
		} catch (IOException e) {
			// Ignore
		} catch (MidiUnavailableException e) {
			// Ignore
		}
	}

	/**
	 * Undersøker om spilleren har aktivert lyd i options.
	 */
	public void checkSoundPref() {
		soundEnabled = prefs.getBoolean(OptionsRenderer.SOUND_ENABLED, true);
		if (!soundEnabled) {
			sequencer.stop();
		} else {
			sequencer.start();
		}
	}

	/**
	 * Spiller av theme-sangen for spillet i en loop
	 */
	public void playSong() {
		if (soundEnabled) {
			sequencer.setLoopCount(-1);

			sequencer.start();
		}
	}

	/**
	 * Spiller av hopp-lyden til spilleren
	 */
	public void jump() {
		if (soundEnabled) {
			try {
				jumpSeq.setSequence(jump);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}
			jumpSeq.setMicrosecondPosition(1020);
			jumpSeq.start();
		}
	}

	/**
	 * Spiller av en lyd når du har drept en fiende
	 */
	public void creepKilled() {
		if (soundEnabled) {
			try {
				items.setSequence(killCreep);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}
			items.setMicrosecondPosition(1000);
			items.start();
		}
	}

	/**
	 * Spiller av en lyd når spilleren er død
	 */
	public void playerDies() {
		if (soundEnabled) {
			try {
				items.setSequence(playerDie);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}
			items.setMicrosecondPosition(1000);
			items.start();
		}
	}

	/**
	 * Spiller av en lyd for ekstra fart-item
	 */
	public void speedBoost() {
		if (soundEnabled) {
			try {
				items.setSequence(speedBoost);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}

			items.setMicrosecondPosition(1000);
			items.start();
		}
	}

	/**
	 * Spiller av en lyd for pearl-item
	 */
	public void playPearl() {
		if (soundEnabled) {
			try {
				items.setSequence(pearl);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}

			items.setMicrosecondPosition(1020);
			items.start();
		}
	}

	/**
	 * Spiller av en lyd for hopp-item
	 */
	public void jumpUpgrade() {
		if (soundEnabled) {
			try {
				items.setSequence(jumpUpgrade);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}

			items.setMicrosecondPosition(1020);
			items.start();
		}
	}

	/**
	 * Spiller av en lyd for skjold-item
	 */
	public void invulnerability() {
		if (soundEnabled) {
			try {
				items.setSequence(invulnerability);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}

			items.setMicrosecondPosition(1020);
			items.start();
		}
	}

	/**
	 * Spiller av en lyd for nådd checkpoint
	 */
	public void checkpoint() {
		if (soundEnabled) {
			try {
				items.setSequence(checkpoint);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}

			items.setMicrosecondPosition(1020);
			items.start();

		}
	}
}