package se.lth.student.eda040.a1;

import se.lth.student.eda040.a1.network.Image;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.Canvas;
import android.view.View.MeasureSpec;


public class AwesomeVideoView extends LinearLayout {
	private ImageView view0;
	private ImageView view1;

	public AwesomeVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void onFinishInflate() {
		this.view0 = (ImageView) findViewById(R.id.view0);
		this.view1 = (ImageView) findViewById(R.id.view1);
		view0.setImageBitmap(BitmapFactory.decodeFile("/sdcard/test2.jpg"));
		view1.setImageBitmap(BitmapFactory.decodeFile("/sdcard/test2.jpg"));
	}

	//public void onDraw(Canvas canvas) {
		//view0.draw(canvas);
		//view1.draw(canvas);
	//}
	
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measureSpecHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) / 2, MeasureSpec.getMode(heightMeasureSpec));

		view0.measure(widthMeasureSpec, measureSpecHeight);
		view1.measure(widthMeasureSpec, measureSpecHeight);

		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}
	
	
	public void drawImage(Image image) {
		ImageView view = null;
		switch (image.getCameraId()) {
			case 0:
				view = view0;
				break;
			case 1:
				view = view1;
				break;
		}
		Log.d("VideoView", "Recieved image to draw from camera " + image.getCameraId());
		view.setImageBitmap(image.toBitmap());
	}
}
