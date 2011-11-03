import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;


public class ServerProtocol {
	private static final int MSG_LEN = 1;
	public static final byte VIDEO_MODE = 1;
	public static final byte IDLE_MODE = 0;
	public static final byte DISCONNECT = 2;

	private Socket s;
	
	public ServerProtocol(Socket s) {
		this.s = s;
	}
	
	public void sendImage(Image img) {
		try {
			s.getOutputStream().write(img.toBytes());
		} catch (IOException e) {
			System.err.println("Could not send picture");
		}
	}
	
	public byte awaitCommand() throws IOException {
		byte[] buff = new byte[MSG_LEN];
		InputStream in = null;
		in = s.getInputStream();
		int readBytes = 0;
		try {
			readBytes = in.read(buff, 0, MSG_LEN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buff[0];
	}
}
