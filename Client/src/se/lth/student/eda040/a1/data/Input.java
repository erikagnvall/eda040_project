package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.network.*;

public class Input extends Thread {
	private ClientMonitor monitor;
	private ClientProtocol protocol;

	public Input(ClientMonitor monitor) {
		this.monitor = monitor;
	}

	public void run() {
		Image image;
		while (!interrupted()) {
			image = protocol.awaitImage();
			monitor.putImage(image, protocol.getCameraId());
		}
	}
}
