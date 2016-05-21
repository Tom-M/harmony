package test;

import static org.junit.Assert.*;

import java.io.IOException;

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

}
