package se.lth.student.eda040.a1.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientProtocol {
	private int cameraID;
	private Socket socket;
	public static byte VIDEO_MODE = 'v';

	public ClientProtocol(Socket socket, int cameraID) {
		this.cameraID = cameraID;
		this.socket = socket;
	}

	public Image awaitImage() throws IOException{
		InputStream is = socket.getInputStream();
		byte[] buffer = new byte[5];
		int bytesRead = 0;
		int returnValue = 0;
		while (bytesRead < 5) {
			returnValue = is.read(buffer, bytesRead, 5-bytesRead);
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
			returnValue = is.read(imageData, bytesRead, imageLen-bytesRead);
			if (returnValue == -1){
				throw new IOException("Connection lost");
			}
			bytesRead += returnValue;
		}
		Image img = new Image(cameraID, imageData, ((int) buffer[0]) == VIDEO_MODE);
		return img;
	}

	public void sendCommand(Command cmd) throws IOException{
		OutputStream os = socket.getOutputStream();
		os.write(cmd.getCommand());
		return;
	}

	public int getCameraID() {
		return cameraID;
	}
	
	public void disconnect() throws IOException {
		socket.close();
	}

}
