package se.lth.student.eda040.a1.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Date;
import java.net.UnknownHostException;
import java.net.Socket;
import android.util.Log;

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

	/** Fetches an image from the remote end.
	 * Fetches the image data and extracts timestamp data from JPEG image.
	 * throws IOException if connection is lost during transfer.
	 */
	public Image awaitImage() throws IOException {
		byte[] headerBytes = new byte[5];
		int bytesRead = 0;
		int returnValue = 0;
		Log.d("ClientProtocol", "Start reading header from inputStream");
		while (bytesRead < 5) {
			returnValue = inputStream.read(headerBytes, bytesRead, (5 - bytesRead));
			if (returnValue == -1) {
				throw new IOException("End of stream.");
			}
			bytesRead += returnValue;
		}
		Log.d("ClientProtocol", "Stopt reading header from inputStream");
		int imageLen = 0;
		for (int i = 0; i < 4; ++i) {
			imageLen |= (int) ((headerBytes[1 + i] < 0 ? 256 + headerBytes[i +1] : headerBytes[i + 1]) << (8 * (3 - i)));
		}

		System.out.println(cameraId + "<>" + this);
		Log.d("ClientProtocol", "imageLen == " + imageLen);
		bytesRead = 0;
		byte[] imageBytes = new byte[imageLen];

		Log.d("ClientProtocol", "Start reading data from inputStream");
		while (bytesRead < imageLen) {
			returnValue = inputStream.read(imageBytes, bytesRead, (imageLen - bytesRead));
			if (returnValue == -1){
				throw new IOException("Connection lost");
			}
			bytesRead += returnValue;
		}
		Log.d("ClientProtocol", "Stopt reading data from inputStream");

		long timestamp = 0;
		for (int i = 0; i < 4; ++i) {
				timestamp |= (long) ((imageBytes[25 + i] < 0 ? 256 + imageBytes[25 + i] : imageBytes[25 + i]) << (8 * (3 - i)));
		}
		timestamp *= 1000;
		Log.d("ClientProtocol", "Timestamp == " + timestamp + ", or in HR == " + new Date(timestamp));
		timestamp |= (long) (imageBytes[29] < 0 ? 256 + imageBytes[29] : imageBytes[29]);
		return new Image(cameraId, imageBytes, timestamp, ((int) headerBytes[0]) == VIDEO_MODE);
	}

	public void sendCommand(Command command) throws IOException{
		Log.d("ClientProtocol", "Sending command: " + command.getCommand());
		outputStream.write(command.getCommand());
	}

	public byte getCameraId() {
		return cameraId;
	}

	public void connectTo(String host) throws IOException, UnknownHostException {
		Log.d("ClientProtocol", "Pre socket instanciation for host " + host);
		socket = new Socket(host, CAMERA_PORT);
		Log.d("ClientProtocol", "Post socket instanciation for host " + host);
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		Log.d("ClientProtocol", "Got in and output streams");
	}
	
	public void gracefullDisconnect() {
		try {
			inputStream.close();
			outputStream.flush();
			outputStream.close();
		} catch (IOException ioe) {
			Log.d("ClientProtocol", "Gracefull disconnections failed.");
		}
		Log.d("ClientProtocol", "Gracefullt disconnect camera " + cameraId);
		disconnect();
	}

	// emergency 
	public void disconnect() {
		try {
			socket.close();
		} catch (IOException ioe) {
			Log.d("ClientProtocol", "Problem closing connection.");
		}
		inputStream = null;
		outputStream = null;
		Log.d("ClientProtocol", "Hard-Disconnect camera " + cameraId);
	}
}
