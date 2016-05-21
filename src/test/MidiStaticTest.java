package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
  public void testSaveLineToMidiFile() throws InvalidMidiDataException, IOException {

  Line testMelody = MidiStatic.getMelodyFromFile("src/test/Resources/MidiStaticTest_Resource1.mid");
  
  String filepath = "src/test/Resources/testExport1.mid";
  
  MidiStatic.saveLineToMidiFile(testMelody, filepath);
  
  //Reimport the file to check everything is still working
  testMelody = MidiStatic.getMelodyFromFile(filepath);
  
  //Number of notes should be the same as before
  Assert.assertTrue(testMelody.getLength() == 19);
  
  //Get rid of the file as we don't need it anymore
  Files.delete(Paths.get(filepath));    
      
  }

}
