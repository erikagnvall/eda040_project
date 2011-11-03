package se.lth.student.eda040.a1.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientProtocol {
	private int cameraId;
	private Socket socket;

	public ClientProtocol(Socket socket, int cameraId) {
		this.cameraId = cameraId;
		this.socket = socket;
	}

	public Image awaitImage() throws IOException{
		InputStream is = socket.getInputStream();
		byte[] buffer = new byte[5];
		int bytesRead = 0;
		while (bytesRead < 5) {
			is.read(buffer, bytesRead, 5-bytesRead);
		}
		int imageLen = 0;
		imageLen |= buffer[1] << 24;
		imageLen |= buffer[2] << 16;
		imageLen |= buffer[3] << 8;
		imageLen |= buffer[4];
		bytesRead = 0;
		byte[] imageData = new byte[imageLen];
		while(bytesRead < imageLen){
			is.read(imageData, bytesRead, imageLen-bytesRead);
		}
		Image img = new Image(imageData, ((int) buffer[0]) == 1); // TODO fix constant value
		return img;
	}

	public void sendCommand(Command cmd){
		return;
	}

	public int getCameraId() {
		return cameraId;
	}
	
	public void disconnect() throws IOException {
		socket.close();
	}

}
