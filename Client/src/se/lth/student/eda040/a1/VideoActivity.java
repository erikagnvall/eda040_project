package se.lth.student.eda040.a1;

import se.lth.student.eda040.a1.data.Input;
import se.lth.student.eda040.a1.data.Output;
import se.lth.student.eda040.a1.data.ClientMonitor;
import se.lth.student.eda040.a1.data.ImageFetcher;
import se.lth.student.eda040.a1.data.DisconnectionDetecter;
import se.lth.student.eda040.a1.network.ClientProtocol;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoActivity extends Activity {

	private Input in0;
	private Input in1;
	private Output out0;
	private Output out1;
	private Handler handler;
	private ClientMonitor monitor;
	private ContextMenu contextMenu;

	private AwesomeVideoView avv;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoview);
		avv = (AwesomeVideoView) findViewById(R.id.avv);
		
		// TODO the socket instantiation is blocking. OK for now but if possible do this in another setup-thread.
		// Socket connect has a timeout specified in ClientProtocol, as of
		// writing this it is 5000 ms.
		handler = new Handler();
		monitor = new ClientMonitor();
		DisconnectionDetecter detecter = new DisconnectionDetecter(monitor, avv, handler);
		ImageFetcher fetcher = new ImageFetcher(monitor, avv, handler);
		ClientProtocol protocol0 = new ClientProtocol((byte) 0);
		ClientProtocol protocol1 = new ClientProtocol((byte) 1);
		monitor.addProtocol((byte) 0, protocol0);
		monitor.addProtocol((byte) 1, protocol1);
		in0 = new Input(monitor, protocol0);
		in1 = new Input(monitor, protocol1);
		out0 = new Output(monitor, protocol0);
		out1 = new Output(monitor, protocol1);
		avv.setMonitor(monitor);

		in0.start();
		in1.start();
		out0.start();
		out1.start();
		fetcher.start();
		detecter.start();
	}


	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.videomenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.setIdle:
				monitor.setVideoMode(false);
				break;
		case R.id.setVideo:
				monitor.setVideoMode(true);
				break;
		default:
				super.onOptionsItemSelected(item);
		}
		return true;
	}

	/** Notify the Activity of disconnected camera.
	 * This will simply make the GUI perform apropriate actions when a camera
	 * has disconnected. For example switch the image in an AwesomeFrameView.
	 */
	public void disconnenctedCamera(byte cameraId) {
		avv.disconnectedCamera(cameraId);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		// Note: this overides the icons that otherwise would be used according to the xml.
		menu.clear();	 // Clears all items, below: rebuild from scratch.
		MenuItem setIdle = menu.add(Menu.NONE, R.id.setIdle, 2, "Set Idle");
		MenuItem setVideo = menu.add(Menu.NONE, R.id.setVideo, 2, "Set Video");
		if (monitor.isConnectedCamera((byte)0) || monitor.isConnectedCamera((byte)1)) {
			if (!monitor.isVideoMode()) {
				setIdle.setEnabled(false);
			} else {
				setVideo.setEnabled(false);
			}
		} else {
			setIdle.setEnabled(false);
			setVideo.setEnabled(false);
		}

		return true;
	}
}
