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
		//this.camera = new Axis211A("argus-4", 8080);
		//this.motionDetector = new MotionDetector("argus-4", 8080);

		camera.connect();
	}

	public void run() {
		byte[] buffer;
		int readBytes;
		byte mode;
		Image image;

		//dbg
		long then = System.currentTimeMillis();
		long now;
		long mean = 0;
		int n = 10;

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
			now = System.currentTimeMillis();
			mean = mean * (n-1) / n + 1000 / (now - then) / n;
			    System.out.println("Capture rate: " + mean + "fps");
			then = now;
		}
	}

}
