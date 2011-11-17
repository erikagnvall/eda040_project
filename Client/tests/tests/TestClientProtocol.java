package tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import com.xtremelabs.robolectric.RobolectricTestRunner;

import java.net.Socket;
import java.net.ServerSocket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import se.lth.student.eda040.a1.network.*;
import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class TestClientProtocol {

	public TestClientProtocol(){
	}

	@Test
	public void testGetSimpleImage(){
		byte[] input = {0x01, 0x00, 0x00, 0x00, 0x20, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F};
		byte[] imageData = {0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F};
		ClientProtocol cp = new ClientProtocol((byte)0);
		ServerState state = new ServerState();
		MockServer ms = new MockServer(input, state);
		ms.start();
		try {
			state.waitConnected();
			cp.connectTo("127.0.0.1");
			Image img = cp.awaitImage();
			assertTrue("Excpetced: " + imageData + " Got: " + img.getImageData(), Arrays.equals(imageData, img.getImageData()));
			assertEquals("Incorrect camera ID", 0, img.getCameraId());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Got Exception but shouldnt have");
		} catch (InterruptedException e){
			//
		}
		
	}

	private class ServerState {
		private boolean connected;

		public ServerState(){
			connected = false;
		}

		public synchronized void setState(boolean con){
			this.connected = con;
			notifyAll();
		}

		public synchronized void waitConnected() throws InterruptedException{
			while(!connected)
				wait();
		}
	}

	private class MockServer extends Thread {

		private byte[] input;
		public ServerState state;
		
		public MockServer(byte[] input, ServerState state){
			this.input = input;
			this.state = state;
		}

		public void run(){
			ServerSocket ss;
			Socket s;
			try {
				ss = new ServerSocket(8080);
				state.setState(true);
				s = ss.accept();
				s.getOutputStream().write(input);
				s.getOutputStream().flush();
				s.close();
				ss.close();
			} catch (IOException e){
				System.err.print(e.getMessage());
			}
		}
	}

	/**
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

	public void testVideoMode(){
		byte[] input = {'v', 0x00, 0x00, 0x00, 0x01, 0x0F};
		ClientProtocol cp = new ClientProtocol(new MockSocket(input), 0);
		try {
			Image img = cp.awaitImage();
			assertTrue("Expected vieo mode, but wasn't", img.isVideoMode()); 
		} catch (IOException e) {
			e.printStackTrace();
			fail("Got Exception but shouldnt have");
		}
	}

	public void testNotVideoMode(){
		byte[] input = {'i', 0x00, 0x00, 0x00, 0x01, 0x0F};
		ClientProtocol cp = new ClientProtocol(new MockSocket(input), 0);
		try {
			Image img = cp.awaitImage();
			assertFalse("Expected vieo mode, but wasn't", img.isVideoMode()); 
		} catch (IOException e) {
			e.printStackTrace();
			fail("Got Exception but shouldnt have");
		}
	}

	public void testLostConnection(){
		byte[] input = {'i', 0x00, 0x00, 0x00, 0x02, 0x0F};
		ClientProtocol cp = new ClientProtocol(new MockSocket(input), 0);
		try {
			Image img = cp.awaitImage();
			fail("Should have throw an IOException but didn't");
		} catch (IOException e) {
			assertEquals("Connection lost", e.getMessage());
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
	*/

}
