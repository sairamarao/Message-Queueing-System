package RequestSender;

import java.util.*;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.net.*;


public class SendRequest {

	private static int noOfClients;
	private static List<Thread> threadPool = new ArrayList<Thread>();
	public static void main (String args[]) {
			Properties sprop = new Properties();
			String host = null;
		try{
                	sprop.load(new FileInputStream("Client.properties"));
                        host = sprop.getProperty("MW");
			noOfClients = Integer.valueOf(sprop.getProperty("Clients")).intValue();
			for(int i = 0; i < noOfClients; i++) {
				ClientThread thread = new ClientThread(i,host);
				thread.start();
				threadPool.add(thread);
			}
			System.out.println("Started Clients.");
			for(Iterator i = threadPool.iterator(); i.hasNext();){
				ClientThread ct = (ClientThread)i.next();
				ct.join();
			}
			int port = 5434;
			InetAddress address = InetAddress.getByName(host);
			Socket conn = new Socket(address,port);
			BufferedOutputStream buf = new BufferedOutputStream(conn.getOutputStream());
			OutputStreamWriter osw = new OutputStreamWriter(buf, "US-ASCII");
			osw.write("STOP");
			osw.flush();
			conn.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public synchronized void shutdown() {
		
		System.out.println( "Shutting down request threads..." );
		for(Iterator i = threadPool.iterator(); i.hasNext();) {
			ClientThread ct = (ClientThread)i.next();
			ct.killThread();
		}
	}
	
}
