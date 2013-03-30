package middlechild.common;


public class TrackTime {
	public byte[] message;
	public long time;
	public int track;
	private final static int byteshift = 8; //8 bits per byte...

	/**
	 * Converts this to a byte array which can be sent
	 * @return
	 */
	public byte[] toByteArray() {
		//total bytes needed  = messagelength + 8 + 4
		byte[] array = new byte[message.length+12];
		
		int timestart = 0;
		int trackstart = 8;
		int messagestart = 12;
		
		//store the time - 8 bytes
		for(int i = 7; i >= 0; i--) {
			array[(7-i)+timestart] = (byte)(time>>(byteshift*i));
		}
		//store the track no. - 4 bytes
		for(int i = 3; i >= 0; i--) {
			array[(3-i)+trackstart] = (byte)(track>>(byteshift*i));
		}
		
		//store the message 
		for(int i = 0; i < message.length; i++) {
			array[i+messagestart] = array[i];
		}
		
		
		return array;
	}
	
	public static TrackTime getTrackTime(byte[] data) {
		TrackTime tt = new TrackTime();
		
		long time = 0;
		int track = 0;
		
		int timeoff = 0;
		int trackoff = 8;
		int msgoff = 12;
		
		//get the time:
		for(int i = 0; i < 8; i++) {
			time += ((long)data[i+timeoff])<<(byteshift*i);
		}
		
		//get the track
		for(int i = 0; i < 4; i++) {
			track += ((int)data[i+trackoff])<<(byteshift*i);
		}
		
		byte[] message = new byte[data.length - 12];
		
		for(int i = 0; i < message.length; i++) {
			message[i] = data[i+msgoff];
		}
		
		tt.time = time;
		tt.track = track;
		tt.message = message;
		
		return tt;
		
	}
}