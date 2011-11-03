import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionSetup {
	private ImageMonitor monitor;
	private ServerSocket ss;

	public ConnectionSetup() {
		monitor = new ImageMonitor();
		try {
			ss = new ServerSocket(1084);
		} catch (IOException e) {
			System.err.println("Could not bind to port.");
		}
	}

	public void run() {
		Socket socket;
		while (true) {
			try {
				socket = ss.accept();
			} catch (IOException e) {
				System.err.println("Could not accept connection");
			}
			monitor.awaitDisconnect();
		}	
	}

	public static void main(String[] args) {
		new ConnectionSetup().run();
	}
}
