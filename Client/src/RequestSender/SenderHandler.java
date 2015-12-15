package RequestSender;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Properties;

public class SenderHandler {

	private OutputStreamWriter osw,o;
	private BufferedReader in;
	private BufferedOutputStream buf;
	private String host;
	private int SenderId;
	private int ClientId;
	private int reqSent = 0;
	private int resRec = 0;
	private String LongMessage = null;
	private String ShortMessage = null;
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
    
	public String getMessage(int l) {
		Properties sprop = new Properties();
		try {
			sprop.load(new FileInputStream("Client.properties"));
		} catch (Exception e) {
			System.out.println("Error: Can't read Client.properties");
		}
		if (l == 1)
			return sprop.getProperty("long");
		else
			return sprop.getProperty("short");
	}
	
	public int register (String ip,int id, int sr) {
		String message = "register " +ip+" "+String.valueOf(id)+" "+String.valueOf(sr)+(char)13;
		String response = null;
		long t1=0,t2=0,respTim=0,serviceTime=0;
		try {
			long w0 = System.currentTimeMillis();
            osw.write(message);
			osw.flush();
			long w1 = System.currentTimeMillis();
            response = in.readLine();
			long t3 = System.currentTimeMillis();
			//System.out.println("Sender, Register Response: "+response);
			/*t2 = Long.valueOf(getWord(response)).longValue();
            t1 = Long.valueOf(getWord(response)).longValue();
            System.out.println(t0+" "+t1+" "+t2+" "+t3);
            long NetworkDelay = ((t3-t0)-(t2-t1))/2;
            long TimeDiff = ((t1-t0) + (t2-t3))/2;
            System.out.println("Network delay: "+NetworkDelay+", TimeDiff: "+TimeDiff+" Res: "+respTim);
            */
			serviceTime =  Long.valueOf(getWord(response)).longValue();
			long waitingTime =  Long.valueOf(getWord(response)).longValue();
			//System.out.println("serviceTime: "+serviceTime+" waitingTime: "+waitingTime+
			//		"Waiting Time(Real): "+(w1-w0)+" ServiceTime: " +(t3-w1));
			respTim = (serviceTime+waitingTime);
            response = response.substring(position);
			position = 0;
			
		} catch(IOException e){
			System.out.println("Error: Unable to register sender:"+String.valueOf(id));
		}
		if(response != null && !response.equals("null") && !response.equals("Error")){
			this.resRec++;
			this.rwriter.println((respTim)+" "+serviceTime);
            return Integer.valueOf(response);
		}
		else{
			System.out.println("Error: Unable to register sender:"+String.valueOf(id));
			return -1;
		}
		
	}
	
