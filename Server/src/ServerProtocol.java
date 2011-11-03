import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;


public class ServerProtocol {
	private static final int MSG_LEN = 10;
	private Socket s;
	
	public ServerProtocol(Socket s) {
		this.s = s;
	}
	
	public void sendImage(Image img) {
		try {
			s.getOutputStream().write(img.toBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int awaitCommand() {
		byte[] buff = new byte[MSG_LEN];
		InputStream in = null;
		try {
			in = s.getInputStream();
		} catch (IOException e) {
			// socket closed
			e.printStackTrace();
		}
		int readBytes = 0;
		try {
			readBytes = in.read(buff, 0, MSG_LEN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (readBytes < MSG_LEN) { //might not be needed
			//fail
		}
		return buff[0];
	}
}
