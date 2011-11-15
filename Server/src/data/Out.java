package data;

import java.net.Socket;
import network.ServerProtocol;
import network.Image;

public class Out extends Thread {

	private ServerProtocol protocol;
	private ImageMonitor monitor;
	
	public Out(ServerProtocol protocol, ImageMonitor monitor) {
		this.protocol = protocol;
		this.monitor = monitor;
	}
	
	public void run() {
	    while (!isInterrupted()) {
			Image img = monitor.getImage();
			if (img != null) {
				protocol.sendImage(img);
				//System.out.println("out got picture");
			}
	    }
	    //System.out.println("out done running");
	}
}
