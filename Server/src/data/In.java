package data;

import java.io.IOException;
import network.ServerProtocol;

public class In extends Thread {

	private ServerProtocol protocol;
	private ImageMonitor monitor;
	
	public In(ServerProtocol protocol, ImageMonitor monitor) {
		this.protocol = protocol;
		this.monitor = monitor;
	}
	
	public void run() {
	    while (!isInterrupted()) {
			byte cmd = 0;
			try {
				cmd = protocol.awaitCommand();
			} catch (IOException e) {
				// TODO 
				monitor.disconnect();
			}
			System.out.println("Recieved command " + cmd);
			switch (cmd) {
				case ServerProtocol.IDLE_MODE:
					monitor.setVideo(false);
					break;
				case ServerProtocol.VIDEO_MODE:
					monitor.setVideo(true);
					break;
				case ServerProtocol.DISCONNECT:
					monitor.disconnect();
					break;
				default:
					System.err.println("Unknown command recieved.");
			}
		}
		System.out.println("in died");
	}

}
