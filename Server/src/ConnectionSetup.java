import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import data.ImageMonitor;
import data.In;
import data.Out;
import network.ServerProtocol;

public class ConnectionSetup {
	private ImageMonitor monitor;
	private ServerSocket serverSocket;

	public ConnectionSetup() {
		monitor = new ImageMonitor();
		try {
			serverSocket = new ServerSocket(8080);
		} catch (IOException e) {
			System.err.println("Could not bind to port.");
		}
	}

	public void run() {
		Socket socket = null;
		System.out.println("Waiting for connection");
		while (true) {
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				System.err.println("Could not accept connection");
			}
			ServerProtocol protocol = new ServerProtocol(socket);
			In in = new In(protocol, monitor);
			Out out = new Out(protocol, monitor);
			in.start();
			out.start();
			System.out.println("Waiting for disconnect");
			monitor.awaitDisconnect();
		}
	}

	public static void main(String[] args) {
		new ConnectionSetup().run();
	}
}
