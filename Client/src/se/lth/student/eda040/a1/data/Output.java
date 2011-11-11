package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.network.*;
import java.io.IOException;

public class Output extends Thread {
	ClientProtocol protocol;
	ClientMonitor monitor;

	public Output(ClientMonitor monitor, ClientProtocol protocol) {
		this.monitor = monitor;
		this.protocol = protocol;
	}

	public void run(){
		Command command;
		while(!interrupted()){
			try {
				command = monitor.awaitCommand(protocol.getCameraId());
				protocol.sendCommand(command);
			} catch (IOException ie) {
				// TODO 
			}
		}

	}
}
