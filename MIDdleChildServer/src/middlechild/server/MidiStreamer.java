package middlechild.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import middlechild.common.*;

public class MidiStreamer {
	List<Socket> connections;
	Sequence sequence;
	ArrayList<TrackTime> tracktimes = new ArrayList<TrackTime>();
	
	long currentattick = 0;
	
	public MidiStreamer(List<Socket> connections, Sequence sequence) {
		System.out.println("CONSTRUCTOR CALL!");
		this.sequence = sequence;
		this.connections = connections;
		tracktimes = formTrackTimes();
		startStreaming();
	}
	
	private ArrayList<TrackTime> formTrackTimes() {
		
		ArrayList<TrackTime> result = new ArrayList();
		for(int track = 0; track < sequence.getTracks().length; track++) {
			Track t = sequence.getTracks()[track];
			for(int i = 0; i < t.size(); i++) {
				TrackTime tracktime = new TrackTime();
				MidiEvent evt = t.get(i);
				long time = evt.getTick();
				MidiMessage msg = evt.getMessage();
				byte[] msgdat = msg.getMessage();
				
				tracktime.time = time;
				tracktime.track = track;
				tracktime.message = msgdat;
				
				result.add(tracktime);
				
			}
		}
		
		return result;
	}
	
	public static class TrackTimeComparator implements Comparator<TrackTime> {
		public int compare(TrackTime t1, TrackTime t2) {
			return (int) (t1.time - t2.time);
		}
	}
	
	public void startStreaming() {
		//sort list:
		
		Collections.sort(tracktimes, new TrackTimeComparator());
		
		//now just stream - sends per note, then per socket
		for(TrackTime tt : tracktimes) {
			for(Socket skt : connections) {
				byte[] tosend = tt.toByteArray();
				try {
					skt.getOutputStream().write(tosend);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
	
	

}