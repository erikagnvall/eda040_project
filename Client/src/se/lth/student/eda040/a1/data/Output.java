package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.network.*;
import java.io.IOException;

public class Output extends Thread {
	ClientProtocol protocol;
	ClientMonitor monitor;

	public Output(ClientMonitor monitor, ClientProtocol protocol ) {
		this.protocol = protocol;
		this.monitor = monitor;
	}

	public void run(){
		while(!interrupted()){
			try {
				Command cmd = monitor.awaitCommand(protocol.getCameraID());
				protocol.sendCommand(cmd);
			} catch (IOException ie) {
				// TODO 
			}
		}

	}

}
