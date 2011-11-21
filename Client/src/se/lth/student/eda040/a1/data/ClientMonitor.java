package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.network.*;

import java.util.Queue;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.net.UnknownHostException;
import android.util.Log;

public class ClientMonitor {

    public static final int SYNC_THRESHOLD = 200;
    private int nunsync;
	private Queue<Command>[] commandQueues;
	private Queue<Image> imageBuffer;
	private boolean[] isVideoMode;
	private Map<Byte, ClientProtocol> protocols;
	private boolean[] connected;
    private boolean isSyncMode;
    private long[] latestTimestamp;
    private int[] delay;
    private long delayNextUntil;

	public ClientMonitor() {
		commandQueues = (LinkedList<Command>[]) new LinkedList[2];
		commandQueues[0] = new LinkedList<Command>();
		commandQueues[1] = new LinkedList<Command>();
		imageBuffer = new PriorityQueue<Image>();
		isVideoMode = new boolean[2];
		protocols = new HashMap<Byte, ClientProtocol>();
		connected = new boolean[2];
		isSyncMode = false;
		delay = new int[2];
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
	    byte cameraId = image.getCameraId();
	    byte otherCamera = (byte) (((int) cameraId + 1) % 2);
	    int delay;

	    delay = (int)(System.currentTimeMillis() - image.getTimestamp());
	    image.setDelay(delay);


		imageBuffer.offer(image);

		// If videomode distribute comand to other camera
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

	    int delayDiff = 0;
	    int cameraId = 0;
		//Log.d("ClientMonitor", "Waiting for image in buffer");
		while (imageBuffer.isEmpty()) {
			wait();
		}
		p1 = imageBuffer.poll();
		cameraId = p1.getCameraId();
		delayDiff = Math.abs(p1.getDelay() - delay[cameraId]);
		if ((delayDiff < SYNC_THRESHOLD || nunsync < 15) && p1.getDelay() < 1000) {
		    wait(1000 - p1.getDelay());
		    nunsync = 0;
		} else {
		    nunsync++;
		}
		delay[cameraId] = p1.getDelay();
		notifyAll(); 
		Log.d("ClientMonitor", "Released image");
		return p1;
	}

	public synchronized void addProtocol(byte cameraId, ClientProtocol protocol) {
		protocols.put(cameraId, protocol);
		System.out.println(protocols.size());
	}

    /**
     * Connects the specified camera to the specified host.
     * Notifies threads waiting for connection.
     * Returns true if successfull.
     * Throws IllegalArgumentException if the cameraId is invalid.
     * Throws Exceptions also thrown by socket.connect().
     */
	public synchronized void connectTo(byte cameraId, String host)
            throws UnknownHostException, IOException, IllegalArgumentException{
		if (protocols.containsKey(cameraId)) {
			ClientProtocol protocol = protocols.get(cameraId);
			protocol.connectTo(host);
			Log.d("ClientMonitor", "Camera: " + cameraId + " is connected.");
		} else {
			Log.d("ClientMonitor", "Could not find protocol for camera " + cameraId);
			throw new IllegalArgumentException("No camera with id: " + cameraId + "!");
		}

		// TODO this could be simplified rigth? The camera should be connected if
		// we've gotten this far.
		Log.d("ClientMonitor", "Successfully connected to host with " + cameraId);
		connected[cameraId] = true;
		notifyAll();
	}

	/**
	 * Sends command to camera server to disconnect.
	 */
	public synchronized void gracefullDisconnect(byte cameraId) {
		if (protocols.containsKey(cameraId)) {
			putCommand(new Command(Command.DISCONNECT, protocols.get(cameraId)), cameraId);
		}
		connected[cameraId] = false;
		Log.d("ClientMonitor", "Gracefull disconnected camera " + cameraId);
	}

	/**
	 * Emergency disconnect.
	 * To be used in emergencies only! Will without warning disconnect the specified camera socket.
	 */
	public synchronized void disconnect(byte cameraId) {
		if (protocols.containsKey(cameraId) && connected[cameraId]) {
			connected[cameraId] = false;
			protocols.get(cameraId).disconnect();
		}
		Log.d("ClientMonitor", "Disconnected camera " + cameraId);
	}

	public synchronized void connectionCheck(byte cameraId) throws InterruptedException{
		//Log.d("ClientMonitor", "Waiting in connectionCheck for camera " + cameraId);
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
