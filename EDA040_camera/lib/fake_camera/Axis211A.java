/*
 * Real-time and concurrent programming
 *
 * MH 041025 Created
 * MH 111026 Modified
 */

package se.lth.cs.fakecamera;

import java.io.*;
import java.util.*;


/**
 * A camera substitute. Plays back a recording from the media jar file.
 * Is singleton.
 */

public class Axis211A {
	public static final int IMAGE_BUFFER_SIZE = 128*1024;
	
    // ---------------------------------------------------- PUBLIC CONSTRUCTORS

    public Axis211A() {
    	init();
    }
    
    public Axis211A(String host, int port) {
    	init();
    }

    // --------------------------------------------------------- PUBLIC METHODS
    
    public boolean connect() {
    	return true;
    }
    
    public int getJPEG(byte[] data, int offset) {
    	if (data.length < IMAGE_BUFFER_SIZE)
    		throw new Error("Length of parameter one is too small");
    	byte[] image = fakes.get(imageIndex());
    	System.arraycopy(image, 0, data, offset, image.length);
    	timestamp(data, offset);
    	return image.length;
    }
    
    public void close() {
    }

    // -------------------------------------------------------- PROTECTED METHODS

    protected void init() {
    	if (instantiated)
    		throw new Error("Axis211A already instantiated, must be singleton");
    	readJpegs();
    	instantiated = true;
    }

    
    /**
     * Reads images from the media jar file into the fakes
     * Vector. Returns image max size + 1024
     *
     */
    protected void readJpegs() {
		// Read individual images from the jar file
		for (int i=1; i<=MAX; i++) {
			byte[] jpeg = readJarFile(mediaName(i));
			fakes.addElement(jpeg);
		}
    }
    
    
    /**
     * Media filename
     */
    protected String mediaName(int n) {
		StringBuffer sb = new StringBuffer();
		sb.append("/media/film");
		if (n<10) sb.append("0");
		if (n<100) sb.append("0");
		sb.append(Integer.toString(n));
		sb.append(".jpg");
    	return sb.toString();
    }
    
    
    /**
     * Reads an image from the jar file. If no image is found
     * an error is thrown.
     */
	protected byte[] readJarFile(String filename) {
		final int BUF_SIZE = 1024;
		try {
	    	InputStream stream = this.getClass().getResourceAsStream(filename);
	    	
	    	// File size
	    	int fileSize = 0;
	    	byte[] part = new byte[BUF_SIZE];
	    	int partLen = 0;
	    	while ((partLen = stream.read(part, 0, BUF_SIZE)) != -1) {
	    		fileSize += partLen;
	    	}
	    	stream.close();
	    	
	    	// Read file
	    	stream = this.getClass().getResourceAsStream(filename);
	    	byte[] buf = new byte[fileSize];
	    	int len = 0;
	    	do {
	    		len += stream.read(buf, len, fileSize-len);
	    	} while (len != fileSize);
	    	stream.close();
	    	
	    	return buf;
		} catch (IOException e) {
			throw new Error("Error loading " + filename);
		}
	}
	
	
	/**
	 * Return image index based on system time. Endless loop.
	 * Blocks until the image is "taken".
	 * 
	 */
	protected int imageIndex() {
		long stime = System.currentTimeMillis();
		
		// Millis between images
		float period = 1.0f / rate * 1000;
		
		// Total amount of millis per loop
		long loop = (long)(MAX * period);
		
		// Loop time
		long time = stime % loop;
		
		// Image index
		int index = (int)Math.floor(time / period);
		
		// Wait time
		long wtime = (long)(time - index*period);
		wtime = wtime < 0 ? wtime + loop : wtime;
		
		try {
			Thread.sleep(wtime);
		} catch (InterruptedException e) {
			throw new Error("Interrupted!");
		}
		
		MotionDetector.lastImageIndex = index;
		return index;
	}
	
	
	/**
	 * Timestamp image
	 * 
	 */
	protected void timestamp(byte[] image, int offset) {
		long stime = System.currentTimeMillis();
		
		long seconds = stime / 1000;
		long hundredths = (stime - seconds*1000) / 10;

		image[25+offset] = (byte)((seconds & 0xff000000) >> 24);
		image[26+offset] = (byte)((seconds & 0x00ff0000) >> 16);
		image[27+offset] = (byte)((seconds & 0x0000ff00) >> 8);
		image[28+offset] = (byte)(seconds & 0x000000ff);
		image[29+offset] = (byte)(hundredths & 0xff);
	}
	
	
	protected static final int MAX = 247; 	// Number of images
	protected static float rate = 12; 		// Movie rate, Hz
	protected static Vector<byte[]> fakes = new Vector<byte[]>();
	protected static boolean instantiated = false;
}
