
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
			//try {
				//sleep(400);
			//} catch (InterruptedException ie) {

			//}
			buffer = new byte[Axis211A.IMAGE_BUFFER_SIZE];
			readBytes = 0;
			readBytes = camera.getJPEG(buffer, 0);
			/* this is the good way of doing things
			monitor.setVideo(motionDetector.detect());
			mode = (monitor.isVideo()) ? ServerProtocol.VIDEO_MODE : ServerProtocol.IDLE_MODE;
			*/
			/* this is the bad way */
			mode = (motionDetector.detect()) ? ServerProtocol.VIDEO_MODE : ServerProtocol.IDLE_MODE;
			/* end of bad way */
			image = new Image(buffer, readBytes, mode);
			monitor.putImage(image);
		}
	}

}
