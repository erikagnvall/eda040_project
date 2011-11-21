import java.net.Socket;

public class ImageMonitor {
	private boolean isConnected;
	private boolean isVideo;
	private Image image;
	
	public ImageMonitor() {
		isVideo = false;
		isConnected = false;
	}
	
	public synchronized boolean isVideo() {
		return isVideo;
	}
		
	public synchronized void putImage(Image image) {
		this.image = image;
		notifyAll();
	}
	
	public synchronized Image getImage() throws InterruptedException {
		if (!isVideo) {
			long stopTime = System.currentTimeMillis() + 5000;
			long ttw;
			while (stopTime > System.currentTimeMillis()) {
			    ttw = stopTime - System.currentTimeMillis();
			    if (ttw > 0) //could it bee a timing issue where we stuck when we get negative on the second calc. Maybe hadled badly when xcompiled? who knows...
				wait(ttw);
			}
		}
		while (image == null) {
			wait();
		}
		Image tmp = image;
		image = null;
		return tmp;
	}
	
	public synchronized void setVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}
	
	public synchronized boolean hasConnection() {
		return this.isConnected;
	}
	
	public synchronized void disconnect() {
		isConnected = false;
		notifyAll();
	}

    public synchronized void setConnection() {
		System.out.println("Monitor seting up a new connection.");
		isConnected = true;
		notifyAll(); 
    }
	
	public synchronized void awaitDisconnect() throws InterruptedException{
		while (isConnected) {
			wait();
		}
	}
	
	public synchronized void connectionCheck() throws InterruptedException {
		while (!isConnected) {
			wait();
		}
	}
}
