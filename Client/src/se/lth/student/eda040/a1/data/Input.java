package se.lth.student.eda040.a1.data;

import java.io.IOException;
import android.util.Log;

import se.lth.student.eda040.a1.network.*;

public class Input extends Thread {
	private ClientMonitor monitor;
	private ClientProtocol protocol;

	public Input(ClientMonitor monitor, ClientProtocol protocol) {
		this.monitor = monitor;
		this.protocol = protocol;
	}

	public void run() {
		Image image;
		byte cameraId = protocol.getCameraId();
		while (!interrupted()) {
			try {
				Log.d("Input", "Checking for connection in monitor");
				monitor.connectionCheck(cameraId);
				Log.d("Input", "In started to fetch images");
				image = protocol.awaitImage();
				monitor.putImage(image, cameraId);
			} catch (IOException e) {
				monitor.disconnectCamera(cameraId);
			}
		}
	}
}
