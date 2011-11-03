package data;

import java.net.Socket;
import network.ServerProtocol;

public class Out extends Thread {

	private ServerProtocol protocol;
	private ImageMonitor monitor;
	
	public Out(ServerProtocol protocol, ImageMonitor monitor) {
		this.protocol = protocol;
		this.monitor = monitor;
	}
	
	public void run() {
		while (true) {
			protocol.sendImage(monitor.getImage());
		}
	}
}
