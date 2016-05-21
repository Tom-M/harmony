package main;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Line {

  // Ordered list of ordered quadruplets (timestamp, duration, pitch, velocity) representing the
  // sequence of notes in this line
  private List<Long> timestamps;
  private List<Long> durations;
  private List<Integer> pitches;
  private List<Integer> velocities;

  public Line() {
    timestamps = new ArrayList<Long>();
    durations = new ArrayList<Long>();
    pitches = new ArrayList<Integer>();
    velocities = new ArrayList<Integer>();
  }

  /**
   * Adds a note to the line. Is configured to throw an exception if any of the input params are -1
   * 
   * @param timestamp The timestamp at which the note occurs (in ticks)
   * @param duration The duration of the note
   * @param pitch The pitch of the note https://andymurkin.files.wordpress.com/2012/01/midi-int-midi-note-no-chart.jpg
   * @param velocity The velocity of the note 
   */
  public void addNoteToLine(long timestamp, long duration, int pitch, int velocity) {

    if (timestamp == -1 || duration == -1 || pitch == -1 || velocity == -1) {
      throw new InvalidParameterException("Input should not be -1");
    } else {
      this.timestamps.add(timestamp);
      this.durations.add(duration);
      this.pitches.add(pitch);
      this.velocities.add(velocity);
    }
  }

  /**
   * Get the number of notes in this line
   * 
   * @return integer value
   */
  public int getLength() {
    return this.timestamps.size();
  }

}
