package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.ImageTransferer;
import se.lth.student.eda040.a1.AwesomeVideoView;
import se.lth.student.eda040.a1.network.Image;
import android.util.Log;
import android.os.Handler;
import android.widget.TextView;

public class ImageFetcher extends Thread {
	private ClientMonitor monitor;
	private AwesomeVideoView videoView;
	private Handler handler;
	private TextView infoText;

	public ImageFetcher(ClientMonitor monitor, AwesomeVideoView videoView, Handler handler, TextView infoText) {
		this.monitor = monitor;
		this.videoView = videoView;
		this.handler = handler;
		this.infoText = infoText;
	}

	public void run(){
		Image image;
		while(!interrupted()){
			try {
				image = monitor.awaitImage();
				handler.post(new ImageTransferer(videoView, image, infoText));
				Log.d("ImageTransferer", "^^Fetched an image and posted an ImageTransferer");
			} catch(InterruptedException ie) {
				// Do nothing
			}
			//handler.postDelayed(new ImageTransferer(videoView, image), 4000);
		}
	}
}
