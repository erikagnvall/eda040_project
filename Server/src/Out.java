import java.net.Socket;


public class Out extends Thread {

	private ServerProtocol sp;
	private ImageMonitor im;
	
	public Out(ServerProtocol sp, ImageMonitor im) {
		this.sp = sp;
		this.im = im;
	}
	
	public void run() {
		while (true) {
			sp.sendImage(im.getImage());
		}
	}
	
}
