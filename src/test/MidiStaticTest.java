package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;

import org.junit.Assert;
import org.junit.Test;

import main.Line;
import main.MidiStatic;

public class MidiStaticTest {

  @Test
  public void testGetMeoldyFromFile() throws InvalidMidiDataException, IOException {

  Line testMelody = MidiStatic.getMelodyFromFile("src/test/Resources/MidiStaticTest_Resource1.mid");
  
  Assert.assertTrue(testMelody.getLength() == 19);
    
  }
  
  @Test
  public void testSaveLinesToMidiFile() throws InvalidMidiDataException, IOException {

  //First a test involving a single melody
  Line testMelody = MidiStatic.getMelodyFromFile("src/test/Resources/MidiStaticTest_Resource1.mid");
  
  String filepath = "src/test/Resources/testExport1.mid";
  
  List<Line> lines = new ArrayList<Line>();
  lines.add(testMelody);
  
  MidiStatic.saveLinesToMidiFile(lines, filepath);
  
  //Reimport the file to check everything is still working
  testMelody = MidiStatic.getMelodyFromFile(filepath);
  
  //Number of notes should be the same as before
  Assert.assertTrue(testMelody.getLength() == 19);
  
  //Get rid of the file as we don't need it anymore
  Files.delete(Paths.get(filepath));   
  
  //Second a test for multiple lines
  Line testMelody2 = MidiStatic.getMelodyFromFile("src/test/Resources/MidiStaticTest_Resource2.mid");
  
  lines.add(testMelody2);
  
  filepath = "src/test/Resources/testExport2.mid";
  
  //Check we don't throw an error
  MidiStatic.saveLinesToMidiFile(lines, filepath);
  
  //Get rid of the file as we don't need it anymore
  Files.delete(Paths.get(filepath));  
  }

}
