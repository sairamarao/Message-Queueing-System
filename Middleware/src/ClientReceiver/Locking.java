package ClientReceiver;

import java.util.concurrent.Semaphore;


public class Locking {
	public Semaphore[] semaphores;
	public Locking(int numSemaphores){
		this.semaphores = new Semaphore[numSemaphores];
		for(int i = 0; i < numSemaphores; i++){
			this.semaphores[i] = new Semaphore(0);
		}
	}
}