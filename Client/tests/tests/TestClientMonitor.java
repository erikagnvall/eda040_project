package tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import com.xtremelabs.robolectric.RobolectricTestRunner;

import se.lth.student.eda040.a1.network.Image;
import se.lth.student.eda040.a1.data.ClientMonitor;

public class TestClientMonitor {

	private ClientMonitor cm;
	
	public TestClientMonitor() {
		cm = new ClientMonitor();
	}

	@Test
	public void testSimpleSync(){
		try{
			long currentTime = System.currentTimeMillis();
			byte[] imgData = {(byte)0xFF};
			Image i1 = new Image((byte)0, imgData, currentTime, true);
			Image i2 = new Image((byte)0, imgData, currentTime + 100, true);
			Thread.sleep(100);
			cm.putImage(i1);
			cm.putImage(i2);
			cm.awaitImage();
			long time1 = System.currentTimeMillis();
			cm.awaitImage();
			long time2 = System.currentTimeMillis();
			long diff = Math.abs(time2 - time1 - 100);
			assertTrue("Incorrect display time: " + diff,  diff < 3);
		} catch (InterruptedException e){
			fail("Caught Exception: " + e.getMessage());
		}
	}

	@Test
	public void testManyImagesOneFeed(){
		try{
			long currentTime = System.currentTimeMillis();
			byte[] imgData = {(byte)0xFF};
			Image[] imgs = new Image[10];
			for(int i = 0; i < 10; i++){
				imgs[i] = new Image((byte)0, imgData, currentTime + 10*i, true);
			}
			Thread.sleep(100);
			for(int i = 0; i < 10; i++){
				cm.putImage(imgs[i]);
			}
			long time1;
			long time2;
			cm.awaitImage();
			time1 = System.currentTimeMillis();
			for(int i = 1; i < 10; i++){
				cm.awaitImage();
				time2 = System.currentTimeMillis();
				long diff = Math.abs(time2 - time1 - 10);
				assertTrue("Incorrect display time: " + diff,  diff < 3);
				time1 = time2;
			}
		} catch (InterruptedException e){
			fail("Caught Exception: " + e.getMessage());
		}
	}


}
