package se.lth.student.eda040.a1.;

public class Input extends Thread {
	private ClientMonitor monitor;
	private ClientProcol protocol;

	public Input(CientMonitor monitor) {
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
