package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.network.*;
import java.util.Queue;
import java.util.LinkedList;

public class ClientMonitor {
	private Queue<Command> commandQueue;
	private Queue<Image> imageBuffer;

	public ClientMonitor() {
		commandQueue = new LinkedList<Command>();
		imageBuffer = new LinkedList<Image>();
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
}
