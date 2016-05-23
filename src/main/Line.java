package main;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Line {

  // Ordered list of ordered quadruplets (timestamp, duration, pitch, velocity) representing the
  // sequence of notes in this line
  private List<Note> notes;

  private float divisionType;
  private int ticksPerBeat;

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
   * Adds a Note object to the line. Is configured to throw an exception if any of the input params are -1
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
      this.notes.add(new Note(timestamp,duration,pitch,velocity));
      
      //CHECK THE NOTE IS ADDED PROPERLY. IF TESTS STILL WORK THEN IT HAS BEEN
      
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

}
