package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.DisonnectNotifier;
import se.lth.student.eda040.a1.AwesomeVideoView;
import se.lth.student.eda040.a1.network.Image;
import android.util.Log;
import android.os.Handler;

public class DisconnectionDetecter extends Thread {
	private ClientMonitor monitor;
	private AwesomeVideoView videoView;
	private Handler handler; 

	public DisconnectionDetecter(ClientMonitor monitor, AwesomeVideoView videoView, Handler handler) {
		this.monitor = monitor;
		this.videoView = videoView;
		this.handler = handler;
	}
	public void run(){
		Byte disconnectedCamera;
		while(!interrupted()){
			try {
				disconnectedCamera = monitor.awaitDisconnection();
				handler.post(new DisonnectNotifier(videoView, disconnectedCamera));
				Log.d("ConnectionDetecter", "Camera " + disconnectedCamera + " was disconnected server-side");
			} catch(InterruptedException ie) {
				// Do nothing
			}
		}
	}
}
