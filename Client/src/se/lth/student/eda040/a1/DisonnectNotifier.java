package se.lth.student.eda040.a1;

import se.lth.student.eda040.a1.AwesomeVideoView;
import android.util.Log;


public class DisonnectNotifier implements Runnable {
	private AwesomeVideoView videoView;
	private Byte disconnectedCamera;

	public DisonnectNotifier(AwesomeVideoView videoView, Byte disconnectedCamera) {
		this.videoView = videoView;
		this.disconnectedCamera = disconnectedCamera;
	}

	public void run() {
		Log.d("DisonnectNotifier", "Now calling videoView.disconnect()");
		videoView.disconnect(disconnectedCamera);
	}
}
