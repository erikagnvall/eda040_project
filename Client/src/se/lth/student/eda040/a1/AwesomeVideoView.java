package se.lth.student.eda040.a1;

import se.lth.student.eda040.a1.network.Image;
import se.lth.student.eda040.a1.data.ClientMonitor;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.Canvas;
import android.view.View.MeasureSpec;
import android.view.Gravity;
import android.widget.TextView;


public class AwesomeVideoView extends LinearLayout {
	private VideoActivity videoActivity;
	private AwesomeFrameLayout frame0;
	private AwesomeFrameLayout frame1;
	private TextView tv;

	public AwesomeVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d("AwesomeVideoView", "In constructor.");
	}

	public void onFinishInflate() {
		Log.d("AwesomeVideoView", "In onFinishInflate.");
		this.frame0 = (AwesomeFrameLayout) findViewById(R.id.frame0);
		this.frame1 = (AwesomeFrameLayout) findViewById(R.id.frame1);
		frame0.setCameraId((byte) 0);
		frame1.setCameraId((byte) 1);
		this.tv = (TextView) findViewById(R.id.infotext);
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d("AwesomeVideoView", "Now measuring");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		tv.measure(widthMeasureSpec, 0);
		int tvHeight = tv.getMeasuredHeight();
		// Do not remove the + 1 in the code line below, displays a pixel of white otherwise.
		int measureSpecHeight = MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(heightMeasureSpec) - tvHeight + 1) / 2, MeasureSpec.getMode(heightMeasureSpec));
		//int measureSpecHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.getMode(heightMeasureSpec));

		frame0.measure(widthMeasureSpec, measureSpecHeight);
		frame1.measure(widthMeasureSpec, measureSpecHeight);

		//setMeasuredDimension(widthMeasureSpec, measureSpecHeight); //dont call, brejks overlay
	}

	public void setVideoActivity(VideoActivity videoActivity) {
		this.videoActivity = videoActivity;
	}
	
	public void setMonitor(ClientMonitor monitor) {
		frame0.setMonitor(monitor);
		frame1.setMonitor(monitor);
	}
	
	public void drawImage(Image image) {
		AwesomeFrameLayout frame = null;
		switch (image.getCameraId()) {
			case 0:
				frame = frame0;
				break;
			case 1:
				frame = frame1;
				break;
		}
		Log.d("VideoView", "Recieved image to draw from camera " + image.getCameraId());
		frame.drawImage(image);
	}

	public void disconnectedCamera(byte cameraId) {
		Log.d("AwesomeVideoView", "Disconnecting camera " + cameraId);
		AwesomeFrameLayout frame = null;
		switch (cameraId) {
			case 0:
				frame = frame0;
				break;
			case 1:
				frame = frame1;
				break;
		}
		frame.disconnected();
	}

	/**
	 * Disconnection caused by error on conenction, maybe the server died.
	 **/
	//public void disconnectEmergency(byte cameraId) {
	//	Log.d("AwesomeVideoView", "Emegency disconnect for camera" + cameraId);
	//	if (videoActivity != null) {
	//		videoActivity.emergencyDisconnenctCamera(cameraId);
	//	} else {
	//		Log.d("AwesomeVideoView", "Forgot to give me an activity.");
	//	}
	//}
}
