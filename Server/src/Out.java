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
				image = monitor.getImage();
			} catch (InterruptedException ie) {
				// do nothing.
				System.out.println("Out got interrupted.");
			}
			if (image != null) {
				try {
					protocol.sendImage(image);
				} catch (IOException e) {
					System.out.println("oh noes, ioexception");
					monitor.disconnect();
				}
				//System.out.println("out got picture");
			}
	    }
		System.out.println("out done running");
	}
}
