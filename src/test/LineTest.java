package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import javax.sound.midi.InvalidMidiDataException;

import org.junit.Test;

import org.junit.Assert;
import main.Line;
import main.Note;

public class LineTest {

  @Test
  public void testGetNotesWithinTimeFrame() throws InvalidMidiDataException, IOException {
    Line testMelody = new Line("src/test/Resources/MidiStaticTest_Resource1.mid");

    // See if sampling over no time breaks the code
    testMelody.getNotesWithinTimeFrame(0, 0);

    // See if having a really long duration breaks the code and check all values exist/are not null
    HashMap<Note, Long> testMap = testMelody.getNotesWithinTimeFrame(testMelody.getNotes().get(0).getTimestamp(), 100000000);
    Assert.assertNotNull(testMap.keySet());
    Assert.assertNotNull(testMap.values());
    Assert.assertTrue(testMap.keySet().size() != 0);
    Assert.assertTrue(testMap.values().size() != 0);
    for (Note note : testMap.keySet()) {
      Assert.assertNotNull(note);
      Assert.assertNotNull(testMap.get(note));
    }
    
    // Check the notes in the hashmap all exist within the timeframe
    long startTime = testMelody.getNotes().get(1).getTimestamp();
    System.out.println("Start Time-------" + startTime);
    HashMap<Note, Long> testMap2 = testMelody.getNotesWithinTimeFrame(startTime, 10000);
    for (Note note : testMap2.keySet()) {
      System.out.println(String.valueOf(note.getTimestamp() + note.getDuration()));
      
      Assert.assertTrue((note.getTimestamp() >= startTime
         || (note.getTimestamp() + note.getDuration() >= startTime))
          && note.getTimestamp() < startTime + 10000);
    }
  }

}
