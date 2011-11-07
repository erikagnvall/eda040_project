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

	public void testGetSimpleImage(){
		byte[] input = {0x01, 0x00, 0x00, 0x00, 0x01, 0x0F};
		byte[] imageData = {0x0F};
		ClientProtocol cp = new ClientProtocol(new MockSocket(input), 0);
		try {
			Image img = cp.awaitImage();
			assertTrue("Excpetced: " + imageData + " Got: " + img.getImageData(), Arrays.equals(imageData, img.getImageData()));
			assertEquals("Incorrect camera ID", 0, img.getCameraId());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Got Exception but shouldnt have");
		}
	}

	public void testImageTooLong(){
		byte[] input = {0x01, 0x00, 0x00, 0x00, 0x01, 0x0F, 0x01, 0x01, 0x01};
		byte[] imageData = {0x0F};
		ClientProtocol cp = new ClientProtocol(new MockSocket(input), 0);
		try {
			Image img = cp.awaitImage();
			assertTrue("Excpetced: " + imageData + " Got: " + img.getImageData(), Arrays.equals(imageData, img.getImageData()));
			assertEquals("Incorrect camera ID", 0, img.getCameraId());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Got Exception but shouldnt have");
		}
	}

	public void testDoubleImage(){
		byte[] input = {0x01, 0x00, 0x00, 0x00, 0x01, 0x0F, 0x01, 0x00, 0x00, 0x00, 0x02, 0x0F, 0x0F};
		byte[] imageData_1 = {0x0F};
		byte[] imageData_2 = {0x0F, 0x0F};
		ClientProtocol cp = new ClientProtocol(new MockSocket(input), 0);
		try {
			Image img = cp.awaitImage();
			assertTrue("Excpetced: " + imageData_1 + " Got: " + img.getImageData(), Arrays.equals(imageData_1, img.getImageData()));
			assertEquals("Incorrect camera ID", 0, img.getCameraId());
			img = cp.awaitImage();
			assertTrue("Excpetced: " + imageData_2 + " Got: " + img.getImageData(), Arrays.equals(imageData_2, img.getImageData()));
			assertEquals("Incorrect camera ID", 0, img.getCameraId());
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
