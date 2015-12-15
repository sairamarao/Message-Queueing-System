package ClientReceiver;

import java.io.*;

public class Compute extends Thread{
	
	private PrintWriter pwrite;
	private RequestQueue reqQueue;
	private int processing = 0;
	private int counter = 0;
	
	public Compute(RequestQueue queue){
		this.reqQueue = queue;
		this.processing = 1;
		try{
		pwrite = new PrintWriter(new BufferedWriter(new FileWriter("arrivalRate.txt")));
		} catch(IOException ie){
			ie.printStackTrace();
		}
	}
	public void finish(){
		this.processing = 0;
	}
	public void run(){
		while(this.processing == 1){
			try{
			Thread.sleep(1000);
			}catch(InterruptedException ie){}
			pwrite.println((this.reqQueue.arrivalCount-counter));
			pwrite.flush();
			this.counter = this.reqQueue.arrivalCount;
			
		}
		pwrite.close();
	}
}
