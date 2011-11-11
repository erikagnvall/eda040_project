package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.network.*;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class ClientMonitor {
	private Queue<Command> commandQueue;
	private Queue<Image> imageBuffer;
	private Map<Byte, ClientProtocol> cameras;

	public ClientMonitor() {
		commandQueue = new LinkedList<Command>();
		imageBuffer = new LinkedList<Image>();
		cameras = new HashMap<Byte, ClientProtocol>();
	}

	public synchronized void putCommand(Command command, int cameraID){
		commandQueue.offer(command);
		notifyAll();
	}

	public synchronized Command awaitCommand(int cameraID){
		while (commandQueue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException ie) {
				System.err.println("Wait got interrupted.");
			}
		}
		return commandQueue.poll();
	}

	public synchronized void putImage(Image image, int camera){
		imageBuffer.offer(image);
		notifyAll();
	}

	public synchronized Image awaitImage() {
		while (imageBuffer.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException ie){
				System.err.println("Wait interrupted.");
			}
		}
		return imageBuffer.poll();
	}

	public synchronized void awaitStateChange() {
		// wait for command in what buffer?
	}
	
	public synchronized void connectCamera(byte id, String host) throws CouldNotConnectException, IOException {
		// TODO instanciate In and Out threads here? And main-thread calling this method should not instanciate ClientProtocol (and the Socketj) since that is blocking?
		if (!cameras.containsKey(id)) {
			cameras.put(id, new ClientProtocol(id, host));
		} else {
			throw new IOException("Camera " + id + " allready connected.");
		}
	}
}
