package ClientReceiver;

import java.util.*;
import java.util.concurrent.Semaphore;
import DatabaseConnector.DBQueue;
import java.io.*;
//import Server.exception.*;

public class RequestQueue {
	private LinkedList<Object> queue = new LinkedList<Object>();
	private Locking semLocks;
	private Responses responses;
	private Compute comp;
	private int clientWorkers = 0;
	private String requestHandlerClassName;
	private List<Thread> threadPool = new ArrayList<Thread>();
	public DBQueue dbQueue;
	private boolean isrunning = true;
	public int arrivalCount = 0;
	private MServer ms;
	
	public RequestQueue(MServer serv, String requestHandlerClassName,Responses resp, Locking sl, DBQueue dbQueue, int cWorkers) {
		this.requestHandlerClassName = requestHandlerClassName;
		this.semLocks = sl;
		this.ms = serv;
		this.responses = resp;
		this.clientWorkers = cWorkers;
		this.comp = new Compute(this);
		this.dbQueue = dbQueue;
		for(int i=0; i<this.clientWorkers; i++) {
			RequestThread thread = new RequestThread(this, this.ms, this.comp, this.dbQueue, i, 
					requestHandlerClassName,this.responses,this.semLocks);
			thread.start();
			this.threadPool.add(thread);
		}
		this.comp.start();
	}
	
	public String getRequestHandlerClassName()
    {
        return this.requestHandlerClassName;
    }
	
	public synchronized void add(Object o ) throws MWException {
		this.queue.addLast(o);
		notify();//Change to NotifyAll() to check!	
		this.arrivalCount++;
	}
	
	public synchronized Object getNextObject() {
		while( queue.isEmpty() ) {
			try {
				if(!isrunning) {
					return null;
				}
				wait();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		if(!isrunning)
			return null;
		
		return queue.removeFirst();
	}
	
	public synchronized void shutdown() {

		System.out.println( "Shutting down request threads..." );
		this.isrunning = false;
		for(Iterator i = this.threadPool.iterator(); i.hasNext();) {
			RequestThread rt = (RequestThread)i.next();
			rt.killThread();
		}
		notifyAll();//Wake up all threads if any thread is waiting.
	}
}
