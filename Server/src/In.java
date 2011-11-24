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
				monitor.connectionCheck();
				cmd = protocol.awaitCommand();
				System.out.println("IN: Recieved command " + cmd);
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
						System.err.println("IN: Unknown command recieved.");
				}

			} catch (IOException e) {
				System.out.println("IN: Got IOException: " + e.getMessage() + "\nDisconnecting.");
				monitor.disconnect();
			} catch (InterruptedException ie) {
				// noop.
			}
		}
		System.out.println("IN: Done running");
	}

}
