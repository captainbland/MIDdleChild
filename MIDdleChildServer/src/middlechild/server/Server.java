package middlechild.server;

import javax.sound.midi.*;

import java.net.*;
import java.io.*;
import java.util.*;
public class Server {
	public static void main(String... args) throws InvalidMidiDataException {
		Sequencer sequencer;
		Sequence sequence;
		ArrayList<Socket> connections; 
		ServerSocket server;
		
		
		try {
            File input = new File("test.mid");
            sequence = MidiSystem.getSequence(input);

            server = new ServerSocket(25556);
            ConnectionFinder cfinder = new ConnectionFinder(server);
            Thread finderRunner = new Thread(cfinder);
            
            
            finderRunner.start();
            
            while(cfinder.getConnectionsList().size() == 0) {
            	//wait for somebody to join...
            }
            
            //start streaming!
            for(Socket skt : cfinder.getConnectionsList()) {
            	
            }
            
            
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static class ConnectionFinder implements Runnable{
		ArrayList<Socket> connections;	
		ServerSocket serv;
		
		public ConnectionFinder(ServerSocket serv) {
			this.serv = serv;
		}
		
		boolean quit = false;
	    public void waitForConnections() throws IOException {
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
	    
	    public ArrayList<Socket> getConnectionsList() {
	    	return connections;
	    }

		@Override
		public void run() {
			this.connections = new ArrayList<Socket>();

			try {
				waitForConnections();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
}
