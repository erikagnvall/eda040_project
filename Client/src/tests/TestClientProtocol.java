package tests;

import junit.framework.TestCase;
import java.net.Socket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import se.lth.student.eda040.a1.network.*;
import java.util.Arrays;

public class TestClientProtocol extends TestCase {

	public TestClientProtocol(){
	}

	public void testGetSimpleImaga(){
		byte[] input = {0x01, 0x00, 0x00, 0x00, 0x01, 0x0F};
		byte[] imageData = {0x0F};
		ClientProtocol cp = new ClientProtocol(new MockSocket(input), 0);
		try {
			Image img = cp.awaitImage();
			assertTrue("Excpetced: " + imageData + " Got: " + img.getImageData(), Arrays.equals(imageData, img.getImageData()));
			assertEquals("Incorrect camera ID", 0, img.getCameraId());
			assertTrue("Incorrect mode", img.isVideoMode());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Got Exception but shouldnt have");
		}
	}

	private class MockSocket extends Socket {
		private java.io.InputStream in;
		private java.io.OutputStream out;

		public MockSocket(byte[] in){
			this.in = new ByteArrayInputStream(in);
			this.out = new ByteArrayOutputStream();
		}

		public InputStream getInputStream(){
			return in;
		}

		public OutputStream getOutputStream(){
			return out;
		}
	}

}
