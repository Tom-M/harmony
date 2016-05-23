package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
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

  /**
   * Takes a string path (pointing to a midi file) and converts the midi data into a Line object
   * 
   * Note: This method is currently quite brittle, and cannot be relied upon to work for complex
   * midi files. The midi files I tested it with are straightforward, single channel sequences of
   * Note_ON and Note_OFF events of the kind which can be easily generated with ScoreCloud
   * http://scorecloud.com/ (Midi export requires plus subscription for about $5 per month and you
   * get a free month).
   * 
   * @param filepath The string filepath pointing to the midi file
   * @return A Line object
   * @throws IOException
   * @throws InvalidMidiDataException
   */
  public static Line getMelodyFromFile(String filepath)
      throws InvalidMidiDataException, IOException {

    // Check the filepath points (in principle) to a midi file
    if (!filepath.endsWith(".midi") && !filepath.endsWith(".mid")) {
      throw new InvalidParameterException("The filepath string must point to a midi file");
    }

    File midiImport = new File(filepath);

    // Obtain a sequence from the midi file
    Sequence sequence = MidiSystem.getSequence(midiImport);

    Line melody = new Line(sequence.getResolution(), sequence.getDivisionType());

    // Iterate over the tracks in the sequence
    for (Track track : sequence.getTracks()) {

      // Look at each event in the track
      for (int i = 0; i < track.size(); i++) {

        // These values will trigger an error in the event data could not be properly retrieved
        long timestamp = -1;
        long duration = -1;
        int pitch = -1;
        int velocity = -1;

        // Get the current midi event
        MidiEvent event = track.get(i);

        // Get the timestamp for the event, in ticks
        timestamp = event.getTick();

        MidiMessage message = event.getMessage();

        // We are only interested in "Note On" and "Note Off" events, so we search for the On events
        // then look ahead to the Off events
        if (message instanceof ShortMessage) {
          ShortMessage sm = (ShortMessage) message;
          if (sm.getCommand() == NOTE_ON) {
            pitch = sm.getData1();
            velocity = sm.getData2();

            // Search ahead for the "Note Off" event
            int j = i;
            MidiEvent nextEvent = null;
            MidiMessage nextMessage = null;
            ShortMessage sm2 = null;
            long endTime = 0;
            int offPitch = -1;
            // The correct "Note Off" event is the subsequent short message Note Off event with the
            // same pitch as the Note On event
            // There is no guarantee that any subsequent "Note Off" will be the correct one, as the
            // notes can overlap even in the same chanell
            while (!(nextMessage instanceof ShortMessage) || !(sm2.getCommand() == NOTE_OFF)
                || offPitch != pitch) {
              nextEvent = track.get(j + 1);
              nextMessage = nextEvent.getMessage();
              sm2 = (ShortMessage) nextMessage;
              offPitch = sm2.getData1();
              j++;
              if (j > track.size()) {
                throw new IndexOutOfBoundsException(
                    "I have reached the end of the track without finding the 'Note Off' signal");
              }
              endTime = nextEvent.getTick();
            }
            duration = endTime - timestamp;

            // Add the note to the melody line
            melody.addNoteToLine(timestamp, duration, pitch, velocity);
          }
        }
      }
    }
    return melody;
  }

  /**
   * An override for saveLinesToMidiFile. This one saves it to the savedMidis folder of this
   * project, and names the file with the date and time. These files are ignored by git.
   * 
   * @param lines The list of lines to be overlaid and saved
   * @throws InvalidMidiDataException
   * @throws IOException
   */
  public static void saveLinesToMidiFile(List<Line> lines)
      throws InvalidMidiDataException, IOException {

    // Get the date and time for use in the default extension
    DateFormat df = new SimpleDateFormat("dd-MM-yy_HH-mm-ss");
    Date dateobj = new Date();

    saveLinesToMidiFile(lines, "src/savedMidis/" + df.format(dateobj) + ".mid");
  }

  /**
   * Takes a list of lines as input, overlays them in a midi sequence and saves them out to a
   * specified destination. All lines are currently saved to a single channel with the piano sound
   * 
   * @param lines The list of lines to be overlaid and saved
   * @param filepath The filepath to be saved to. Must end with .mid
   * @throws InvalidMidiDataException
   * @throws IOException
   */
  public static void saveLinesToMidiFile(List<Line> lines, String filepath)
      throws InvalidMidiDataException, IOException {

    if (!filepath.endsWith(".mid")) {
      throw new InvalidParameterException("The filepath must have suffix .mid");
    }

    // At the moment we assume that all the lines supplied have the same division type and ticks per
    // beat as the first line. If the timings get messed up, this may well be why
    float divisionType = lines.get(0).getDivisionType();
    int ticksPerBeat = lines.get(0).getTicksPerBeat();

    Sequence s = new Sequence(divisionType, ticksPerBeat);

    for (Line l : lines) {
      addLineToMidiSequence(s, l);
    }

    File f = new File(filepath);
    MidiSystem.write(s, 1, f);

  }

  /**
   * Take a Line object as input and adds it to a midi sequence. Based on (more or less verbatim)
   * http://www.automatic-pilot.com/midifile.html
   * 
   * @param s The Sequence to which the line will be added.
   * @param line The line to be added to the midi sequence
   * @throws InvalidMidiDataException
   */
  private static void addLineToMidiSequence(Sequence s, Line line) throws InvalidMidiDataException {

    Track t = s.createTrack();

    // Turn on general midi sound set
    byte[] b = {(byte) 0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte) 0xF7};
    SysexMessage sm = new SysexMessage();
    sm.setMessage(b, 6);
    MidiEvent me = new MidiEvent(sm, (long) 0);
    t.add(me);

    // Set track name
    MetaMessage mt = new MetaMessage();
    String TrackName = new String("midifile track");
    mt.setMessage(0x03, TrackName.getBytes(), TrackName.length());
    me = new MidiEvent(mt, (long) 0);
    t.add(me);

    // Set omni on
    addShortMessageToTrack(t, 0xB0, 0x7D, 0x00, 0, 0);

    // Turn poly on
    addShortMessageToTrack(t, 0xB0, 0x7F, 0x00, 0, 0);

    // Set instrument to piano
    addShortMessageToTrack(t, 0xC0, 0x00, 0x00, 0, 0);

    for (int i = 0; i < line.getLength(); i++) {

      // Note on at t=timestamp
      addShortMessageToTrack(t, NOTE_ON, line.getPitchAtIndex(i), line.getVelocityAtIndex(i),
          line.getTimeStampAtIndex(i), 0);

      // Note off at timestamp+duration
      addShortMessageToTrack(t, NOTE_OFF, line.getPitchAtIndex(i), 0x00,
          line.getTimeStampAtIndex(i), line.getDurationAtIndex(i));

    }

    // Set end of track a short while after the last note ends
    long endTimeStamp = line.getTimeStampAtIndex(line.getLength() - 1)
        + line.getDurationAtIndex(line.getLength() - 1) + 20;
    mt = new MetaMessage();
    byte[] bet = {}; // empty array
    mt.setMessage(0x2F, bet, 0);
    me = new MidiEvent(mt, endTimeStamp);
    t.add(me);

  }

  /**
   * A private method to keep saveLineToMidiFile neater. Add a short midi message to the Track t.
   * 
   * @param t The track the message will be added to
   * @param status The MIDI status byte for this short message, defining in broad terms what the
   *        message does (e.g. NOTE_ON, NOTE_OFF)
   * @param dataByte1 The first databyte as an int or byte. In the case of NOTE_ON/NOTE_OFF this is
   *        the pitch
   * @param dataByte2 The second databyte as an int or byte. In the case of NOTE_ON/NOTE_OFF this is
   *        the velocity
   * @param timestamp The time at which the event in this message occurs (as a long)
   * @param duration The duration of the event. If status != NOTE_OFF then this should be set to 0
   * @throws InvalidMidiDataException
   */
  private static void addShortMessageToTrack(Track t, int status, int dataByte1, int dataByte2,
      long timestamp, long duration) throws InvalidMidiDataException {

    ShortMessage mm = new ShortMessage();
    mm.setMessage(status, dataByte1, dataByte2);
    MidiEvent me = new MidiEvent(mm, timestamp + duration);
    t.add(me);

  }

}
