package main;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.InvalidMidiDataException;

public class Line {

  Logger logger = LoggerFactory.getLogger(Line.class);

  // Ordered list of ordered quadruplets (timestamp, duration, pitch, velocity) representing the
  // sequence of notes in this line
  private List<Note> notes;

  private float divisionType;
  private int ticksPerBeat;

  /**
   * These dictate the max/min pitch values between which notes in this line may sit. The defaults
   * -1 lie outside the midi pitch range, so will throw an error if they are called to mutate a
   * note. This is to make sure the lines which don't have assigned max/min pitch ranges (the melody
   * line) cannot be mutated
   */
  private int maxPitch = -1;
  private int minPitch = -1;

  /**
   * Creates a line object with metadata concerning tempo but no specific note data
   * 
   * @param ticksPerBeat The ticks per beat for this line. This parameter is intended to be either
   *        pulled from a midi file (see MidiStatic.getMelodyFromFile) or from another line, so that
   *        lines can be synched up properly
   * @param divisionType The division type (must be a valid PPQ or SMTp type, e.g.
   *        javax.sound.midi.Sequence.PPQ). This parameter is intended to be either pulled from a
   *        midi file (see MidiStatic.getMelodyFromFile) or from another line, so that lines can be
   *        synched up properly
   */
  public Line(int ticksPerBeat, float divisionType) {
    this.notes = new ArrayList<Note>();

    this.ticksPerBeat = ticksPerBeat;
    this.divisionType = divisionType;

  }

  /**
   * Use this constructor to get a line object straight from a midi filepath. This is
   * interchangeable with MidiStatic.getMelodyFromFile but is included for neatness
   * 
   * @param filepath The midi filepath
   * @throws InvalidMidiDataException
   * @throws IOException
   */
  public Line(String filepath) throws InvalidMidiDataException, IOException {
    Line importedLine = MidiStatic.getMelodyFromFile(filepath);
    this.notes = importedLine.getNotes();
    this.ticksPerBeat = importedLine.getTicksPerBeat();
    this.divisionType = importedLine.getDivisionType();
  }

  /**
   * Creates a new Line object based on a template line. The horizontal note placement is preserved,
   * and the pitches are randomised within the specified bounds (which are set as the bounds for
   * this line)
   * 
   * @param template The template line
   * @param minPitch The minimum pitch
   * @param maxPitch The maximum pitch
   * @throws InvalidParameterException if the pitches are invalid
   */
  public Line(Line template, int minPitch, int maxPitch) {
    this.ticksPerBeat = template.getTicksPerBeat();
    this.divisionType = template.getDivisionType();
    this.notes = template.getNotes();
    for (Note note : this.notes) {
      note.mutatePitch(minPitch, maxPitch);
    }
    if (maxPitch < 128 && minPitch < 128 && maxPitch >= 0 && minPitch >= 0 && maxPitch > minPitch) {
      this.maxPitch = maxPitch;
      this.minPitch = minPitch;
    } else {
      logger.error("Attempted to create line with minPitch = " + minPitch + ", maxPitch = "
          + maxPitch + ". We require minPitch >= 0, maxPitch <= 127 & maxPitch > minPitch.");
      throw new InvalidParameterException("Attempted to create line with minPitch = " + minPitch + ", maxPitch = "
          + maxPitch + ". We require minPitch >= 0, maxPitch <= 127 & maxPitch > minPitch.");
    }

  }

  /**
   * Adds a Note object to the line. Is configured to throw an exception if any of the input params
   * are -1
   * 
   * @param timestamp The timestamp at which the note occurs (in ticks)
   * @param duration The duration of the note
   * @param pitch The pitch of the note
   *        https://andymurkin.files.wordpress.com/2012/01/midi-int-midi-note-no-chart.jpg
   * @param velocity The velocity of the note
   */
  public void addNoteToLine(long timestamp, long duration, int pitch, int velocity) {

    if (timestamp == -1 || duration == -1 || pitch == -1 || velocity == -1) {
      throw new InvalidParameterException("Input should not be -1");
    } else {
      this.notes.add(new Note(timestamp, duration, pitch, velocity));

      // TODO: CHECK THE NOTE IS ADDED PROPERLY. IF TESTS STILL WORK THEN IT HAS BEEN

    }
  }

