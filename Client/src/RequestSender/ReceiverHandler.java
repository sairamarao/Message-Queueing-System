package RequestSender;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Date;

public class ReceiverHandler {

	private OutputStreamWriter osw;
	private BufferedReader in;
	private BufferedOutputStream buf;
	public String host;
	private int ReceiverId;//Make a ClientInterface
	private int ClientId;
	private int reqSent = 0;
	private int resRec = 0;
	private int position = 0;
	private PrintWriter twriter;
	private PrintWriter rwriter;
	private int throughputThread = 1;
	
	public void init(){
		try{
			this.twriter = new PrintWriter(
					new BufferedWriter(
							new FileWriter("Throughput/Throughput"+ClientId+".txt",true)));
			this.rwriter = new PrintWriter(
					new BufferedWriter(
						new FileWriter("ResponseTime/ResponseTime"+ClientId+".txt",true)));
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
    public String getWord(String s) {
		String temp = "";
		int i = position;
		for(;i < s.length() && s.charAt(i) != ' ';i++) {
			temp += s.charAt(i);
		}
		while(i < s.length() && s.charAt(i) == ' '){i++;}
		position = i;
		return temp;
	}
    
	public int register (String ip,int id, int sr) {
		String message = "register " +ip+" "+String.valueOf(id)+" "+String.valueOf(sr)+(char)13;
		String response = "";
		long t1=0,t2=0,respTim=0;
		try {
			long w0 = System.currentTimeMillis();
			osw.write(message);
			osw.flush();
			long w1 = System.currentTimeMillis();
			//System.out.println("WaitingTime+SendingTime: "+(w1-w0));
			response = in.readLine();
			long t3 = System.currentTimeMillis();
			//System.out.println("Receiver, Register Response: "+response);
			/*t2 = Long.valueOf(getWord(response)).longValue();
            t1 = Long.valueOf(getWord(response)).longValue();
            long NetworkDelay = ((t3-t0)-(t2-t1))/2;
            long TimeDiff = ((t1-t0) + (t2-t3))/2;
            System.out.println("Network delay: "+NetworkDelay+", TimeDiff: "+TimeDiff);
            */
			long serviceTime =  Long.valueOf(getWord(response)).longValue();
			long waitingTime =  Long.valueOf(getWord(response)).longValue();
			//System.out.println("serviceTime: "+serviceTime+" waitingTime: "+waitingTime+
			//		"Waiting Time(Real): "+(w1-w0)+" ServiceTime: " +(t3-w1));
			response = response.substring(position);
			respTim = ((w1-w0)+serviceTime);
			position = 0;
		} catch(IOException e){
			System.out.println("Error: Unable to register client: "+ClientId);
		}
		if(response != null && !response.equals("null")) {
			this.resRec++;
			this.rwriter.println(respTim);
			return Integer.valueOf(response);
		}
		else
			return -1;
	}
	
	public boolean getQueues() {
		String message = Long.toString(System.currentTimeMillis())+" "+"getQueues "+ReceiverId+(char)13;
		String response = null;
		try{
			osw.write(message);
			osw.flush();
			response = in.readLine();
			long timeStamp = Long.valueOf(getWord(response)).longValue();
			response = response.substring(position);
			position = 0;
		}catch(IOException e){
			System.out.println("Error: Couldn't get Queues.");
		}
		if(!response.equals("null"))
			this.resRec++;
		return response.equals("yes");
	}
	public void receiveMessage() {
		String message = "receiveMessage "+ReceiverId+" 0"+(char)13;
		try {
			long w0 = System.currentTimeMillis();
			osw.write(message);
			osw.flush();
			long w1 = System.currentTimeMillis();
			message = in.readLine();
			long t3 = System.currentTimeMillis();
            //System.out.println("Response: "+message);
            /*long t2 = Long.valueOf(getWord(message)).longValue();
            long t1 = Long.valueOf(getWord(message)).longValue();
            long NetworkDelay = ((t3-t0)-(t2-t1))/2;
            long TimeDiff = ((t1-t0) + (t2-t3))/2;
            System.out.println("Network delay: "+NetworkDelay+", TimeDiff: "+TimeDiff);
            */
            long serviceTime =  Long.valueOf(getWord(message)).longValue();
			long waitingTime =  Long.valueOf(getWord(message)).longValue();
			//System.out.println("serviceTime: "+serviceTime+" waitingTime: "+waitingTime+
			//		"Waiting Time(Real): "+(w1-w0)+" ServiceTime: " +(t3-w1));
			message = message.substring(position);
			position = 0;
			if(!message.equals("null") && !message.equals("Error"))
				{
				this.resRec++;
				this.rwriter.println((w1-w0)+serviceTime);
				System.out.println("Pop: "+ClientId+" ResponseNumber: "+this.resRec+" received successfully "+(serviceTime+w1-w0));
				}
			else
				System.out.println("Pop: "+ClientId+" couldn't receive "+(serviceTime+w1-w0));
		} catch(IOException e) {
			System.out.println("Error: Couldn't receive message for "+ReceiverId);
		}
	}
	
	public void deregister() {
		String message = "deregister "+ReceiverId+(char)13;
		String response = "";
		try {
			long w0 = System.currentTimeMillis();
            osw.write(message);
			osw.flush();
			long w1 = System.currentTimeMillis();
			response = in.readLine();
			long t3 = System.currentTimeMillis();
            /*long t2 = Long.valueOf(getWord(response)).longValue();
            long t1 = Long.valueOf(getWord(response)).longValue();
            long NetworkDelay = ((t3-t0)-(t2-t1))/2;
            long TimeDiff = ((t1-t0) + (t2-t3))/2;
            System.out.println("Network delay: "+NetworkDelay+", TimeDiff: "+TimeDiff);
            */
			long serviceTime =  Long.valueOf(getWord(response)).longValue();
			long waitingTime =  Long.valueOf(getWord(response)).longValue();
			//System.out.println("serviceTime: "+serviceTime+" waitingTime: "+waitingTime+
			//		"Waiting Time(Real): "+(w1-w0)+" ServiceTime: " +(t3-w1));
			response = response.substring(position);
			position = 0;
			if(!response.equals("null")){
				this.rwriter.println((serviceTime+(w1-w0)));
				this.resRec++;
			}
			System.out.println("Deregistering Receiver "+ReceiverId+" "+response+" "+(serviceTime+w1-w0));
		} catch(IOException e){
			System.out.println("Error: Unable to deregister client: "+ReceiverId);
		}
	}
	
	public void receiver(int id,String MWhost){
		try {
			
			this.ClientId = id+1;
			init();
			host = MWhost;
			int port = 5434;
			InetAddress address = InetAddress.getByName(host);
			Socket conn = new Socket(address,port);
			String ip = address.toString();
			
			this.buf = new BufferedOutputStream(conn.getOutputStream());
			this.in = new BufferedReader(new InputStreamReader(conn.getInputStream()) ); 
			this.osw = new OutputStreamWriter(buf, "US-ASCII");
			
			
			this.reqSent++;
			this.ReceiverId = register(ip,id,1);
			Thread throughput = new Thread(new Runnable() {
				public void run() {
						while(ReceiverHandler.this.throughputThread == 1){
							try{
								Thread.sleep(1000);
								System.out.println("Debugging.."+ReceiverHandler.this.ClientId);
							}catch(InterruptedException ie){}
							ReceiverHandler.this.twriter.println(ReceiverHandler.this.reqSent+" "+ReceiverHandler.this.resRec);
						}
				}
			});
			throughput.start();
			if (ReceiverId != -1) {
				System.out.println("ReceiverPopHandler registeres as: ["+ClientId+"] : "+ReceiverId);
			}
			else
				System.out.println("Client already registered : Pop");
			int counter = 0;
			long startTime = System.currentTimeMillis();
			while((System.currentTimeMillis()-startTime) < 60000) {	
				this.reqSent++;
				receiveMessage();
				counter++;
				//System.out.println("Receiver: "+ReceiverId+" Request Number: "+counter
				//		+" No.this.reqSent: "+this.reqSent+" No.this.resRec: "+this.resRec);
			}
			System.out.println("Receiver: ["+ClientId+"] Came out of receiving loop.");
			Thread.sleep(4000);
			this.reqSent++;
			deregister();
			System.out.println("ClientId "+ClientId+" called deregister");
			conn.close();
			System.out.println("ClientId "+ClientId+" called connclose");
			throughput.interrupt();
			System.out.println("ClientId "+ClientId+" called interrupt");
			this.throughputThread = 0;
			System.out.println("Closed socket for ["+ClientId+"]");
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.twriter.close();
		this.rwriter.close();
		System.out.println("*******************************************");
	}
}
