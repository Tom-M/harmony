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
    if (minPitch < 0 || maxPitch < 0 || minPitch > 127 || maxPitch > 127) {
      throw new InvalidParameterException("keys must be between 0<key<127. You have set minKey = "
          + minPitch + " and maxKey = " + maxPitch);
    }
    if (minPitch > maxPitch) {
      throw new InvalidParameterException("minKey cannot be greater than maxKey");
    }
    int newPitch = (int) Math.round((double) minPitch + (maxPitch - minPitch) * Math.random());
    this.setPitch(newPitch);
  }

  /**
   * Looks at th pitches of this note and a comparison note and returns a score between 0 and 1 for
   * the consonance. 
   * 1.0--Perfect intervals (P5 and Octave) 
   * 0.75--Maj/Min 3rds and 6ths
   * 0.25--Perfect unison (gets a low score as it is consonant but boring) 
   * 0.0--All others (including P4 for the sake of simplicity)
   * 
   * Of course this is extremely rough and won't be adhered to extremely strictly, but it should at
   * least ensure that we don't have a song full of dissonances
   * 
   * @param comparisonNote The note whose pitch will be compared
   * @return A score between 0 and 1, with 1 being most consonant and 0 being least
   */
  public double calculateIPitchConsonanceScore(Note comparisonNote) {

    // We want to calculate the frequency ratio of these two notes.
    int pitchOfThisNote = this.getPitch();
    int pitchOfComparisonNote = comparisonNote.getPitch();

    // This was originally something cleverer, but like so many clever things, it was obtuse and not
    // as good as something simpler, so we just assign a score based on the intervals
    // Of course this is highly subjective, but it at least means that we will usually have more
    // consonances than dissonances in the song

    int semitoneSteps = Math.abs(pitchOfThisNote - pitchOfComparisonNote);

    if (semitoneSteps == 0) {
      // if the notes are the same this is consonant but dull, so assign a low score
      return 0.25;
    } else if (semitoneSteps % 7 == 0 || semitoneSteps % 12 == 0) {
      // The perfect intervals get a high score
      return 1.0;
    } else if (semitoneSteps % 3 == 0 || semitoneSteps % 4 == 0 || semitoneSteps % 8 == 0
        || semitoneSteps % 9 == 0) {
      // The major/minor 3rds and 6ths are imperfect consonances so get upper-middle scores
      return 0.75;
    } else {
      // All of the others get low scores as they are dissonant
      return 0.0;
    }



  }

}
