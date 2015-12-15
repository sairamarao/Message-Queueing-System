package DatabaseConnector;

import java.net.*;
import java.sql.*;
import java.util.Vector;

import ClientReceiver.*;

public class DBWorkerThread extends Thread{
	
	private Connection conn;
	private Responses responses;
	private Locking semLocks;
	private DBQueue dbQueue;
	private boolean isrunning;
	private boolean isprocessing = false;
	private int threadNumber;
	
	public DBWorkerThread(DBQueue dbQueue,Responses resp, Locking l,Connection conn, int threadNumber) {
		this.conn = conn;
		this.responses = resp;
		this.semLocks = l;
		this.threadNumber = threadNumber;
		this.dbQueue = dbQueue;
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
				Object o = dbQueue.getNextObject();
				if (isrunning && o!=null) {//When we kill threads this will be false(race condition may occur.)
					long dbWaitEnd = System.currentTimeMillis();
					Query query = (Query)o;
					if(query.threadNumber == -2){
						this.isrunning = false;
						this.conn.close();
						this.dbQueue.add(query);
						continue;
					}
					this.isprocessing = true;
					System.out.println( "DB: [" + threadNumber + "]: Processing request..." );
					String command = query.queryParam.get(0);
					ResultSet rs = null;
					/*System.out.println("********************");
					for(int i=0;i<query.queryParam.size();i++){
						System.out.println(query.queryParam.get(i)+" ");
					}
					System.out.println("**********************");
					//System.out.println("Command: "+command);
					//System.out.println("Query: "+query.queryParam.get(1));
					*/
					PreparedStatement stmt = conn.prepareStatement(query.queryParam.get(1));
					
					
					int a,b,c;
					try {
						switch(command){
						    case "register": 
					    		stmt.setString(1,query.queryParam.get(2));
					    		a = Integer.parseInt(query.queryParam.get(3));
							    stmt.setInt(2, a);
							    b = Integer.parseInt(query.queryParam.get(4));
							    stmt.setInt(3,b);
								rs = stmt.executeQuery();
								break;
						    case "deregister": 
					    		a = Integer.parseInt(query.queryParam.get(2));
							    stmt.setInt(1,a);
								rs = stmt.executeQuery();
								break;
						    case "queue": 
					    		rs = stmt.executeQuery();
								break;
						    case "getR": 
					    		stmt.setString(1,query.queryParam.get(2));
							    rs = stmt.executeQuery();
						    	break;
						    case "getQueues": 
					    		a = Integer.parseInt(query.queryParam.get(2));
							    stmt.setInt(1,a);
								rs = stmt.executeQuery();
								break;
						    case "sendMessageR": 
					    		a = Integer.parseInt(query.queryParam.get(2));
							    stmt.setInt(1,a);
							    b = Integer.parseInt(query.queryParam.get(3));
							    stmt.setInt(2,b);
							    c = Integer.parseInt(query.queryParam.get(4));
							    stmt.setInt(3,c);
							    stmt.setString(4,query.queryParam.get(5));
								rs = stmt.executeQuery();
								break;
						    case "sendMessage": 
					    		a = Integer.parseInt(query.queryParam.get(2));
							    stmt.setInt(1,a);
							    b = Integer.parseInt(query.queryParam.get(3));
							    stmt.setInt(2,b);
							    stmt.setString(3,query.queryParam.get(4));
								rs = stmt.executeQuery();
								break;
						    case "receiveMessage": 
					    		a = Integer.parseInt(query.queryParam.get(2));
							    stmt.setInt(1,a);
							    b = Integer.parseInt(query.queryParam.get(3));
							    stmt.setInt(2,b);
							    rs = stmt.executeQuery();
								break;
							default: throw new Exception("Unkown Command");
						}
					} catch(Exception e){
						e.printStackTrace();
					}
					long dbWaitTime = dbWaitEnd - Long.valueOf(query.timeStamp).longValue();
					long DBWorkerServiceTime = System.currentTimeMillis()-dbWaitEnd;
		    		System.out.println("DBWaitTime: "+dbWaitTime+" DB Worker Service Time: "+
		    				DBWorkerServiceTime);
		    		
					String result = null;
					//System.out.println("DBWOrker:(T5 T4 T3 T2 T1)"+query.timeStamp);
					if(rs != null && rs.next()){
						result = rs.getString(1);
						System.out.println("Served command: "+command+"successfully!"+" "+result);
					}
					else{
						result = "Error";
						System.out.println("Couldn't serve command: "+command);
					}
						responses.response[query.threadNumber] = result;
						System.out.println("Releasing lock: "+query.threadNumber);
						semLocks.semaphores[query.threadNumber].release();
						System.out.println("After Releasing lock: "+query.threadNumber);
				 }
             	 this.isprocessing = false;
                 System.out.println( "DB: [" + threadNumber + "]: Finished Processing request..." );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println( "DB: [" + threadNumber + "]: Thread shutting down..." );
	}
}
