package main;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LinePopulation objects contain lines and methods for their interaction
 * 
 * @author tmanf
 *
 */
public class LinePopulation {

  static Logger logger = LoggerFactory.getLogger(LinePopulation.class);

  /**
   * A list of lines which constitute the harmonies in this song
   */
  private List<Line> lines;

  /**
   * This constructor takes a Line object and adds it as the first element in the line population.
   * Use .addNewLine to add unoptimised harmony lines which can then be optimised
   * 
   * @param melody The Line which will be used to generate the harmonies
   */
  public LinePopulation(Line melody) {
    this.lines = new ArrayList<Line>();
    this.lines.add(melody);
  }

  /**
   * Add a new line to the line population. The new line will have the same horizontal note
   * placement as the melody line, but the pitches will all be randomly located between minPitch and
   * maxPitch
   * 
   * @param minPitch The minimum pitch which this note will be allowed to vary between
   * @param maxPitch The maximum pitch which this note will be allowed to vary between
   */
  public void addNewLineWithMelodyAsTemplate(int minPitch, int maxPitch) {
    Line newLine = new Line(this.lines.get(0), minPitch, maxPitch);
    this.lines.add(newLine);
  }

  /**
   * @return The melody line for this population
   */
  public Line getMelody() {
    // Melody line is always the first added in the constructor
    return this.lines.get(0);
  }

  public Line getLineAtIndex(int index) {
    if (index == 0) {
      logger
          .warn("Index of 0 supplied, returning melody. To be safe, use .getMelody if you want the"
              + " melody line, as this one behaves differently, for example its notes cannot be mutated");
    }
    return this.lines.get(index);
  }

  /**
   * Calculate the pitch fitness of the note with index noteIndex belonging to the line in this
   * population with index lineIndex.
   * 
   * The pitch fitness gives an idea of how good the harmonic intervals between this note and the
   * notes in the other lines are.
   * 
   * The pitch fitness is calculated by first finding all the notes in all the other lines which are
   * 'on' at the same time as this note. The individual pitch fitness of this note to each of those
   * is then separately calculated. They are then summed together with a weighting corresponding to
   * the time for which they are on at the same time as the note in question (shorter times
   * contribute less to the fitness score)
   * 
   * @param lineIndex The index of the line within this LinePopulation's list of lines. Must not be
   *        the melody (index 0)
   * @param noteIndex The index of the note whose fitness is to be calculated
   * @return The pitch fitness score as a double between 0 (lowest score) and 1 (highest score)
   */
  public double getPitchFitnessScore(int lineIndex, int noteIndex) {

    // Check the parameters
    if (lineIndex > lines.size() - 1 || lineIndex < 1) {
      throw new InvalidParameterException(lineIndex + " is not a valid lineIndex value. "
          + "lineindex must point to one of the lines in the population which isn't "
          + "the melody line at index 0. In this case that means 0 < lineIndex < "
          + String.valueOf(lines.size() - 1));
    }
    if (noteIndex > lines.get(lineIndex).getLength() - 1 || noteIndex < 0) {
      throw new InvalidParameterException(noteIndex + " is not a valid note index. "
          + "The line specified has " + lines.get(lineIndex).getLength() + " notes, so"
          + " noteIndex must be between 0 < noteIndex < "
          + String.valueOf(lines.get(lineIndex).getLength() - 1));
    }

    Line lineInQuestion = this.lines.get(lineIndex);
    Note noteInQuestion = lineInQuestion.getNotes().get(noteIndex);

    // First we need to build a HashMap of Notes to the times for which they are on at the same time
    // as this note
    // We do this by looking at each other line in turn and building a hashmap for each, then adding
    // these to a list and concatenating them
    HashMap<Note, Long> concatenatedMapOfNotesToDurations = new HashMap<Note, Long>();
    for (int i = 0; i < this.lines.size(); i++) {

      if (i == lineIndex) {
        // We don't consider the line we're on
        continue;
      }

      HashMap<Note, Long> mapOfNotesToDurations = this.lines.get(i)
          .getNotesWithinTimeFrame(noteInQuestion.getTimestamp(), noteInQuestion.getDuration());
      for (Note note : mapOfNotesToDurations.keySet()) {
        concatenatedMapOfNotesToDurations.put(note, mapOfNotesToDurations.get(note));
      }
    }
    // Now we have a hashmap of all the notes in all the other lines which are on at the same time
    // as this note mapped to the time for which they are on.
    // The score is then a weighted sum of the individual consonance scores

    double sum = 0;

    for (Note note : concatenatedMapOfNotesToDurations.keySet()) {
      // Multiply the consonance score by the time for which the note is on and add it to the total
      // sum
      sum += noteInQuestion.calculateIPitchConsonanceScore(note)
          * concatenatedMapOfNotesToDurations.get(note);
    }

    // To normalise the score, divide it by the total time the note is on for * the number of
    // harmony lines, so that we get a score between 0 and 1
    return sum / (noteInQuestion.getDuration() * (this.lines.size() - 1));

  }

  /**
   * Compute the average of getPitchFitnessScore() as taken over all the notes in this population
   * 
   * @return The average fitness as a double between 0 and 1
   */
  public double getAverageFitnessScore() {

    // Count the number of values whose score we are computing
    int totalScores = 0;
    // Get the sum of all the scores
    double sumOfScores = 0;

    for (int l = 0; l < this.lines.size(); l++) {
      for (int n = 0; n < this.lines.get(l).getLength(); n++) {
        totalScores += 1;
        sumOfScores += this.getPitchFitnessScore(l, n);
      }
    }
    return sumOfScores / totalScores;

  }

}
