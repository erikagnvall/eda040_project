import java.io.IOException;


public class In extends Thread {

	private ServerProtocol sp;
	private ImageMonitor im;
	
	public In(ServerProtocol sp, ImageMonitor im) {
		this.sp = sp;
		this.im = im;
	}
	
	public void run() {
		while (!interrupted()) {
			byte cmd = 0;
			try {
				cmd = sp.awaitCommand();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				im.disconnect();
			}
			switch (cmd) {
			case ServerProtocol.IDLE_MODE: im.setVideo(false); break;
			case ServerProtocol.VIDEO_MODE: im.setVideo(true); break;
			case ServerProtocol.DISCONNECT: im.disconnect(); break;
			default: System.err.println("Unknown command recieved.");
			}
			
			
		}
	}

}
