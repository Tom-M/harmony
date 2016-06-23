package main;

import java.util.ArrayList;
import java.util.List;

/**
 * LinePopulation objects contain lines and methods for their interaction
 * 
 * @author tmanf
 *
 */
public class LinePopulation {

  /**
   * A list of lines which constitute the harmonies in this song
   */
  private List<Line> lines;
  
  /**
   * This constructor takes a Line object and adds it as the first element in the line population. Use .addNewLine to add unoptimised harmony lines which can then be optimised
   * 
   * @param melody The Line which will be used to generate the harmonies
   */
  public LinePopulation(Line melody) {
    this.lines = new ArrayList<Line>();
    this.lines.add(melody);
  }
  
  public void addNewLineWithMelodyAsTemplate(int minPitch, int maxPitch) {
    Line newLine = new Line(this.lines.get(0), minPitch, maxPitch);
  }
  
}
