package ClientReceiver;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Vector;

import DatabaseConnector.*;


public class QueryHandler implements RequestHandler{
    
	private int cursor = 0;
	private String timestamp = null;
	
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
	
	
	public String handleRequest (DBQueue dbQueue, int threadNumber, Tuple t) {
    	String rs = "null";
    	long queryReadEnd = 0;
    	try {
    		BufferedReader in = new BufferedReader(new InputStreamReader(t.s.getInputStream()));
    		String query = in.readLine();
    		queryReadEnd = System.currentTimeMillis();
    		if (query == null) {
    			in.close();
    			System.out.println("Client Disconnected!!");
    			return (queryReadEnd+" Client Disconnected");
    		} else if(query.equals("STOP")){
    			in.close();
    		    Vector<String> param2 = new Vector<String>();
    		    int tnum = -2;
    			Query client_query2 = new Query(t.s,tnum,"",param2);
    			dbQueue.add(client_query2);
    			System.out.println("All Clients Stopped!!");
    			return (queryReadEnd+" STOP");
    		}
    		String command = getWord(query);
		    Vector<String> param = new Vector<String>();
		    Query client_query = new Query(t.s,threadNumber,"",param);
    		String cmd = "",temp="";
    		switch(command){
		    	case "register": 
		    		cmd = "SELECT registerClient(?,?,?)";
		    		client_query.queryParam.addElement("register");
		    		client_query.queryParam.addElement(cmd);
		    		temp = getWord(query);
		    		//System.out.println("temp: "+temp+"cursor: "+this.cursor);
		    		client_query.queryParam.addElement(temp);
		    		temp = getWord(query);
		    		//System.out.println("temp: "+temp+"cursor: "+this.cursor);
		    		client_query.queryParam.addElement(temp);
		    		temp = getWord(query);
		    		//System.out.println("temp: "+temp+"cursor: "+this.cursor);
		    		client_query.queryParam.addElement(temp);
		    		break;
			    case "deregister": 
			    	cmd = "SELECT deregisterClient(?)";
			    	client_query.queryParam.addElement("deregister");
			    	client_query.queryParam.addElement(cmd);
		    		temp = getWord(query);
		    		client_query.queryParam.addElement(temp);
		    		break;
			    case "queue": 
			    	cmd = "SELECT createQueue()";
			    	client_query.queryParam.addElement("queue");
			    	client_query.queryParam.addElement(cmd);
		    		break;
			    case "getR": 
			    	cmd = "SELECT getReceiver(?)";
			    	client_query.queryParam.addElement("getR");
			    	client_query.queryParam.addElement(cmd);
		    		temp = getWord(query);
		    		client_query.queryParam.addElement(temp);
		    		break;
			    case "getQueues": 
			    	cmd = "SELECT getQueues(?)";
			    	client_query.queryParam.addElement("getQueues");
			    	client_query.queryParam.addElement(cmd);
		    		temp = getWord(query);
		    		client_query.queryParam.addElement(temp);
		    		break;
			    case "sendMessageR": 
			    	cmd = "SELECT sendMessageR(?,?,?,?)";
			    	client_query.queryParam.addElement("sendMessageR");
			    	client_query.queryParam.addElement(cmd);
		    		temp = getWord(query);
		    		client_query.queryParam.addElement(temp);
		    		temp = getWord(query);
		    		client_query.queryParam.addElement(temp);
		    		temp = getWord(query);
		    		client_query.queryParam.addElement(temp);
		    		temp = query.substring(this.cursor);
		    		client_query.queryParam.addElement(temp);
		    		break;
			    case "sendMessage": 
			    	cmd = "SELECT sendMessage(?,?,?)";
			    	client_query.queryParam.addElement("sendMessage");
			    	client_query.queryParam.addElement(cmd);
		    		temp = getWord(query);
		    		client_query.queryParam.addElement(temp);
		    		temp = getWord(query);
		    		client_query.queryParam.addElement(temp);
		    		temp = query.substring(this.cursor);
		    		client_query.queryParam.addElement(temp);
		    		break;
			    case "receiveMessage": 
			    	cmd = "SELECT receiveMessage(?,?)";
			    	client_query.queryParam.addElement("receiveMessage");
			    	client_query.queryParam.addElement(cmd);
		    		temp = getWord(query);
		    		client_query.queryParam.addElement(temp);
		    		temp = getWord(query);
		    		client_query.queryParam.addElement(temp);
		    		break;
			    default: throw new Exception("Unkown Command");
    		}
    		long queryReadTime = queryReadEnd-Long.valueOf(t.timeStamp).longValue();
    		client_query.timeStamp = System.currentTimeMillis()+"";
    		System.out.print("Query: ");
    		for(int i=0;i<client_query.queryParam.size();i++){
    			System.out.print(client_query.queryParam.get(0)+" ");
    		}
    		System.out.println();
    		dbQueue.add(client_query);
    		long requestQThinkTime = System.currentTimeMillis() - queryReadEnd;
    		System.out.println("Query Read Time: "+queryReadTime+"ReqQThinkTime: "+
    				requestQThinkTime);
    		this.cursor = 0;
    	} catch (IOException e) {
    		System.out.println("Client disconnected.");
    		return (queryReadEnd+" Disconnected");
    	} catch(SQLException se) {
    		System.out.println("Query failed");
    		return (queryReadEnd+" null");
    	} catch(ClassNotFoundException cne) {
    		System.out.println("RequestHandler not found");
    		return (queryReadEnd+" null");
    	} catch(Exception e){
    		e.printStackTrace();
    	}
		return (queryReadEnd+" Success");
    	
    }
}
