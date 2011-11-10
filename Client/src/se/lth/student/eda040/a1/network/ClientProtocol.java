package se.lth.student.eda040.a1.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientProtocol {
	public static byte VIDEO_MODE = 'v';

	private static int REMOTE_PORT = 8080;

	private byte cameraID;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;

	public ClientProtocol(byte cameraID, String host) throws CouldNotConnectException {
		this.cameraID = cameraID;
		try {
			this.socket = new Socket(host, REMOTE_PORT);
			this.inputStream = socket.getInputStream();
			this.outputStream = socket.getOutputStream();
		} catch (IOException ie) {
			throw new CouldNotConnectException();
		}
	}

	public Image awaitImage() throws IOException{
		InputStream inputStream = socket.getInputStream();
		byte[] buffer = new byte[5];
		int bytesRead = 0;
		int returnValue = 0;
		while (bytesRead < 5) {
			returnValue = inputStream.read(buffer, bytesRead, 5-bytesRead);
			if (returnValue == -1){
				throw new IOException("Connection lost");
			}
			bytesRead += returnValue;
		}
		int imageLen = 0;
		imageLen |= buffer[1] << 24;
		imageLen |= buffer[2] << 16;
		imageLen |= buffer[3] << 8;
		imageLen |= buffer[4];
		bytesRead = 0;
		byte[] imageData = new byte[imageLen];
		while(bytesRead < imageLen){
			returnValue = inputStream.read(imageData, bytesRead, imageLen-bytesRead);
			if (returnValue == -1){
				throw new IOException("Connection lost");
			}
			bytesRead += returnValue;
		}
		Image img = new Image(cameraID, imageData, ((int) buffer[0]) == VIDEO_MODE);
		return img;
	}

	public void sendCommand(Command cmd) throws IOException{
		outputStream.write(cmd.getCommand());
		return;
	}

	public int getCameraID() {
		return cameraID;
	}
	
	public void disconnect() throws IOException {
		socket.close();
	}

}
