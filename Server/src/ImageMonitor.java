import java.net.Socket;

public class ImageMonitor {

	private static final int IDLE_PERIOD = 5000;

	private boolean isConnected;
	private boolean isVideo;
	private Image image;
	private long fetchedImageAt;
	
	public ImageMonitor() {
		isVideo = false;
		isConnected = false;
		this.fetchedImageAt = 0;
	}
	
	public synchronized boolean isVideo() {
		return isVideo;
	}
		
	public synchronized void putImage(Image image) {
		this.image = image;
		notifyAll();
	}
	
	/**Returns next image to be sent.
	 * Waits until the image should be sent.
	 * Waits until connection is set.
	 * Will never return null.
	 */
	public synchronized Image getImage() throws InterruptedException {
		awaitConnected();
		long stopTime = this.fetchedImageAt + IDLE_PERIOD;
		long ttw = stopTime - System.currentTimeMillis();
		while (ttw > 0 && !isVideo) {
			wait(ttw);
			ttw = stopTime - System.currentTimeMillis();
		}
		while (image == null) {
			wait();
		}
		this.fetchedImageAt = System.currentTimeMillis();
		Image tmp = image;
		image = null;
		return tmp;
	}

	private void awaitConnected(){
		while (!isConnected) {
			wait();
		}
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
		awaitConnected();
	}
}