	public int createQueue(){
		String message = "queue"+(char)13;
		String response = null;
		int queue_id = -1;
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
			this.rwriter.println((serviceTime+waitingTime)+" "+serviceTime);
            response = response.substring(position);
			position = 0;
		} catch(IOException e){
			System.out.println("Error: Couldn't create Queue for Sender: "+SenderId);
		}
		if(response != null && !response.equals("null") && !response.equals("Error")) {
			queue_id = Integer.valueOf(response);
			this.resRec++;
			System.out.println("Queue created with id: "+queue_id);
		}
		else
			System.out.println("Couldn't create queue");
		return queue_id;
	}
	
	public long sendMessage(int queue_id) {
		String message = "";
		String result = null;
		long t3 = 0;
		try {	
			message = "sendMessage "+queue_id+" "+SenderId+" "+ShortMessage+(char)13;
			long w0 = System.currentTimeMillis();
            osw.write(message);
            osw.flush();
            long w1 = System.currentTimeMillis();
            result = in.readLine();
            t3 = System.currentTimeMillis();
            //System.out.println("Response: "+result);
            /*long t2 = Long.valueOf(getWord(result)).longValue();
            long t1 = Long.valueOf(getWord(result)).longValue();
            long NetworkDelay = ((t3-t0)-(t2-t1))/2;
            long TimeDiff = ((t1-t0) + (t2-t3))/2;
            System.out.println("Network delay: "+NetworkDelay+", TimeDiff: "+TimeDiff);
            */
            long serviceTime =  Long.valueOf(getWord(result)).longValue();
			long waitingTime =  Long.valueOf(getWord(result)).longValue();
			//System.out.println("serviceTime: "+serviceTime+" waitingTime: "+waitingTime+
			//		"Waiting Time(Real): "+(w1-w0)+" ServiceTime: " +(t3-w1));
			result = result.substring(position);
            position = 0;
            if(result.equals("Success"))
			{
            	this.rwriter.println((serviceTime+waitingTime)+" "+serviceTime);
                this.resRec++;
		System.out.println("Pop Success. Resp. Time: "+(t3-w0)+"MW Resp. Time: "+(serviceTime+waitingTime)+
				" service Time: "+serviceTime);
			}
            else
            	System.out.println(ClientId+" couldn't send!");
		} catch(IOException e) {
			System.out.println("Error: Couldn't send message from sender: "+ClientId);
		}
		return t3;
	}
	
	public long receiveMessage() {
		String message = "receiveMessage "+SenderId+" 1"+(char)13;
		long t3 = 0;
		try {
			long w0 = System.currentTimeMillis();
			osw.write(message);
			osw.flush();
			long w1 = System.currentTimeMillis();
			message = in.readLine();
			t3 = System.currentTimeMillis();
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
				this.rwriter.println((waitingTime+serviceTime)+" "+serviceTime);
				System.out.println("Pop Success. Resp. Time: "+(t3-w0)+"MW Resp. Time: "+(serviceTime+waitingTime)+
				" service Time: "+serviceTime);
				}
			else
				System.out.println("Pop: "+ClientId+" couldn't receive "+(serviceTime+w1-w0));
		} catch(IOException e) {
			System.out.println("Error: Couldn't receive message for "+ClientId);
		}
		return t3;
	}
	
	public int getR(String ip) {
		String message = Long.toString(System.currentTimeMillis())+" "+"getR "+ip+(char)13;
		String response = null;
		int rid = -1;
		try {
			osw.write(message);
			osw.flush();
			response = in.readLine();
			long timeStamp = Long.valueOf(getWord(response)).longValue();
			response = response.substring(position);
			position = 0;
			
		} catch(IOException e){
			System.out.println("Error: No Receiver found");
		}
		if(response != null && !response.equals("null") ) {
			rid = Integer.valueOf(response);
			this.resRec++;
			System.out.println("Got a receiver: "+ rid);
		}
		return rid;
			
	}
	
	public void sendMessageR(int queue_id,int rid) {
		String text = "Hello Message: "+SenderId+" -> "+rid;
		
		String message = Long.toString(System.currentTimeMillis())+" "+"sendMessageR "+queue_id+" "+SenderId+" "+rid+" "
						+text+(char)13;
		String response = null;
		try {
			osw.write(message);
			osw.flush();
			response = in.readLine();
			long timeStamp = Long.valueOf(getWord(response)).longValue();
			response = response.substring(position);
			position = 0;
			
			if(!response.equals("null"))
				this.resRec++;
			System.out.println("Sent message "+SenderId+" -> "+rid+" "+response);
		} catch(IOException e) {
			System.out.println("Couldn't send message "+SenderId+" -> "+rid);
		}
	}
	public void deregister() {
		String message = "deregister "+String.valueOf(SenderId)+(char)13;
		String response = null;
		try {
			long w0 = System.currentTimeMillis();
            osw.write(message);
			osw.flush();
			long w1 = System.currentTimeMillis();
            response = in.readLine();
			long t3 = System.currentTimeMillis();
            //System.out.println("Response: "+response);
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
			
			if (!response.equals("null") && !response.equals("Error"))
				{
				this.rwriter.println((waitingTime+serviceTime)+" "+serviceTime);
				System.out.println("Deregisterd Sender: "+ClientId+" "+((w1-w0)+serviceTime));
	            this.resRec++;
				}
			else
			{
				System.out.println("Error: Unable to deregister client: "+ClientId);
			}
			System.out.println("Deregistered Sender "+ClientId+" "+response);
		} catch(IOException e){
			System.out.println("Error: Unable to deregister client: "+ClientId);
		}
	}
	
	public void sender(int id,String MWhost){
		try {
			this.ClientId = id+1;
			init();
			host = MWhost;
			int port = 5434;
			int queue_id;
			InetAddress address = InetAddress.getByName(host);
			String ip = address.toString();
			Socket conn = new Socket(address,port);
			this.buf = new BufferedOutputStream(conn.getOutputStream());
			this.in = new BufferedReader(new InputStreamReader(conn.getInputStream()) ); 
			this.osw = new OutputStreamWriter(buf, "US-ASCII");	
			
			
			Thread throughput = new Thread(new Runnable() {
				public void run() {
						while(SenderHandler.this.throughputThread == 1){
							SenderHandler.this.twriter.println(SenderHandler.this.reqSent+" "+SenderHandler.this.resRec);
							try{
								Thread.sleep(1000);
							//	System.out.println("Debugging Sender.."+SenderHandler.this.ClientId);
							}catch(InterruptedException ie){}
							
						}
				}
			});
			throughput.start();
			
			this.reqSent++;
			this.SenderId = register(ip,id,0);
			if(LongMessage == null){
				LongMessage = getMessage(1);
			}
			if(ShortMessage == null) {
				ShortMessage = getMessage(0);
			}
			if (SenderId != -1)
				System.out.println("SenderId for Client: ["+ClientId+"] : "+SenderId);
			else
				System.out.println("Error: Sender "+SenderId+" already registered");
			this.reqSent++;
			queue_id = createQueue();
			
			
			long startTime = System.currentTimeMillis();
			long sendLastUpdate = 0;
			long receiveLastUpdate = 0;
			int counter = 0;
			/*PrintWriter pwrite = new PrintWriter(
									new BufferedWriter(
											new FileWriter("DummyResp"+ClientId+".txt",true)));
			*/
			while((System.currentTimeMillis()-startTime) < 120000) {
			//while(counter < 2){
				this.reqSent++;
				//pwrite.println("RespTime: "+(System.currentTimeMillis()-sendLastUpdate));
				//System.out.println("Sending 'send' request after: "+(System.currentTimeMillis()-sendLastUpdate));
				sendLastUpdate = sendMessage(queue_id);
				//Thread.sleep(50);
				//System.out.println("Sending 'receive' request after: "+(System.currentTimeMillis()-receiveLastUpdate));
				this.reqSent++;
				receiveLastUpdate = receiveMessage();
				//counter++;
				//System.out.println("Sender: "+SenderId+" Request Number: "+counter
				//		+" reqSent: "+reqSent+" resRec: "+resRec);
			}
			
			//pwrite.close();
			System.out.println("Client: ["+ClientId+" Came out of Sending loop.");			
			Thread.sleep(4000);
			this.reqSent++;
			deregister();
			conn.close();
			throughput.interrupt();
			this.throughputThread = 0;
			System.out.println("Closed socket for Client: ["+ClientId+"]");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.twriter.close();
		this.rwriter.close();
		System.out.println("*******************************************");
		
	}
}
