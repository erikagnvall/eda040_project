package se.lth.student.eda040.a1.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Image implements Comparable<Image>{
	private byte cameraID; 
	private byte[] data;
	private long timestamp;
	private boolean videoMode;
	private boolean wasTrigger;

	public Image(byte cameraID, byte[] data, long timestamp, boolean videoMode) {
		this.cameraID = cameraID;
		this.data = data;
		this.timestamp = timestamp;
		this.videoMode = videoMode;
		this.wasTrigger = false;
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

	public long getTimestamp() {
		return timestamp;
	}

	public int getCurrentDelay(){
		return (int)(System.currentTimeMillis() - this.timestamp);
	}

	public Bitmap toBitmap() {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	public int compareTo(Image other){
		return (int)(this.timestamp - other.timestamp);
	}

	public boolean wasTrigger() {
		return wasTrigger;
	}

	public void setTrigger(boolean trigged) {
		this.wasTrigger = trigged;
	}
}
