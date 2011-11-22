package se.lth.student.eda040.a1;

import se.lth.student.eda040.a1.network.Image;
import se.lth.student.eda040.a1.AwesomeVideoView;
import android.util.Log;
import android.widget.TextView;

public class ImageTransferer implements Runnable {
	private AwesomeVideoView videoView;
	private Image image;
	private TextView infoText;

	public ImageTransferer(AwesomeVideoView videoView, Image image, TextView infoText) {
		this.videoView = videoView;
		this.image = image;
		this.infoText = infoText;
	}

	public void run() {
		Log.d("ImageTransferer", "Now calling Handler.drawImage()");
		if (image.isVideoMode()) {
			infoText.setText("Video mode");
		} else {
			infoText.setText(" ");
		}
		videoView.drawImage(image);
	}
}
