package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.network.*;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import android.util.Log;

public class ClientMonitor {

    public static final double SYNC_THRESHOLD = 0.2;

	private Queue<Command>[] commandQueues;
	private LinkedList<Image> imageBuffer;
	private boolean[] isVideoMode;
	private Map<Byte, ClientProtocol> protocols;
	private boolean[] connected;
    private boolean isSyncMode;
    private long[] latestTimestamp;
    private long delayNextUntil;

	public ClientMonitor() {
		commandQueues = (LinkedList<Command>[]) new LinkedList[2];
		commandQueues[0] = new LinkedList<Command>();
		commandQueues[1] = new LinkedList<Command>();
		imageBuffer = new LinkedList<Image>();
		isVideoMode = new boolean[2];
		protocols = new HashMap<Byte, ClientProtocol>();
		connected = new boolean[2];
		isSyncMode = false;
		latestTimestamp = new long[2];
		delayNextUntil = 0;
	}

	// TODO private
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

	public synchronized void putImage(Image image){
	    LinkedList<Image> tmp = null;
	    long deltaT = 0;
	    byte cameraId = image.getCameraId();
	    byte otherCamera = (byte) (((int) cameraId + 1) % 2);

	    if (latestTimestamp[cameraId] < image.getTimstamp())
		latestTimestamp[cameraId] = image.getTimstamp();

	    deltaT = Math.abs(latestTimestamp[0] - latestTimestamp[1]);
	    isSyncMode = deltaT < SYNC_THRESHOLD;

	    if (isSyncMode) {
		tmp = new LinkedList<Image>();
		while (imageBuffer.size() > 0 && imageBuffer.getLast().getTimstamp() > image.getTimstamp()) {
		    tmp.addFirst(imageBuffer.removeLast());
		}
		imageBuffer.addLast(image);
		imageBuffer.addAll(tmp);
	    } else {
		imageBuffer.offer(image);
	    }

	    if (connected[otherCamera] && !isVideoMode[cameraId] && image.isVideoMode()) {
		putCommand(new Command(Command.MODE_VIDEO, protocols.get(otherCamera)), otherCamera);
	    }

	    isVideoMode[cameraId] = image.isVideoMode();
	    Log.d("ClientMonitor", "Put image in buffer");
	    notifyAll(); 
	}

	public synchronized Image awaitImage() throws InterruptedException{

	    Image p1 = null;
	    Image p2 = null;

		Log.d("ClientMonitor", "Waiting for image in buffer");
		while (imageBuffer.isEmpty()) {
			wait();
		}
		while (System.currentTimeMillis() < delayNextUntil)
		    wait(delayNextUntil - System.currentTimeMillis());

		if (isSyncMode && imageBuffer.size() >= 2) {
		    p1 = imageBuffer.get(1);
		    p2 = imageBuffer.get(2);
		    // if there is no image to delay for the time is already up and there is no need to set he delaytime to now. Promise.
		    if (p1.getCameraId() != p2.getCameraId())
			this.delayNextUntil = p2.getTimstamp() - p1.getTimstamp() + System.currentTimeMillis();
		} else {
		    this.delayNextUntil = System.currentTimeMillis();
		}
		
		Log.d("ClientMonitor", "Polled image in buffer");
		notifyAll(); 
		return imageBuffer.poll();
	}

	public synchronized void addProtocol(byte cameraId, ClientProtocol protocol) {
		protocols.put(cameraId, protocol);
		System.out.println(protocols.size());
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
			putCommand(new Command(Command.DISCONNECT, protocols.get(cameraId)), cameraId);
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
	
	public synchronized void setIdleMode() {
		for (ClientProtocol protocol : protocols.values()) {
			if (connected[protocol.getCameraId()]) {
				putCommand(new Command(Command.MODE_IDLE, protocol), protocol.getCameraId());
			}
		}
	}
}
