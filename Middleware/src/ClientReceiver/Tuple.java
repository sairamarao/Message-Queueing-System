package ClientReceiver;

import java.net.Socket;

public class Tuple {
	public Socket s;
	public String rs;
	public String timeStamp;
	public Tuple(Socket s,String tm, String rs) {
		this.s = s;
		this.rs = rs;
		this.timeStamp = tm;
	}
}
