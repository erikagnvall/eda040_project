package se.lth.student.eda040.a1.data;

import se.lth.student.eda040.a1.network.Image;

class ImageFetcher extends Thread {
	private ClientMonitor monitor;

	public ImageFetcher(ClientMonitor monitor){ // TODO act param
		this.monitor = monitor;
	}

	public void run(){
		while(!interrupted()){
			Image img = monitor.awaitImage();
			// TODO act.displayImage(img)
		}
	}

}
