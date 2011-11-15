package network;

import se.lth.cs.fakecamera.Axis211A;
import se.lth.cs.fakecamera.MotionDetector;
//import se.lth.cs.cameraproxy.Axis211A;
//import se.lth.cs.cameraproxy.MotionDetector;
//import se.lth.cs.camera.Axis211A;
//import se.lth.cs.camera.Axis211A;

import data.ImageMonitor;

public class ImageCapture extends Thread {
	private ImageMonitor monitor;
	private Axis211A camera;
	private MotionDetector motionDetector;

	public ImageCapture(ImageMonitor monitor) {
		this.monitor = monitor;
		this.camera = new Axis211A(); // Real and fakecamera.
		//camera = new Axis211A("argus-4", 8080); // Proxycamera.
		this.motionDetector = new MotionDetector();
		//this.motionDetector = new MotionDetector("argus-4", 8080); // Proxycamera
		camera.connect();
	}

	public void run() {
		byte[] buffer;
		int readBytes;
		byte mode;
		Image image;
		while (!interrupted()) {
			buffer = new byte[Axis211A.IMAGE_BUFFER_SIZE];
			readBytes = 0;
			readBytes = camera.getJPEG(buffer, 0);
			monitor.setVideo(motionDetector.detect());
			mode = (monitor.isVideo()) ? ServerProtocol.VIDEO_MODE : ServerProtocol.IDLE_MODE;
			image = new Image(buffer, readBytes, mode);
			monitor.putImage(image);
		}
	}

}
