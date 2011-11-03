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
		while (true) {
			byte[] buff = new byte[Axis211A.IMAGE_BUFFER_SIZE + Image.OFFSET];
			int n = 0;
			n = cam.getJPEG(buff, Image.OFFSET);
			Image img = new Image(buff, n);
			im.putImage(img);
		}
	}

}
