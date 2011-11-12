package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.network.*;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import android.util.Log;

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
		Log.d("ClientMonitor", "Put image in buffer");
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
		Log.d("ClientMonitor", "Polled image in buffer");
		notifyAll(); 
		return imageBuffer.poll();
	}

	public synchronized void addProtocol(byte cameraId, ClientProtocol protocol) {
		protocols.put(cameraId, protocol);
	}

	public synchronized boolean connectTo(byte cameraId, String host) {
		boolean success = false;
		if (protocols.containsKey(cameraId)) {
			ClientProtocol protocol = protocols.get(cameraId);
			try {
				protocol.connectTo(host);
				Log.d("ClientMonitor", "Connecting camera " + cameraId);
				success = true;
			} catch (Exception e)  {
				success = false;
				Log.d("ClientMonitor", "caught exception" + e.toString());
			}
		} else {
			Log.d("ClientMonitor", "Could not fins protocol for camera " + cameraId);
			System.err.println("missing protocol.");
		}

		if (success) {
			Log.d("ClientMonitor", "Successfully connected to host with " + cameraId);
			connected[cameraId] = true;
			notifyAll();
		} else {
			Log.d("ClientMonitor", "Unsuccessfull connecttion to host with " + cameraId);
		}
		return success;
	}

	public synchronized void disconnectCamera(byte cameraId) {
		if (protocols.containsKey(cameraId)) {
			try {
			protocols.get(cameraId).disconnect();
			} catch (IOException ioe) {
				// TODO what to do?
			}
		}
		connected[cameraId] = false;
		Log.d("ClientMonitor", "Disconnected camera " + cameraId);
	}

	public synchronized void connectionCheck(byte cameraId) {
		while (!connected[cameraId]) {
			try {
				Log.d("ClientMonitor", "Waiting in connectionCheck for camera " + cameraId);
				wait();
			} catch (InterruptedException ie) {
				System.err.println("Broken bed.");
			}
		}
		Log.d("ClientMonitor", "Released in connectionCheck for camera " + cameraId);
	}

	public synchronized boolean isConnectedCamera(byte cameraId) {
		return connected[cameraId];
	}
}
