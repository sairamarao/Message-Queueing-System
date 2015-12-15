package ClientReceiver;
import java.net.*;
import javax.net.*;

import DatabaseConnector.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public abstract class MServer extends Thread{
	protected ServerSocket serverSocket;
	protected boolean isrunning;
	protected int port;
	protected int backlog;
	protected RequestQueue requestQueue;
	protected Locking semLocks;
	protected DBQueue dbQueue;
	protected Responses responses;
	protected Semaphore[] semaphores;
	
	public MServer(int port, int backlog, String reqHandClass, int CWorkers) {
		this.port = port;
		this.backlog = backlog;
		this.responses = new Responses(CWorkers);
		this.semLocks = new Locking(CWorkers);
		this.dbQueue = new DBQueue(this.responses, this.semLocks, CWorkers);
		this.requestQueue = new RequestQueue(this, reqHandClass, this.responses, this.semLocks, this.dbQueue, CWorkers);
		
	}
	
	public void startServer() {
		try {
			ServerSocketFactory ssf = ServerSocketFactory.getDefault();
			serverSocket = ssf.createServerSocket(this.port, this.backlog);
			this.start();	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stopServer() {
		try {
			this.isrunning = false;
			this.serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		System.out.println("Server started. Listening on port: " + this.port);
		this.isrunning = true;
		while( this.isrunning ) {
			try {
				Socket s = serverSocket.accept();
				String t1 = System.currentTimeMillis()+""; 
				InetAddress addr = s.getInetAddress();
				System.out.println("Received a new connection from ("+addr.getHostAddress()+"): "+ addr.getHostName());
				Tuple t = new Tuple(s,t1,"");
				this.requestQueue.add(t);
				
			} catch( SocketException se) {
				System.out.println("Server socket closed");
			} catch( Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println( "Server Shutting down..." );
        //this.requestQueue.shutdown();
	}

}
