package se.lth.cs.cameraproxy;

import java.net.*;
import java.io.*;


/**
 *
 * Camera Proxy API for the Axis211A model. 
 *
 * @author Emma Nilsson-Nyman (emma.nyman@cs.lth.se)
 * @author Roger Henriksson (roger@cs.lth.se)
 *
 */ 
public class Axis211A {

    public static final int IMAGE_BUFFER_SIZE = 100000;

    private String host;
    private int port;
    private Socket socket;
    private InputStream inp;
    private OutputStream outp;

    /**
     * Constructor
     */
    public Axis211A(String host,int port) {
	this.host = host;
	this.port = port;
    }

    /**
     * Connects to the camera.
     *
     * @return true if connected otherwise false.
     */
    public boolean connect() {
	socket = null;
	try {
	    socket = new Socket(host,port);
	    inp = socket.getInputStream();
	    outp = socket.getOutputStream();
	} catch (IOException e) { }
	return socket!=null;
    }

    /**
     * Reads an image from the camera.
     *
     * @param target Byte array to put data in.
     * @param offset Offset from the start of the byte array.
     *
     * @return The number of read bytes.
     */
    public int getJPEG(byte[] target, int offset) {
	int len = 0;
	int read;
	
	try {
	    outp.write(42);
	    outp.flush();
	    len = inp.read();
	    len = (len<<8)+inp.read();
	    len = (len<<8)+inp.read();
	    len = (len<<8)+inp.read();
	    read = 0;
	    while(len>read) {
		read += inp.read(target,offset+read,len-read);
	    }
	} catch(IOException e) {
	    System.err.println("Could not connect to Axis camera in Axis211A.");
	    System.exit(1);
	}
	return len;
    }

    /**
     * Closes the camera connection.
     */
    public void close() {
	try {
	    socket.close();
	} catch(IOException e) {}
    }
}
