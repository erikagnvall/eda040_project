/*
 * Real-time and concurrent programming
 *
 * MH 111026 Created
 */

package se.lth.cs.fakecamera;

import java.util.*;

/**
 * A motion detector substitute. Motion detection on a recording from the media jar file.
 * Should be singleton.
 */

public class MotionDetector {
	public MotionDetector() {
	}
	
	public MotionDetector(String host, int port) {
	}
	
	public int getLevel() {
		int i = lastImageIndex;
		return 86<=i && i<=240 ? 80 : 0;
	}
	
	public int getThreshold() {
		return 15;
	}
	
	public boolean detect() {
		return getLevel() > getThreshold();
	}
	
	protected static int lastImageIndex = 0;
}
