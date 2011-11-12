package se.lth.student.eda040.a1.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Image {
	private byte cameraID; 
	private byte[] data;
	private long timestamp;
	private boolean videoMode;

	public Image(byte cameraID, byte[] data, long timestamp, boolean videoMode) {
		this.cameraID = cameraID;
		this.data = data;
		this.timestamp = timestamp;
		this.videoMode = videoMode;
	}
	
	public byte[] getImageData(){
		return data;
	}

	public boolean isVideoMode(){
		return videoMode;
	}

	public byte getCameraId() {
		return cameraID;
	}

	public long getTimstamp() {
		return timestamp;
	}

	public Bitmap toBitmap() {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
}
