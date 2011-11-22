package se.lth.student.eda040.a1;

import se.lth.student.eda040.a1.data.Input;
import se.lth.student.eda040.a1.data.Output;
import se.lth.student.eda040.a1.data.ClientMonitor;
import se.lth.student.eda040.a1.data.ImageFetcher;
import se.lth.student.eda040.a1.data.DisconnectionDetecter;
import se.lth.student.eda040.a1.network.ClientProtocol;
import se.lth.student.eda040.a1.network.Command;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.UnknownHostException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class VideoActivity extends Activity {

	private Input in0;
	private Input in1;
	private Output out0;
	private Output out1;
	private Handler handler;
	private ClientMonitor monitor;
	private ContextMenu contextMenu;

	private ArrayAdapter<String> adapter;
	private AlertDialog cameraPicker;
	private AwesomeVideoView avv;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoview);
		avv = (AwesomeVideoView) findViewById(R.id.avv);
		avv.setVideoActivity(this);
		TextView infoText = (TextView) findViewById(R.id.infotext);
		
		// TODO the socket instantiation is blocking. OK for now but if possible do this in another setup-thread.
		handler = new Handler();
		monitor = new ClientMonitor();
		DisconnectionDetecter detecter = new DisconnectionDetecter(monitor, avv, handler);
		ImageFetcher fetcher = new ImageFetcher(monitor, avv, handler, infoText);
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
		case R.id.disconnectCam0:
				Log.d("VideoActivity", "Selected disconnectCam0");
				//disconnectCamera((byte) 0);
				//avv.disconnect((byte) 0);
				break;
		case R.id.disconnectCam1:
				Log.d("VideoActivity", "Selected disconnectCam1");
				//disconnectCamera((byte) 1);
				//avv.disconnect((byte) 1);
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


	/**
	 * Disconnect a camera as well as some magic with the camera list.
	 */
	//public void disconnectCamera(byte cameraId) {
	//	monitor.gracefullDisconnect((byte) cameraId);
	//}

	public void disconnenctedCamera(byte cameraId) {
		avv.disconnectedCamera(cameraId);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
				// Note: this overides the icons that otherwise would be used according to the xml.
				menu.clear();	 // Clears all items, below: rebuild from scratch.
				if (monitor.isConnectedCamera((byte) 0)) {
					menu.add(Menu.NONE, R.id.disconnectCam0, 0, "Disconnect c0");
				} else {
					menu.add(Menu.NONE, R.id.connectCam0, 0, "Connect c0");
				}

				if (monitor.isConnectedCamera((byte) 1)) {
					menu.add(Menu.NONE, R.id.disconnectCam1, 1, "Disconnect c1");
				} else {
					menu.add(Menu.NONE, R.id.connectCam1, 1, "Connect c1");
				}
				menu.add(Menu.NONE, R.id.setIdle, 2, "Set Idle");
				menu.add(Menu.NONE, R.id.setVideo, 2, "Set Video");
		return true;
	}
}
