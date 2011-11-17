import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CameraServer {
	private ImageMonitor monitor;
	private ServerSocket serverSocket;
	private ImageCapture capture;

	public CameraServer() {
		monitor = new ImageMonitor();
		try {
			serverSocket = new ServerSocket(8080);
		} catch (IOException e) {
			System.err.println("Could not bind to port.");
		}
		this.capture = new ImageCapture(monitor);
	}

	public void run() {
		capture.start();
		Socket socket = null;
		ServerProtocol protocol;
		In in;
		Out out;
		while (true) {
			System.out.println("Waiting for connection");
			try {
				socket = serverSocket.accept();
				monitor.connect();
				System.out.println("got new connection");
			} catch (IOException e) {
				System.err.println("Could not accept connection");
			}
			protocol = new ServerProtocol(socket);
			in = new In(protocol, monitor);
			out = new Out(protocol, monitor);
			in.start();
			out.start();
			System.out.println("Waiting for disconnect");
			monitor.awaitDisconnect();
			System.out.println("diconnected");
			in.interrupt();
			out.interrupt();
			try {
			    in.join();
			    out.join();
			} catch (InterruptedException e) {
			    System.err.println("cold not join threads");
			}
			System.out.println("killed com threads");
			//try {
			    socket.close();
			//} catch (IOException e) {
				//System.err.println("cold not close connection");
			//}			    
			System.out.println("closed connection");
		}
	}

	public static void main(String[] args) {
		new CameraServer().run();
	}
}
