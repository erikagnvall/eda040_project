import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CameraServer implements Runnable {
	private ImageMonitor monitor;
	private ServerSocket serverSocket;
	private ServerProtocol protocol;
	private In in;
	private Out out;
	private ImageCapture capture;

	public CameraServer() {
		try {
			this.serverSocket = new ServerSocket(8080);
		} catch (IOException e) {
			System.err.println("FATAL: Could not bind to port.");
			System.exit(1);
		}
		this.protocol = new ServerProtocol();
		this.monitor = new ImageMonitor();
		this.in = new In(protocol, monitor);
		this.out = new Out(protocol, monitor);
		this.capture = new ImageCapture(monitor);
	}

	public void run() {
		capture.start();
		Socket socket = null;
		in.start();
		out.start();
		while (true) {
			System.out.println("Waiting for connection");
			try {
				socket = serverSocket.accept();
				System.out.println("Got new connection");
				protocol.setConnection(socket);
				monitor.setConnection();
			} catch (IOException e) {
				System.err.println("Could not accept connection");
			}
			System.out.println("Waiting for disconnect");
			try{
				monitor.awaitDisconnect();
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
			System.out.println("Disconnecting");
			//try {
			socket.close();
			//} catch (IOException e) {
			//System.err.println("cold not close connection");
			//}
		}
	}

	public static void main(String[] args) {
		new CameraServer().run();
	}
}
