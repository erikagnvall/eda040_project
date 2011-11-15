package se.lth.student.eda040.a1;

import se.lth.student.eda040.a1.data.Input;
import se.lth.student.eda040.a1.data.Output;
import se.lth.student.eda040.a1.data.ClientMonitor;
import se.lth.student.eda040.a1.data.ImageFetcher;
import se.lth.student.eda040.a1.network.ClientProtocol;
import se.lth.student.eda040.a1.network.Command;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.Menu;
import android.util.Log;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;


public class VideoActivity extends Activity {
	private Input in0;
	private Input in1;
	private Output out0;
	private Output out1;
	private Handler handler;
	private ClientMonitor monitor;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoview);
		AwesomeVideoView avv = (AwesomeVideoView) findViewById(R.id.avv);
		
		// TODO the socket instantiation is blocking. OK for now but if possible do this in another setup-thread.
		handler = new Handler();
		monitor = new ClientMonitor();
		ImageFetcher fetcher = new ImageFetcher(monitor, avv, handler);
		ClientProtocol protocol0 = new ClientProtocol((byte) 0);
		ClientProtocol protocol1 = new ClientProtocol((byte) 1);
		monitor.addProtocol((byte) 0, protocol0);
		monitor.addProtocol((byte) 1, protocol1);
		in0 = new Input(monitor, protocol0);
		in1 = new Input(monitor, protocol1);
		out0 = new Output(monitor, protocol0);
		out1 = new Output(monitor, protocol1);

		in0.start();
		in1.start();
		out0.start();
		out1.start();
		fetcher.start();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.videomenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.connectCam0:
				Log.d("VideoActivity", "Selected connectCam0 option.");
				// TODO let user choose camera here.
				monitor.connectTo((byte) 0, "10.0.2.2");
				break;
		case R.id.connectCam1:
				Log.d("VideoActivity", "Selected connectCam0 option.");
				// TODO let user choose camera here.
				// The actual state will not be changed unit StateFetcher notifies about it.
				monitor.connectTo((byte) 1, "10.0.2.2");
				break;
		case R.id.disconnectCam0:
				Log.d("VideoActivity", "Selected disconnectCam0");
				monitor.gracefullDisconnect((byte) 0);	
				break;
		case R.id.disconnectCam1:
				Log.d("VideoActivity", "Selected disconnectCam1");
				monitor.gracefullDisconnect((byte) 1);	
				break;
		default:
				super.onOptionsItemSelected(item);
		}
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
				// TODO this overides the icons that otherwide would be used according to the xml. What to do?
				menu.clear();	 // Clears all items, below: rebuild from scratch.
				if (monitor.isConnectedCamera((byte) 0)) {
					menu.add(0, R.id.disconnectCam0, 0, "Disconnect c0");
				} else {
					menu.add(0, R.id.connectCam0, 0, "Connect c0");
				}

				if (monitor.isConnectedCamera((byte) 1)) {
					menu.add(1, R.id.disconnectCam1, 1, "Disconnect c1");
				} else {
					menu.add(1, R.id.connectCam1, 1, "Connect c1");
				}
		return true;
	}
}
