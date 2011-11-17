package tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
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

	private void connectAndSendInput(ClientProtocol cp, byte[] input) throws IOException, InterruptedException{
		ServerState state = new ServerState();
		MockServer ms = new MockServer(input, state);
		ms.start();
		state.waitConnected();
		cp.connectTo("127.0.0.1");
	}

	@Test
	public void testGetSimpleImage(){
		byte[] input = new byte[37];
		byte[] expectedImageData = new byte[32];
		input[0] = 0;
		input[1] = 0;
		input[2] = 0;
		input[3] = 0;
		input[4] = (byte)32;
		for(int i = 0; i < input[4]; i++){
			input[i + 5] = (byte)200;
			expectedImageData[i] = (byte)200;
		}

		ClientProtocol cp = new ClientProtocol((byte)0);
		try {
			connectAndSendInput(cp, input);
			Image img = cp.awaitImage();
			assertTrue("Excpetced: " + expectedImageData + " Got: " + img.getImageData(),
					Arrays.equals(expectedImageData, img.getImageData()));
			assertEquals("Incorrect camera ID", 0, img.getCameraId());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Got Exception but shouldnt have");
		} catch (InterruptedException e){
			//
		}
		
	}

	@Test
	public void testImageTooLong(){
		byte[] input = new byte[39];
		byte[] expectedImageData = new byte[32];
		input[0] = 0;
		input[1] = 0;
		input[2] = 0;
		input[3] = 0;
		input[4] = (byte)32;
		for(int i = 0; i < input[4]; i++){
			input[i + 5] = (byte)200;
			expectedImageData[i] = (byte)200;
		}

		ClientProtocol cp = new ClientProtocol((byte)0);
		try {
			connectAndSendInput(cp, input);
			Image img = cp.awaitImage();
			assertTrue("Excpetced: " + expectedImageData + " Got: " + img.getImageData(),
					Arrays.equals(expectedImageData, img.getImageData()));
			assertEquals("Incorrect camera ID", 0, img.getCameraId());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Got Exception but shouldnt have");
		} catch (InterruptedException e){
			//
		}

	}

	@Test
	public void testDoubleImage(){
		byte[] input = new byte[74];
		byte[] expectedImageData_1 = new byte[32];
		byte[] expectedImageData_2 = new byte[32];
		input[0] = 0;
		input[1] = 0;
		input[2] = 0;
		input[3] = 0;
		input[4] = (byte)32;
		input[37] = 0;
		input[38] = 0;
		input[39] = 0;
		input[40] = 0;
		input[41] = (byte)32;
		for(int i = 0; i < input[4]; i++){
			input[i + 5] = (byte)200;
			expectedImageData_1[i] = (byte)200;
		}
		for(int i = 0; i < input[41]; i++){
			input[i + 42] = (byte)200;
			expectedImageData_2[i] = (byte)200;
		}
		ClientProtocol cp = new ClientProtocol((byte)0);
		try {
			connectAndSendInput(cp, input);
			Image img = cp.awaitImage();
			assertTrue("Excpetced: " + expectedImageData_1 + " Got: " + img.getImageData(),
					Arrays.equals(expectedImageData_1, img.getImageData()));
			assertEquals("Incorrect camera ID", 0, img.getCameraId());
			img = cp.awaitImage();
			assertTrue("Excpetced: " + expectedImageData_2 + " Got: " + img.getImageData(),
					Arrays.equals(expectedImageData_2, img.getImageData()));
			assertEquals("Incorrect camera ID", 0, img.getCameraId());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Got Exception but shouldnt have");
		} catch (InterruptedException e){
			//
		}

	}

	@Test
	public void testVideoMode(){
		byte[] input = new byte[37];
		input[0] = 'v';
		input[1] = 0;
		input[2] = 0;
		input[3] = 0;
		input[4] = (byte)32;
		for(int i = 0; i < input[4]; i++){
			input[i + 5] = (byte)200;
		}
		ClientProtocol cp = new ClientProtocol((byte)0);
		try {
			connectAndSendInput(cp, input);
			Image img = cp.awaitImage();
			assertTrue("Expected vieo mode, but wasn't", img.isVideoMode()); 
		} catch (IOException e) {
			e.printStackTrace();
			fail("Got Exception but shouldnt have");
		} catch (InterruptedException e){
			//
		}

	}

	@Test
	public void testNotVideoMode(){
		byte[] input = new byte[37];
		input[0] = 'i';
		input[1] = 0;
		input[2] = 0;
		input[3] = 0;
		input[4] = (byte)32;
		for(int i = 0; i < input[4]; i++){
			input[i + 5] = (byte)200;
		}
		ClientProtocol cp = new ClientProtocol((byte)0);
		try {
			connectAndSendInput(cp, input);
			Image img = cp.awaitImage();
			assertFalse("Expected not video mode, but was.", img.isVideoMode()); 
		} catch (IOException e) {
			e.printStackTrace();
			fail("Got Exception but shouldnt have");
		} catch (InterruptedException e){
			//
		}
	}

	@Test
	public void testLostConnection(){
		byte[] input = {'i', 0x00, 0x00, 0x00, 0x02, 0x0F};
		ClientProtocol cp = new ClientProtocol((byte)0);
		try {
			connectAndSendInput(cp, input);
			Image img = cp.awaitImage();
			fail("Should have throw an IOException but didn't");
		} catch (IOException e) {
			assertEquals("Connection lost", e.getMessage());
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

}
