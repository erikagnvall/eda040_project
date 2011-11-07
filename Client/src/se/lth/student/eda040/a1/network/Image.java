package se.lth.student.eda040.a1.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Image {
	private byte cameraID; 
	private byte[] data;
	private boolean videoMode;

	public Image(byte cameraID, byte[] data, boolean videoMode) {
		this.cameraID = cameraID;
		this.data = data;
		this.videoMode = videoMode;
	}
	
	public byte getCameraID() {
		return cameraID;
	}

	public Bitmap toBitmap() {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
}
