package network;

//import se.lth.cs.fakecamera.Axis211A;
import se.lth.cs.cameraproxy.Axis211A;
//import se.lth.cs.camera.Axis211A;

import data.ImageMonitor;

public class ImageCapture extends Thread {
	private ImageMonitor monitor;
	private Axis211A camera;

	public ImageCapture(ImageMonitor monitor) {
		this.monitor = monitor;
		//camera = new Axis211A(); // Real and fakecamera.
		camera = new Axis211A("argus-4", 8080); // Proxycamera.
		System.out.println("try connect to camera");
		camera.connect();
		System.out.println("connected to camera");
	}

	public void run() {
		byte[] buffer;
		int readBytes;
		byte mode;
		Image image;
		while (!interrupted()) {
			//try {
				//sleep(2);
				////sleep(1000);
			//} catch (InterruptedException ie) {
				//System.err.println("Broken bed");
			//}

			buffer = new byte[Axis211A.IMAGE_BUFFER_SIZE];
			//System.out.println("Kameran används2");
			readBytes = 0;
			readBytes = camera.getJPEG(buffer, 0);
			mode = (monitor.isVideo()) ? ServerProtocol.VIDEO_MODE : ServerProtocol.IDLE_MODE;
			image = new Image(buffer, readBytes, mode);
			monitor.putImage(image);
		}
	}

}
