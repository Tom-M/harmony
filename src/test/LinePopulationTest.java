package test;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

import org.junit.Test;

import org.junit.Assert;
import main.Line;
import main.LinePopulation;
import main.Note;

public class LinePopulationTest {

  @Test
  public void testAddNewLineWithMelodyAsTemplate() throws InvalidMidiDataException, IOException {
    Line testMelody = new Line("src/test/Resources/MidiStaticTest_Resource1.mid");
    
    LinePopulation testPop = new LinePopulation(testMelody);
    
    testPop.addNewLineWithMelodyAsTemplate(5, 10);
    
    Assert.assertNotNull(testPop.getLineAtIndex(1));
    
    //Check that all the notes lie within the specified pitch boundaries
    for (Note note : testPop.getLineAtIndex(1).getNotes()) {
      Assert.assertTrue(note.getPitch() >= 5 && note.getPitch() <= 10);
    }
    
    //Check there are the expected number of notes
    Assert.assertEquals(testPop.getLineAtIndex(1).getNotes().size(), testMelody.getNotes().size());
    
  }

  @Test
  public void testGetPitchFitnessScore() throws InvalidMidiDataException, IOException {
    Line testMelody = new Line("src/test/Resources/MidiStaticTest_Resource1.mid");
    
    LinePopulation testPop = new LinePopulation(testMelody);
    
    testPop.addNewLineWithMelodyAsTemplate(5, 10);
    
    //In this case, all the notes are of the same length, so we expect the score to only be informed by a single note starting at the same time
    int MelodyStartingPitch = testMelody.getNotes().get(0).getPitch();
    int HarmonyStartingPitch = testPop.getLineAtIndex(1).getNotes().get(0).getPitch();
    int steps = Math.abs(MelodyStartingPitch - HarmonyStartingPitch);
    
    double consonanceScore = testPop.getPitchFitnessScore(1, 0);
    
    if (steps == 0) {
      Assert.assertTrue(consonanceScore == 0.25);
    } else if (steps % 7 == 0 || steps % 12 == 0) {
      Assert.assertTrue(consonanceScore == 1.0);
    } else if (steps % 3 == 0 || steps % 4 == 0 || steps % 8 == 0 || steps % 9 == 0) {
      Assert.assertTrue(consonanceScore == 0.75);
    } else {
      Assert.assertTrue(consonanceScore == 0.0);
    }
    
    //TODO Add a test case for the more complex case with multiple notes on at the same time
    
  }

}
