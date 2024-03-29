package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.network.*;

import java.util.Queue;
import java.util.Collection;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.net.UnknownHostException;
import android.util.Log;

public class ClientMonitor {

	public static final int SYNC_THRESHOLD = 200;
	private static final int MAX_BUFFER_SIZE = 50;
	private int nunsync;
	private Queue<Command>[] commandQueues;
	private Queue<Image> imageBuffer;
	private Queue<Byte> disconnectionQueue;
	private boolean[] isVideoMode;
	private Map<Byte, ClientProtocol> protocols;
	private boolean[] connected;
	private int trigger;
	private SyncManager syncManager;

	public ClientMonitor() {
		commandQueues = (LinkedList<Command>[]) new LinkedList[2];
		commandQueues[0] = new LinkedList<Command>();
		commandQueues[1] = new LinkedList<Command>();
		imageBuffer = new PriorityQueue<Image>();
		isVideoMode = new boolean[2];
		protocols = new HashMap<Byte, ClientProtocol>();
		connected = new boolean[2];
		disconnectionQueue = new LinkedList<Byte>();
		trigger = -1;
		this.syncManager = new SyncManager(20, SYNC_THRESHOLD);
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

	public synchronized void putImage(Image image){
		byte cameraId = image.getCameraId();
		byte otherCamera = (byte) (((int) cameraId + 1) % 2);


		imageBuffer.offer(image);
		while(imageBuffer.size() > MAX_BUFFER_SIZE){
			imageBuffer.poll();
		}

		if (connected[otherCamera] && !isVideoMode[otherCamera] && image.isVideoMode()) {
			putCommand(new Command(Command.MODE_VIDEO, protocols.get(otherCamera)), otherCamera);
			this.trigger = cameraId;
		}
		if (cameraId == trigger) {
			image.setTrigger(true);
		}
		isVideoMode[cameraId] = image.isVideoMode();
		//Log.d("ClientMonitor", "Put image in buffer");
		notifyAll(); 
	}

	public synchronized Image awaitImage() throws InterruptedException{

		while (imageBuffer.isEmpty()) {
			wait();
		}
		Image image = imageBuffer.poll();

		//syncDelay(image);
		notifyAll(); 
		//Log.d("ClientMonitor", "Released image");
		return image;
	}

	private void syncDelay(Image img) throws InterruptedException {
		this.syncManager.addDelay(img.getCurrentDelay());
		int targetDelay = syncManager.getMeanDelay();
		int delay = 0;
		if(syncManager.isSync()){
			delay = img.getCurrentDelay();
			Log.d("ClientMonitor", "Syncing image! target: " + targetDelay +
					" delay: " + delay);
			while((delay = img.getCurrentDelay()) < targetDelay){
				wait(targetDelay - delay);
			}
		}
	}

	public synchronized void addProtocol(byte cameraId, ClientProtocol protocol) {
		protocols.put(cameraId, protocol);
		//System.out.println(protocols.size());
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
		disconnectionQueue.offer(cameraId);
		notifyAll();
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
			disconnectionQueue.offer(cameraId);
			notifyAll();
		}
		Log.d("ClientMonitor", "Disconnected camera " + cameraId);
	}

	public synchronized byte awaitDisconnection() throws InterruptedException {
		while (disconnectionQueue.isEmpty()) {
			wait();		
		}
		return disconnectionQueue.poll();
	}

	public synchronized void connectionCheck(byte cameraId) throws InterruptedException{
		while (!connected[cameraId]) {
			wait();
		}
		//Log.d("ClientMonitor", "Released in connectionCheck for camera " + cameraId);
	}

	public synchronized boolean isConnectedCamera(byte cameraId) {
		return connected[cameraId];
	}

	public synchronized boolean isVideoMode() {
		return isVideoMode[0] || isVideoMode[1];
	}

	public synchronized void setVideoMode(boolean video) {
		for (ClientProtocol protocol : protocols.values()) {
			if (connected[protocol.getCameraId()]) {
				byte cmd = video ? Command.MODE_VIDEO : Command.MODE_IDLE;
				Log.d("ClientMonitor", "about to send command: " + cmd);
				putCommand(new Command(cmd, protocol), protocol.getCameraId());
			}
		}
		if (!video) {
			this.trigger = -1;
		}
	}

	public synchronized Collection<String> getConnectedHosts(){
		ArrayList<String> hosts = new ArrayList<String>();
		for(ClientProtocol cp : protocols.values()){
			if(connected[cp.getCameraId()]){
				hosts.add(cp.getHost());
			}
		}
		return hosts;
	}

	private class SyncManager{
		private int[] delayHist;
		private int currentIndex;
		private int meanDelay;
		private int meanDiff;
		private int syncThreshold;

		public SyncManager(int size, int threshold){
			this.delayHist = new int[size];
			this.currentIndex = 0;
			this.meanDelay = 0;
			this.meanDiff = 0;
			this.syncThreshold = threshold;
		}

		public void addDelay(int delay){
			this.currentIndex = (this.currentIndex + 1) % this.delayHist.length;
			this.delayHist[this.currentIndex] = delay;
			update();
		}

		public int getMeanDelay(){
			return this.meanDelay;
		}

		private void update(){
			this.meanDelay = calcMeanDelay();
			this.meanDiff = getMeanDiff();
		}

		private int calcMeanDelay(){
			int sum = 0;
			for(int i = 0; i < this.delayHist.length; i++){
				sum += this.delayHist[i];
			}
			return sum / this.delayHist.length;
		}

		private int getMeanDiff(){
			int sum = 0;
			for(int i = 0; i < this.delayHist.length; i++){
				sum += Math.abs(this.meanDelay - this.delayHist[i]);
			}
			return sum / this.delayHist.length;
		}

		public boolean isSync(){
			return meanDiff <= syncThreshold;
		}
	}

}
