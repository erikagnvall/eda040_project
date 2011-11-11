package se.lth.student.eda040.a1;

import se.lth.student.eda040.a1.network.Image;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;



public class AwesomeVideoView extends LinearLayout {
	private ImageView view1;
	private ImageView view2;

	public AwesomeVideoView(Context context, ImageView view1, ImageView view2) {
		super(context);
		this.view1 = view1;
		this.view2 = view2;
		view1.setImageBitmap(BitmapFactory.decodeFile("/sdcard/test2.jpg"));
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
