package se.lth.student.eda040.a1.network;

public class Image {
	private byte[] data;
	private boolean videoMode;

	public Image(byte[] data, boolean videoMode) {
		this.data = data;
		this.videoMode = videoMode;
	}

}
