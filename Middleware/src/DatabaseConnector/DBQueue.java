package DatabaseConnector;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import ClientReceiver.*;
//import Server.exception.*;

public class DBQueue {
	private LinkedList<Object> queue = new LinkedList<Object>();
	private Responses responses;
	private Locking semLocks;
	private int dbWorkers = 0;
	private List<Thread> DBConnectionPool = new ArrayList<Thread>();
	private boolean isrunning = true;
	
	
	public DBQueue(Responses resp, Locking l, int num_responses) {
		this.responses = resp;
		this.semLocks = l;
		Properties props = new Properties();
		try {
			props.load(new FileInputStream("dbPool.properties"));
		} catch (Exception e) {
			System.out.println("Can't read dbPool.properties");
		}
		String dbName="postgres";
		String driver = props.getProperty(dbName + ".driver");
		String url = props.getProperty(dbName + ".url");
		String user = props.getProperty(dbName + ".user");
        String password = props.getProperty(dbName + ".password");
        String dbWorkers_input = props.getProperty(dbName + ".maximum");
        this.dbWorkers = Integer.valueOf(dbWorkers_input).intValue(); 
		try{
			Class.forName(driver);
			
			for(int i=0; i<this.dbWorkers; i++) {
				Connection conn = DriverManager.getConnection(url,user,password);
				//Connection conn = null;
				DBWorkerThread thread = new DBWorkerThread(this,this.responses,this.semLocks,conn,i);
				thread.start();
				this.DBConnectionPool.add(thread);
			}
		} catch(ClassNotFoundException e){
			e.printStackTrace();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	
	public synchronized void add(Object o ) {
		queue.addLast(o);
		notify();//Change to NotifyAll() to check!		
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
		for(Iterator i = this.DBConnectionPool.iterator(); i.hasNext();) {
			DBWorkerThread rt = (DBWorkerThread)i.next();
			rt.killThread();
		}
		notifyAll();//Wake up all threads and let them die!
	}
}
