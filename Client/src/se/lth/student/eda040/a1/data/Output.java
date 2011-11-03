package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.network.*;
import java.io.IOException;

public class Output extends Thread {

	ClientProtocol protocol;
	ClientMonitor monitor;

	public Output(ClientProtocol protocol, ClientMonitor monitor) {
		this.protocol = protocol;
		this.monitor = monitor;
	}

	public void run(){
		while(!interrupted()){
			Command cmd = monitor.awaitCommand(protocol.getCameraId());
			try {
			protocol.sendCommand(cmd);
			} catch (IOException ie) {
				// TODO 
			}
		}

	}

}
