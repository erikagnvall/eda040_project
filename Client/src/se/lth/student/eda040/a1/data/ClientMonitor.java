package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.network.*;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public class ClientMonitor {
	private Queue<Command> commandQueue;
	private Queue<Image> imageBuffer;
	private Map<Byte, ClientProtocol> protocols;
	private boolean[] connected;

	public ClientMonitor() {
		commandQueue = new LinkedList<Command>();
		imageBuffer = new LinkedList<Image>();
		protocols = new HashMap<Byte, ClientProtocol>();
		connected = new boolean[2];
	}

	public synchronized void putCommand(Command command, int cameraId){
		commandQueue.offer(command);
		notifyAll();
	}

	public synchronized Command awaitCommand(int cameraId){
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

	public synchronized boolean connectTo(byte cameraId, String host) {
		boolean success = false;
		if (!protocols.containsKey(cameraId)) {
			ClientProtocol protocol = protocols.get(cameraId);
			try {
				protocol.connectTo(host);
				success = true;
			} catch (Exception e)  {
				success = false;
			}
		}

		if (success) {
			connected[cameraId] = true;
			notifyAll();
		}

		return success;
	}

	public synchronized void connectionCheck(byte cameraId) {
		while (!connected[cameraId]) {
			try {
				wait();
			} catch (InterruptedException ie) {
				System.err.println("Broken bed.");
			}
		}
	}
}
