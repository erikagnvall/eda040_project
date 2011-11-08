package se.lth.student.eda040.a1;

import se.lth.student.eda040.a1.network.Image;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import 	android.util.Log;
import android.graphics.Canvas;
import android.view.View.MeasureSpec;


public class AwesomeVideoView extends LinearLayout {
	private ImageView view1;
	private ImageView view2;

	public AwesomeVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d("aaaaaaaaaaaaaaaaaaaaaa", "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
	}

	//public AwesomeVideoView(Context context) {
		//super(context);
		//this.view1 = new ImageView(context);
		//this.view2 = new ImageView(context);
		//int myHeigth = getHeigth();
		//int myWidth = getWidth();
		//view1.set
		//view1.setImageBitmap(BitmapFactory.decodeFile("/sdcard/test2.jpg"));

	//}
	public void onFinishInflate() {
		this.view1 = (ImageView) findViewById(R.id.view1);
		this.view2 = (ImageView) findViewById(R.id.view2);
		view1.setImageBitmap(BitmapFactory.decodeFile("/sdcard/test2.jpg"));
		view2.setImageBitmap(BitmapFactory.decodeFile("/sdcard/test2.jpg"));
		Log.d("d", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
	}

	//public void onDraw(Canvas canvas) {
		//view1.draw(canvas);
		//view2.draw(canvas);
	//}
	
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measureSpecHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) / 2, MeasureSpec.getMode(heightMeasureSpec));

		view1.measure(widthMeasureSpec, measureSpecHeight);
		view2.measure(widthMeasureSpec, measureSpecHeight);

		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}
	
	
	public void drawImage(Image image) {
		ImageView view = null;
		switch (image.getCameraId()) {
			case 0:
				view = view1;
				break;
			case 1:
				view = view2;
				break;
		}
		view.setImageBitmap(image.toBitmap());
	}

	//@Override
	//protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		//// TODO 
		//if (changed) {
			//view1.layout(0, top, right, bottom / 2);
			//view2.layout(left, top + bottom / 2, right, bottom);
		//}
		
	//}

}
