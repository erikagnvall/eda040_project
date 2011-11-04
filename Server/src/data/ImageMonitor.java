package data;

import network.Image;

public class ImageMonitor {
	//out/in
	private boolean isConnected;
	private boolean isVideo;
	private Image image;
	
	public ImageMonitor() {
		isVideo = false;
		isConnected = false;
	}
	
	public boolean isVideo() {
		return isVideo;
	}
		
	public synchronized void putImage(Image image) {
		this.image = image;
		notifyAll();
	}
	
	public synchronized Image getImage() {
		while (image == null && isConnected) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println("getImag interrupted");
			}
		}
		Image tmp = image;
		image = null;
		return tmp;
	}
	
	public synchronized void setVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}
	
	public boolean hasConnection() {
		return this.isConnected;
	}
	
	public synchronized void disconnect() {
		isConnected = false;
		notifyAll();
	}

    public synchronized void connect() {
	isConnected = true;
    }
	
	public synchronized void awaitDisconnect() {
		while (isConnected) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println("Interrupted in awaitDisc.");
			}
		}
	}
	
}
