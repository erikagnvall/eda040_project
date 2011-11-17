package se.lth.cs.cameraproxy;

import java.net.*;
import java.io.*;

/**
 *
 * Motion detector.
 *
 * @author Emma Nilsson-Nyman (emma.nyman@cs.lth.se)
 * @author Roger Henriksson (roger@cs.lth.se)
 *
 */
public class MotionDetector {

    private int threshold;
    private int level;
    private String host;
    private int port;
    private Socket socket;
    private InputStream inp;
    private OutputStream outp;

    public MotionDetector(String host,int port) {
	this.host = host;
	this.port = port+1;
	level = 0;
	threshold = 5;
	try {
	    socket = new Socket(host,this.port);
	    inp = socket.getInputStream();
	    outp = socket.getOutputStream();
	} catch(IOException e) {
	    System.err.println("Could not connect to Axis camera in MotionDetector.");
	    System.exit(1);
	}
    }


    private void updateLevel() {
	try {
	    outp.write(42);
	    outp.flush();
	    level = inp.read();
	} catch(IOException e) {
	    System.err.println("Could not connect to Axis camera in MotionDetector.");
	    System.exit(1);
	}
    }

    public int getLevel() {
      updateLevel();
      return level;
    }

    public int getThreshold() {
      return threshold;
    }

    public boolean detect() {
      updateLevel();
      return level > threshold;
    }

    public void close() {
	//try {
	    socket.close();
	//} catch(IOException e) {}
	}
}
