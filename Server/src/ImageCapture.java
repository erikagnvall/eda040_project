import se.lth.cs.fakecamera.Axis211A;

public class ImageCapture extends Thread {
	private ImageMonitor im;
	private Axis211A cam;

	public ImageCapture(ImageMonitor im) {
		this.im = im;
		cam = new Axis211A(); // fakecamera has empty
		cam.connect();
	}

	public void run() {
		byte[] buff;
		while (!interrupted()) {
			buff = new byte[Axis211A.IMAGE_BUFFER_SIZE];
			int n = 0;
			n = cam.getJPEG(buff, 0);
			byte mode = (im.isVideo()) ? ServerProtocol.VIDEO_MODE : ServerProtocol.IDLE_MODE;
			Image img = new Image(buff, n, mode);
			im.putImage(img);
		}
	}

}
