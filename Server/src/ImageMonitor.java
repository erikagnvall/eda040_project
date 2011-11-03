
public class ImageMonitor {
	//out/in
	private boolean isConnected;
	private boolean isVideo;
	private Image img;
	
	public ImageMonitor() {
		isVideo = false;
	}
	
	public boolean isVideo() {
		return isVideo;
	}
		
	public synchronized void putImage(Image img) {
		this.img = img;
		notifyAll();
	}
	
	public synchronized Image getImage() {
		while (img == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println("getImg interrupted");
			}
		}
		Image tmp = img;
		img = null;
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
