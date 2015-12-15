package ClientReceiver;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.sql.*;

import DatabaseConnector.DBQueue;

public class RequestThread extends Thread{
	
	private RequestQueue queue;
	private Locking semLocks;
	private Responses responses;
	private DBQueue dbQueue;
	private boolean isrunning;
	private boolean isprocessing = false;
	private int threadNumber;
	private Compute comp;
	private RequestHandler requestHandler;
	private MServer ms;
	private int cursor = 0;
	
	public RequestThread(RequestQueue queue, MServer serv, Compute comp,DBQueue dbQueue, 
			int threadNumber, String className, Responses resp, Locking l) {
		this.queue = queue;
		this.dbQueue = dbQueue;
		this.ms = serv;
		this.threadNumber = threadNumber;
		this.semLocks = l;
		this.responses = resp;
		this.comp = comp;
		try {
			this.requestHandler = (RequestHandler)(Class.forName(className).newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getWord(String s) {
		String temp = "";
		int i = cursor;
		
		for(;i < s.length() && s.charAt(i) != ' ';i++) {
			temp += s.charAt(i);
		}
		while(i < s.length() && s.charAt(i) == ' '){i++;}
		cursor = i;
		return temp;
	}
	public boolean isProcessing() {
		return this.isprocessing;
	}

	public void killThread() {
		System.out.println( "[" + threadNumber + "]: Attempting to kill thread..." );
        this.isrunning = false;
	}
	
	public void run() {
		this.isrunning = true;
		while (isrunning) {
			try {
				Object o = queue.getNextObject();
				if (isrunning && o!=null) {//When we kill threads this will be false(race condition may occur.)
					this.isprocessing = true;
					System.out.println( "Request: [" + threadNumber + "]: Processing request..." );
                    
					long serviceStart = System.currentTimeMillis();
					Tuple t = (Tuple) o;
					if(t.rs.equals("STOP")){
						this.queue.add(t);
						this.isprocessing = false;
						this.isrunning = false;
						System.out.println( "Request: [" + threadNumber + "]: Processing finished" );
						continue;
					}
					long waitStart = Long.valueOf(t.timeStamp).longValue();
					t.timeStamp = serviceStart+"";
					/**Process Request**/
					String ret  = this.requestHandler.handleRequest(this.dbQueue, this.threadNumber, t);
                    System.out.println("Return: "+ret);
                    long queryReadEnd = Long.valueOf(getWord(ret)).longValue();
                    ret = ret.substring(this.cursor);
                    this.cursor = 0;
					if(ret.equals("Success")){
                    	System.out.println("Acquiring lock: "+this.threadNumber);
                    	this.semLocks.semaphores[this.threadNumber].acquire();
                    	System.out.println("After releasing lock: "+this.threadNumber);
                    	long serviceEnd = System.currentTimeMillis();
                    	
                    	/**Send Response to Client***/
                    	BufferedOutputStream buf = new BufferedOutputStream(t.s.getOutputStream());
        				OutputStreamWriter osw = new OutputStreamWriter(buf, "US-ASCII");
        				String response = (serviceEnd-queryReadEnd)+" "+(serviceStart-waitStart)+
        						" "+this.responses.response[this.threadNumber]+(char)13;
        				osw.write(response);
        			    osw.flush();
                    	
        			    /**Put Client back in Queue**/
        			    Tuple tt = new Tuple(t.s,System.currentTimeMillis()+"","");
                    	this.queue.add(tt);
                    	
                    	System.out.println("ReqWaitTime: "+(serviceStart-waitStart)+
                    			"ServiceTime: "+(serviceEnd-serviceStart));
                    }
                    else if(ret.equals("STOP")){
                    	Tuple tt = new Tuple(t.s,System.currentTimeMillis()+"","STOP");
                    	this.queue.add(tt);
                    	this.isrunning = false;
                    }
                    else{
                    	System.out.println("Client disconnected.");
                    }
                 }
             	 this.isprocessing = false;
                 System.out.println( "Request: [" + threadNumber + "]: Finished Processing request..." );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println( "Request: [" + threadNumber + "]: Thread shutting down..." );
		this.comp.finish();
		this.ms.isrunning = false;
		try{
		this.ms.serverSocket.close();
		} catch(Exception e){e.printStackTrace();}
	}
}
