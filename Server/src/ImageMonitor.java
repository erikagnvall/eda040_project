
public class ImageMonitor {
	//out/in
	private boolean isConnected;
	private boolean isVideo;
	Image img;
	
	public ImageMonitor() {
		isVideo = false;
	}
	
	public synchronized void putImage(Image img) {
		this.img = img;
	}
	
	public synchronized Image getImage() {
		return img;
	}
	
	public synchronized void setVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}
	
	public boolean hasConnection() {
		return this.isConnected;
	}
	
	public void disconnect() {
		// you probably thought this method was alive.
		// NOPE!
	}
}
