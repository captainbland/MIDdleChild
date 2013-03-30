package middlechild.server;

import javax.sound.midi.*;


import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
public class Server {
	

		
	
	
	public static void main(String... args) throws InvalidMidiDataException {
		Sequencer sequencer;
		Sequence sequence;
		List<Socket> connections; 
		ServerSocket server;
		
		
		try {
            File input = new File("track.mid");
            sequence = MidiSystem.getSequence(input);

            server = new ServerSocket(25556);
            ConnectionFinder cfinder = new ConnectionFinder(server);
            Thread finderRunner = new Thread(cfinder);
            
            
            finderRunner.start();
            

            while(cfinder.getConnectionsList().size() == 0) {
            	System.out.println(cfinder.getConnectionsList().size());
            }
            
            connections = cfinder.getConnectionsList();
            
            MidiStreamer midstream = new MidiStreamer(connections, sequence);            
            
            
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static class ConnectionFinder implements Runnable{
		List<Socket> connections= new ArrayList<Socket>();
		ServerSocket serv;
		
		public ConnectionFinder(ServerSocket serv) {
			this.serv = serv;
		}
		
		boolean quit = false;
	    public synchronized void waitForConnections() throws IOException {
	    	while(!quit) {
	    		//accept connections
	    		System.out.println("Looking...");
	    		Socket skt = serv.accept();
	    		connections.add(skt);
	    		String teststr = "Hello client!";
	    		byte[] test =  teststr.getBytes();
	    		
	    		
	    		skt.getOutputStream().write(test);
	    		System.out.println("Accepted connection: " + skt.getInetAddress().getHostAddress() );
	    	}
	    	
	    	for(Socket skt : connections) {
	    		skt.close();
	    	}
	    	
	    	serv.close();
	    	
	    	
	    }
	    
	    public void setQuit() {
	    	quit = true;
	    }
	    
	    public List<Socket> getConnectionsList() {
	    	return connections;
	    }

		@Override
		public void run() {

			try {
				waitForConnections();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
}
