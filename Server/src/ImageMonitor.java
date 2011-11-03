
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		img = null;
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
