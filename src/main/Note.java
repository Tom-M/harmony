package main;

import java.security.InvalidParameterException;

/**
 * Note objects form the building blocks of line objects
 * 
 * @author tmanf
 *
 */
public class Note {

  private long timestamp;
  private long duration;
  private int pitch;
  private int velocity;

  /**
   * Create a note object with a supplied timestamp, duration, pitch and velocity
   * 
   * @param timestamp The timestamp (in ticks) at which this note occurs
   * @param duration The duration (in ticks) of this note
   * @param pitch The pitch of the note (see
   *        https://andymurkin.files.wordpress.com/2012/01/midi-int-midi-note-no-chart.jpg )
   * @param velocity The velocity of the note
   */
  public Note(long timestamp, long duration, int pitch, int velocity) {
    this.timestamp = timestamp;
    this.duration = duration;
    this.pitch = pitch;
    this.velocity = velocity;
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public long getDuration() {
    return this.duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public int getPitch() {
    return this.pitch;
  }

  public void setPitch(int pitch) {
    this.pitch = pitch;
  }

  public int getVelocity() {
    return this.velocity;
  }

  public void setVelocity(int velocity) {
    this.velocity = velocity;
  }

  /**
   * Changes the pitch to any key between minKey and maxKey, inclusive, with equal probability
   * 
   * @param minPitch The lower bound (inclusive) which the note may mutate between 
   * @param maxPitch The upper bound (inclusive) which the note may mutate between 
   */
  public void mutatePitch(int minPitch, int maxPitch) {
    if (minPitch<0 || maxPitch <0 || minPitch>127 || maxPitch >127){
      throw new InvalidParameterException("keys must be between 0<key<127. You have set minKey = "
        +minPitch+" and maxKey = "+maxPitch);
    }
    if (minPitch>maxPitch) {
      throw new InvalidParameterException("minKey cannot be greater than maxKey");
    }
    int newPitch = (int) Math.round((double) minPitch + (maxPitch - minPitch)*Math.random());
    this.setPitch(newPitch);
  }

}
