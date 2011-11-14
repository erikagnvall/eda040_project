package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.network.*;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import android.util.Log;

public class ClientMonitor {
	private Queue<Command>[] commandQueues;
	private Queue<Image> imageBuffer;
	private Map<Byte, ClientProtocol> protocols;
	private boolean[] connected;

	public ClientMonitor() {
		commandQueues = (LinkedList<Command>[]) new LinkedList[2];
		commandQueues[0] = new LinkedList<Command>();
		commandQueues[1] = new LinkedList<Command>();
		imageBuffer = new LinkedList<Image>();
		protocols = new HashMap<Byte, ClientProtocol>();
		connected = new boolean[2];
	}

	public synchronized void putCommand(Command command, int cameraId){
		commandQueues[cameraId].offer(command);
		notifyAll();
	}

	public synchronized Command awaitCommand(int cameraId) throws InterruptedException{
		while (commandQueues[cameraId].isEmpty()) {
			wait();
		}
		return commandQueues[cameraId].poll();
	}

	public synchronized void putImage(Image image, int camera){
		imageBuffer.offer(image);
		Log.d("ClientMonitor", "Put image in buffer");
		notifyAll(); 
	}

	public synchronized Image awaitImage() throws InterruptedException{
		Log.d("ClientMonitor", "Waiting for image in buffer");
		while (imageBuffer.isEmpty()) {
			wait();
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

	/**
	 * Sends command to camera server to disconnect.
	 */
	public synchronized void gracefullDisconnect(byte cameraId) {
		if (protocols.containsKey(cameraId)) {
			commandQueues[cameraId].offer(new Command(Command.DISCONNECT, cameraId, protocols.get(cameraId)));
		}
		connected[cameraId] = false;
		Log.d("ClientMonitor", "Disconnected camera " + cameraId);
	}

	/**
	 * Emergency disconnect.
	 * To be used in emergencies only! Will without warning disconnect the specified camera socket.
	 */
	public synchronized void disconnect(byte cameraId) {
		if (protocols.containsKey(cameraId)) {
			protocols.get(cameraId).disconnect();
		}
		connected[cameraId] = false;
		Log.d("ClientMonitor", "Disconnected camera " + cameraId);
	}

	public synchronized void connectionCheck(byte cameraId) throws InterruptedException{
		Log.d("ClientMonitor", "Waiting in connectionCheck for camera " + cameraId);
		while (!connected[cameraId]) {
			wait();
		}
		Log.d("ClientMonitor", "Released in connectionCheck for camera " + cameraId);
	}

	public synchronized boolean isConnectedCamera(byte cameraId) {
		return connected[cameraId];
	}
}
