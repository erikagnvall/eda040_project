package se.lth.student.eda040.a1;

import se.lth.student.eda040.a1.network.Image;
import se.lth.student.eda040.a1.data.ClientMonitor;
import java.util.Map;
import java.util.HashMap;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.Canvas;
import android.view.View.MeasureSpec;
import android.view.Gravity;


public class AwesomeVideoView extends LinearLayout {

	private Map<Integer, AwesomeFrameLayout> frameList;

	public AwesomeVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d("AwesomeVideoView", "In constructor.");
	}

	public void onFinishInflate() {
		Log.d("AwesomeVideoView", "In onFinishInflate.");
		this.frameList = new HashMap<Integer, AwesomeFrameLayout>();
		AwesomeFrameLayout frame0 = (AwesomeFrameLayout) findViewById(R.id.frame0);
		AwesomeFrameLayout frame1 = (AwesomeFrameLayout) findViewById(R.id.frame1);
		frame0.setCameraId((byte) 0);
		frame1.setCameraId((byte) 1);
		this.frameList.put(0, frame0);
		this.frameList.put(1, frame1);
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//Log.d("AwesomeVideoView", "Now measuring");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measureSpecHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) / 2, MeasureSpec.getMode(heightMeasureSpec));

		for(AwesomeFrameLayout f : this.frameList.values()){
			f.measure(widthMeasureSpec, measureSpecHeight);
		}
	}

	public void setMonitor(ClientMonitor monitor) {
		for(AwesomeFrameLayout f : this.frameList.values()){
			f.setMonitor(monitor);
		}
	}

	public void drawImage(Image image) {
		//Log.d("VideoView", "Recieved image to draw from camera " + image.getCameraId());
		this.frameList.get((int)image.getCameraId()).drawImage(image);
	}

	public void disconnectedCamera(byte cameraId) {
		//Log.d("AwesomeVideoView", "Camera disconnected: " + cameraId);
		this.frameList.get((int)cameraId).disconnected();
	}
}
