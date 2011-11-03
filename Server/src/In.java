
public class In {

	private ServerProtocol sp;
	private ImageMonitor im;
	
	public In(ServerProtocol sp, ImageMonitor im) {
		this.sp = sp;
		this.im = im;
	}
	
	public void run() {
		while (true) {
			int cmd = sp.awaitCommand();
			// doet!
		}
	}

}
