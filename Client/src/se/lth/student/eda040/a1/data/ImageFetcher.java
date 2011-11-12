package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.ImageTransferer;
import se.lth.student.eda040.a1.AwesomeVideoView;
import se.lth.student.eda040.a1.network.Image;
import android.util.Log;

public class ImageFetcher extends Thread {
	private ClientMonitor monitor;
	private AwesomeVideoView videoView; 

	public ImageFetcher(ClientMonitor monitor, AwesomeVideoView videoView) {
		this.monitor = monitor;
		this.videoView = videoView;
	}

	public void run(){
		Image image;
		while(!interrupted()){
			image = monitor.awaitImage();
			videoView.post(new ImageTransferer(videoView, image));
			Log.d("ImageTransferer", "Fetched an image and posted an ImageTransferer");
		}
	}
}
