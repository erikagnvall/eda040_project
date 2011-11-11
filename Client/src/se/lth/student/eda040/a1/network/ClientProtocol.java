package se.lth.student.eda040.a1.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.Socket;

// TODO needs to be synchronized because of connectTo? if they share the socket? But that no good since is.read is blocking....
public class ClientProtocol {
	public static byte VIDEO_MODE = 'v';
	public static int CAMERA_PORT = 8080;

	private byte cameraId;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;

	public ClientProtocol(byte cameraId) {
		this.cameraId = cameraId;
	}

	public Image awaitImage() throws IOException{
		while (inputStream == null) {
			try {
				wait();
			} catch (InterruptedException ie) {
				System.err.println("Caught an interrupted exception. What to do");
			}
		}

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

		Image img = new Image(cameraId, imageData, ((int) buffer[0]) == VIDEO_MODE);
		return img;
	}

	public void sendCommand(Command cmd) throws IOException{
		outputStream.write(cmd.getCommand());
	}

	public byte getCameraId() {
		return cameraId;
	}

	public void connectTo(String host) throws IOException, UnknownHostException {
		socket = new Socket(host, CAMERA_PORT);
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
	}

	public void disconnect() throws IOException {
		socket.close();
		inputStream = null;
		outputStream = null;
	}
}
