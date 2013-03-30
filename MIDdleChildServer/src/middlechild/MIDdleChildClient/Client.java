package middlechild.MIDdleChildClient;

import java.io.IOException;
import java.net.*;
import java.io.*;

import javax.sound.midi.*;

public class Client {
	Sequencer sequencer;
	Sequence sequence;
	public void setupMidi() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.start();

		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addMessage(byte[] message, int track, long miditime) {
		if(sequencer.isOpen()) {
			int status = (int)message[0]&0xFF;
			int type = (int)message[1]&0xFF;
			int length = (int)message[2]&0xFF;
			MidiMessage msg;
			byte[] data = new byte[length];
			
			//extract the data bytes
			for(int i = 0; i < length; i++) {
				//the message starts at 3
				data[i] = message[i-3];
			}
			
			//if we're dealing with a midi meta message, make one:
			if(status == 0xFF) {
				MetaMessage meta = new MetaMessage();
				try {
					meta.setMessage(type, data, length);
				} catch (InvalidMidiDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msg = meta;
			} else { //otherwise we're dealing with a short message, so make one of those
				ShortMessage smsg = new ShortMessage();
				if(length > 1) {
					try {
						smsg.setMessage(status, data[0], data[1]);
					} catch (InvalidMidiDataException e) {
						// TODO Auto-generated catch block
						//or not, iunno
						e.printStackTrace();
					}
				}
				
				msg = smsg;
			}
			//then add everything to the event
			MidiEvent evt = new MidiEvent(msg, miditime);
			
			sequence.getTracks()[track].add(evt);
			
		}
	}
	
	public static enum Signal {
		MSG, INSTRUMENT
	}
	
	public static void main(String[] args) {
		
		
		
		try {
			Socket skt = new Socket("localhost",25556);
			System.out.println("TEST!");
			InputStream socketinput = skt.getInputStream();
			while(true) {
				//receive message:
				byte[] buffer = new byte[1024];
				
				int len = socketinput.read(buffer);
				
				/*packet format:
				 * [signal][time][track][message]
				 */
				
				
				String tstr = new String(buffer, "UTF-8");
				tstr = tstr.substring(0,len);
				System.out.println("RECEIVED: " + tstr);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
