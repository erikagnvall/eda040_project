import java.io.IOException;

public class In extends Thread {

	private ServerProtocol protocol;
	private ImageMonitor monitor;
	
	public In(ServerProtocol protocol, ImageMonitor monitor) {
		this.protocol = protocol;
		this.monitor = monitor;
	}
	
	public void run() {
	    while (!interrupted()) {
			byte cmd = 0;
			try {
				// TODO not thread safe.
				monitor.connectionCheck();
			    cmd = protocol.awaitCommand();
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
			
			} catch (IOException e) {
				// TODO 
				monitor.disconnect();
			} catch (InterruptedException ie) {
				// noop.
			}
		}
		System.out.println("In done running");
	}

}
