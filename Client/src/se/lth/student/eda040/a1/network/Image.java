package se.lth.student.eda040.a1.network;

public class Image {
	private int cameraID; // TODO byte is enough.
	private byte[] data;
	private boolean videoMode;

	public Image(int cameraID, byte[] data, boolean videoMode) {
		this.cameraID = cameraID;
		this.data = data;
		this.videoMode = videoMode;
	}
	
	public byte[] getImageData(){
		return data;
	}

	public int getCameraId(){
		return cameraID;
	}

	public boolean isVideoMode(){
		return videoMode;
	}
}
