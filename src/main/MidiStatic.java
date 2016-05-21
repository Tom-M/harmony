package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * 
 * A class containing static methods for handling midi files
 * 
 * @author tmanf
 *
 */
public class MidiStatic {

  // Define the midi bytes which will be understood as switching a note on/off
  private static final int NOTE_ON = 0x90;
  private static final int NOTE_OFF = 0x80;



  // A midi file can contain as many tracks as you like, each containing up to 16 channels (this is
  // a way to get around the 16 channel limit)--see
  // https://docs.oracle.com/javase/7/docs/api/javax/sound/midi/Track.html

  /**
   * Takes a string path (pointing to a midi file) and converts the midi data into a Line object
   * 
   * @param filepath The string filepath pointing to the midi file
   * @return A Line object 
   * @throws IOException 
   * @throws InvalidMidiDataException 
   */
  public Line getMeoldyFromFile(String filepath) throws InvalidMidiDataException, IOException{
   
    //Check the filepath points (in principle) to a midi file
    if (!filepath.endsWith(".midi") && !filepath.endsWith(".mid") ){
      throw new InvalidParameterException("The filepath string must point to a midi file");
    }
    
    File midiImport = new File(filepath);
    
    //Obtain a sequence from the midi file
    Sequence sequence = MidiSystem.getSequence(midiImport);
     
    Line melody = new Line();
    
    //Iterate over the tracks in the sequence
    for(Track track : sequence.getTracks()){
      
      // Look at each event in the track
      for (int i = 0; i < track.size(); i++) {
        
        //These values will trigger an error in the event data could not be properly retrieved
        long timestamp = -1;
        long duration = -1;
        int pitch = -1;
        int velocity = -1;
        
        // Get the current midi event
        MidiEvent event = track.get(i);
        
        // Get the timestamp for the event, in ticks
        timestamp = event.getTick();  
        
        MidiMessage message = event.getMessage();
        
        //We are only interested in "Note On" and "Note Off" events, so we search for the On events then look ahead to the Off events
        if (message instanceof ShortMessage) {
          ShortMessage sm = (ShortMessage) message;
          if (sm.getCommand() == NOTE_ON) {
            pitch = sm.getData1();
            velocity = sm.getData2();
            
            //Search ahead for the "Note Off" event
            int j = i;
            MidiEvent nextEvent = null; 
            MidiMessage nextMessage = null;
            ShortMessage sm2 = null; 
            int offPitch = -1;
            //The correct "Note Off" event is the subsequent short message Note Off event with the same pitch as the Note On event 
            // There is no guarantee that any subsequent "Note Off" will be the correct one, as the notes can overlap even in the same chanell 
            while (!(nextMessage instanceof ShortMessage) || !(sm2.getCommand() == NOTE_OFF) || offPitch != pitch){
              nextEvent = track.get(j+1);
              nextMessage = nextEvent.getMessage(); 
              sm2 = (ShortMessage) nextMessage; 
              offPitch = sm2.getData1();
              j++;
              if (j > track.size()){
                throw new IndexOutOfBoundsException("I have reached the end of the track without finding the 'Note Off' signal");
              }
            long endTime = nextEvent.getTick();
            duration = endTime - timestamp;
            }
            
            //Add the note to the melody line
            melody.addNoteToLine(timestamp,  duration,  pitch,  velocity); 
          }
        } 
      }  
    }
    return melody;
  }

}