  /**
   * Get the number of notes in this line
   * 
   * @return integer value
   */
  public int getLength() {
    return this.notes.size();
  }

  /**
   * Returns the timestamp at a given index
   * 
   * @param i the index of the timestamp to be returned
   * @return the timestamp as a long
   * @throws IndexOutOfBoundsException if the supplied index is out of bounds
   */
  public long getTimeStampAtIndex(int i) {

    if (i > this.notes.size() - 1) {
      throw new IndexOutOfBoundsException("Index out of Bounds");
    }
    if (i < 0) {
      throw new IndexOutOfBoundsException("Index should not be negative");
    }

    return this.notes.get(i).getTimestamp();
  }

  /**
   * Returns the duration at a given index
   * 
   * @param i the index of the duration to be returned
   * @return the duration as a long
   * @throws IndexOutOfBoundsException if the supplied index is out of bounds
   */
  public long getDurationAtIndex(int i) {

    if (i > this.notes.size() - 1) {
      throw new IndexOutOfBoundsException("Index out of Bounds");
    }
    if (i < 0) {
      throw new IndexOutOfBoundsException("Index should not be negative");
    }

    return this.notes.get(i).getDuration();
  }

  /**
   * Returns the pitch at a given index
   * 
   * @param i the index of the pitch to be returned
   * @return the pitch as an int (see
   *         https://andymurkin.files.wordpress.com/2012/01/midi-int-midi-note-no-chart.jpg )
   * @throws IndexOutOfBoundsException if the supplied index is out of bounds
   */
  public int getPitchAtIndex(int i) {

    if (i > this.notes.size() - 1) {
      throw new IndexOutOfBoundsException("Index out of Bounds");
    }
    if (i < 0) {
      throw new IndexOutOfBoundsException("Index should not be negative");
    }

    return this.notes.get(i).getPitch();
  }

  /**
   * Returns the velocity at a given index
   * 
   * @param i the index of the velocity to be returned
   * @return the velocity as an int (see
   *         https://andymurkin.files.wordpress.com/2012/01/midi-int-midi-note-no-chart.jpg )
   * @throws IndexOutOfBoundsException if the supplied index is out of bounds
   */
  public int getVelocityAtIndex(int i) {

    if (i > this.notes.size() - 1) {
      throw new IndexOutOfBoundsException("Index out of Bounds");
    }
    if (i < 0) {
      throw new IndexOutOfBoundsException("Index should not be negative");
    }

    return this.notes.get(i).getVelocity();
  }

  /**
   * Get the ticks per beat of this line (important to get right for midi export or they can be
   * slowed down/sped up)
   * 
   * @return the ticks per beat as an int
   */
  public int getTicksPerBeat() {
    return this.ticksPerBeat;
  }

  /**
   * Get the tempo division type for this line (important to get right for midi export or they can
   * be slowed down/sped up)
   * 
   * @return the division type as a float
   */
  public float getDivisionType() {
    return this.divisionType;
  }

  /**
   * @return The list of notes constituting this line
   */
  public List<Note> getNotes() {
    return this.notes;
  }

  /**
   * @return the maxPitch
   */
  public int getMaxPitch() {
    if (this.maxPitch == -1) {
      logger.warn(
          "maxPitch does not seem to have been set for this Line object, it seems to be a melody");
    }
    return maxPitch;
  }

  /**
   * @return the minPitch
   */
  public int getMinPitch() {
    if (this.minPitch == -1) {
      logger.warn(
          "minPitch does not seem to have been set for this Line object, it seems to be a melody");
    }
    return minPitch;
  }

}
