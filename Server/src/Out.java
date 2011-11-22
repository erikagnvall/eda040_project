import java.net.Socket;
import java.io.IOException;

public class Out extends Thread {

	private ServerProtocol protocol;
	private ImageMonitor monitor;
	
	public Out(ServerProtocol protocol, ImageMonitor monitor) {
		this.protocol = protocol;
		this.monitor = monitor;
	}
	
	public void run() {
		Image image = null;
	    while (!interrupted()) {
			try {
				//monitor.connectionCheck();
				image = monitor.getImage();
				protocol.sendImage(image);
			} catch (IOException e) {
				System.out.println("OUT: Lost connection.\nDisconnecting");
				monitor.disconnect();
			} catch (InterruptedException ie) {
				System.out.println("OUT: Got interrupted.");
			}
	    }
	    System.out.println("OUT: Done!");
	}
}
