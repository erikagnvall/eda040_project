import java.net.Socket;

public class ImageMonitor {
	private boolean isConnected;
	private boolean isVideo;
	private Image image;
	private ServerProtocol protocol;
	
	public ImageMonitor(ServerProtocol protocol) {
		this.protocol = protocol;
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
	
	public synchronized Image getImage() throws InterruptedException {
		while (!isConnected) {
			wait();
		}
		if (!isVideo) {
			long stopTime = System.currentTimeMillis() + 5000;
			while (stopTime > System.currentTimeMillis()) {
				wait(stopTime - System.currentTimeMillis());
			}
		}
		while (image == null && isConnected) {
			wait();
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

    public synchronized void setConnection(Socket socket) {
		System.out.println("Monitor seting up a new connection.");
		protocol.setConnection(socket);
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
