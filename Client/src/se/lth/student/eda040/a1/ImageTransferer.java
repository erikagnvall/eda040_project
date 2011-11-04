package se.lth.student.eda040.a1;

import se.lth.student.eda040.a1.network.Image;

public class ImageTransferer implements Runnable {
	private AwesomeVideoView videoView;
	private Image image;

	public ImageTransferer(AwesomeVideoView videoView, Image image) {
		this.videoView = videoView;
		this.image = image;
	}

	public void run() {
		videoView.drawImage(image);
	}
}
