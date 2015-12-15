package RequestSender;

import java.net.*;

public class ClientThread extends Thread{
	
	private boolean isrunning;
	private boolean isprocessing = false;
        private String host = null;
	private int threadNumber;
	private SenderHandler send;
	private ReceiverHandler rec;
	private ReceiverPopHandler recp;
	
	public ClientThread(int threadNumber, String host) {
		this.threadNumber = threadNumber;
		this.host = host;
		try {
			this.recp = (ReceiverPopHandler)(Class.forName("RequestSender.ReceiverPopHandler").newInstance());
			this.send = (SenderHandler)(Class.forName("RequestSender.SenderHandler").newInstance());
			this.rec = (ReceiverHandler)(Class.forName("RequestSender.ReceiverHandler").newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isProcessing() {
		return this.isprocessing;
	}

	public void killThread() {
		
		System.out.println( threadNumber + ": Attempting to kill thread..." );
        this.isrunning = false;
	}
	
	public void run() {
		this.isrunning = true;
		try {
			this.isprocessing = true;
			/*if (this.threadNumber%2 == 1 && this.isrunning == true){
				this.send.sender(threadNumber,host);
			}
			else if(this.isrunning == true){
				/*if(this.threadNumber%4 == 0){
				System.out.println("Peeker: "+threadNumber);
					this.rec.receiver(threadNumber,host);}
				else{
				System.out.println("Popper: "+threadNumber);
				*/
				this.send.sender(threadNumber,host);
                  		
			
				
		//	}
			//Testing only senders for calculating throughput
			//if(this.isrunning == true)
			//	this.send.sender(threadNumber);
            
			this.isprocessing = false;
         }catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println( threadNumber + "]: Thread shutting down..." );
	}
}

