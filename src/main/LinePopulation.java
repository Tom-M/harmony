package main;

import java.util.ArrayList;
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

}
