package DatabaseConnector;

import java.net.Socket;
import java.util.Vector;

public class Query {
	public Socket s;
	public int threadNumber;
	public String timeStamp;
	public Vector<String> queryParam;
	public Query(Socket s,int tnum, String tim, Vector<String> query) {
		this.s = s;
		this.threadNumber = tnum;
		this.timeStamp = tim;
		this.queryParam = new Vector<String>(query);
		
	}
}
