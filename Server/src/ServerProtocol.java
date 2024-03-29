import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerProtocol {
	private static final int MSG_LEN = 1;
	public static final byte VIDEO_MODE = 'v';
	public static final byte IDLE_MODE = 'i';
	public static final byte DISCONNECT = 'd';

	private InputStream input;
	private OutputStream output;
	private Socket socket;

	public void setConnection(Socket socket) {
		this.socket = socket;
		try {
			this.input = socket.getInputStream();
			this.output = socket.getOutputStream();
		} catch (IOException ie) {
			System.err.println("Could not get streams from socket.");
		}
	}

	public void sendImage(Image image) throws IOException {
		socket.getOutputStream().write(image.toBytes());
		System.out.println("Sent image.");
	}

	public byte awaitCommand() throws IOException {
		byte[] buff = new byte[MSG_LEN];
		int readBytes = 0;
		while(readBytes < MSG_LEN){
			readBytes = input.read(buff, 0, MSG_LEN);
			if (readBytes == -1)
				throw new IOException("connection dropped");
		}
		return (byte) (buff[0] < 0 ? buff[0] + 256 : buff[0]);
	}
}
