//import se.lth.cs.fakecamera.Axis211A;
//import se.lth.cs.fakecamera.MotionDetector;
//import se.lth.cs.cameraproxy.Axis211A;
//import se.lth.cs.cameraproxy.MotionDetector;
import se.lth.cs.camera.Axis211A;
import se.lth.cs.camera.MotionDetector;

public class ImageCapture extends Thread {
	private ImageMonitor monitor;
	private Axis211A camera;
	private MotionDetector motionDetector;

	public ImageCapture(ImageMonitor monitor) {
		this.monitor = monitor;

		// Real and fake.
		this.camera = new Axis211A(); 
		this.motionDetector = new MotionDetector();

		// Proxy.
		//this.camera = new Axis211A("argus-4", 8080); // Proxycamera.
		//this.motionDetector = new MotionDetector("argus-4", 8080); // Proxycamera

		camera.connect();
	}

	public void run() {
		byte[] buffer;
		int readBytes;
		byte mode;
		Image image;
		while (!interrupted()) {
			//try {
				//sleep(400);
			//} catch (InterruptedException ie) {

			//}
			buffer = new byte[Axis211A.IMAGE_BUFFER_SIZE];
			readBytes = 0;
			readBytes = camera.getJPEG(buffer, 0);

			if (!monitor.isVideo() && motionDetector.detect())
			    monitor.setVideo(true);
			mode = (monitor.isVideo()) ? ServerProtocol.VIDEO_MODE : ServerProtocol.IDLE_MODE;
			image = new Image(buffer, readBytes, mode);
			monitor.putImage(image);
		}
	}

}
