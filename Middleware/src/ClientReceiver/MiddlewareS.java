package ClientReceiver;

import java.io.FileInputStream;
import java.util.Properties;


public class MiddlewareS extends MServer{
     
	private static int port;
	private static int backlog;
	private static String reqHandlerClass;
	private static int ClientWorkers;
	
	public MiddlewareS() {
		super(port,backlog,reqHandlerClass,ClientWorkers);    	
    }
    
    public static void main(String args[]) {
    	
    	Properties sprop = new Properties();
		try {
			sprop.load(new FileInputStream("middlewareS.properties"));
		} catch (Exception e) {
			System.err.println("Can't read middlewareS.properties");
		}
		port = Integer.valueOf(sprop.getProperty("port")).intValue();
    	backlog = Integer.valueOf(sprop.getProperty("backlog")).intValue();
    	reqHandlerClass = sprop.getProperty("RequestHandlerClass");
    	ClientWorkers = Integer.valueOf(sprop.getProperty("ClientWorkers")).intValue();
    	
    	MiddlewareS ms = new MiddlewareS();
    	ms.startServer();
    }
}
