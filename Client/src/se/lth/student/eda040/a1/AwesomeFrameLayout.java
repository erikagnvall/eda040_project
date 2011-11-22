package se.lth.student.eda040.a1;

import se.lth.student.eda040.a1.network.Image;
import se.lth.student.eda040.a1.data.ClientMonitor;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.net.UnknownHostException;
import java.io.IOException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View.OnLongClickListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.app.AlertDialog.Builder;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.Canvas;
import android.view.View.MeasureSpec;
import android.view.Gravity;
import android.view.View;


public class AwesomeFrameLayout extends FrameLayout {
	private ImageView view;
	private TextView overlay;
	private Bitmap disconnectedImage;
	private boolean disconnected;
	private ClientMonitor monitor;
	private byte cameraId;

	private String[] hosts = {
		"argus-1.student.lth.se",
		"argus-2.student.lth.se",
		"argus-3.student.lth.se",
		"argus-4.student.lth.se",
		"argus-5.student.lth.se",
		"argus-6.student.lth.se",
		"argus-7.student.lth.se",
		"argus-8.student.lth.se",
		"argus-9.student.lth.se",
		"argus-10.student.lth.se",
		"10.0.2.2",
	};

	public AwesomeFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d("AwesomeFrameLayout", " In constructor");
	}

	public void onFinishInflate() {
		Log.d("AwesomeFrameLayout", " In onFinishInflate");
		this.view = (ImageView) findViewById(R.id.view);
		this.overlay = (TextView) findViewById(R.id.overlay);
		this.disconnectedImage = BitmapFactory.decodeResource(getResources(), R.drawable.disconnected);

		this.view.setLongClickable(true);
		this.view.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v){
				if(monitor.isConnectedCamera(cameraId)){
					showDisconnectDialog();
				} else {
					showConnectDialog();
				}
				return true;
			}
		});

		view.setImageBitmap(disconnectedImage);
		overlay.setVisibility(View.INVISIBLE);
	}

	private void showConnectDialog() {
		List<String> cameras = new ArrayList<String>(Arrays.asList(hosts));
		cameras.removeAll(monitor.getConnectedHosts());
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, cameras);
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle("Select host");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int item) {
				String host = adapter.getItem(item);
				connectCamera((byte) cameraId, host);
			}
		});
		AlertDialog cameraPicker = builder.create();
		cameraPicker.show();
	}

	private void showDisconnectDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			})
			.setPositiveButton("YES", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id){
					disconnectCamera();
				}
			})
			.setMessage("Do you really want to\ndisconnect this camera?");
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showErrorDialog(String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			})
			.setMessage(msg);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void connectCamera(byte cameraId, String host){
		String errorMsg = null;
		try {
			monitor.connectTo(cameraId, host);
			Log.d("VideoActivity", "Connected to camera: " + cameraId);
		} catch (UnknownHostException e){
			errorMsg = "Failed to connect camera: " + cameraId +
				".\nUnable to connect to host: " + host + ".";
		} catch (IOException e){
			errorMsg = "Failed to connect camera: " + cameraId +
				"\n" + e.getMessage();
		} catch (IllegalArgumentException e){
			errorMsg = "Failed to connect camera: " + cameraId + 
				"Camera has not been set up!";
		}

		if (errorMsg != null) {
			Log.d("VideoActivity", errorMsg);
			showErrorDialog(errorMsg);
		}
	}

	private void disconnectCamera(){
		monitor.gracefullDisconnect(cameraId);
	}

	public void disconnected() {
		view.setImageBitmap(disconnectedImage);
		overlay.setVisibility(View.INVISIBLE);
	}

	public void setMonitor(ClientMonitor monitor){
		this.monitor = monitor;
	}

	public void setCameraId(byte cameraId) {
		this.cameraId = cameraId;
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d("AwesomeFrameLayout", " In onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measureSpecHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.getMode(heightMeasureSpec));
		view.measure(widthMeasureSpec, measureSpecHeight);
		overlay.measure(0, 0);
		overlay.measure(overlay.getMeasuredWidth(), getMeasuredHeight());
	}

	public void drawImage(Image image) {
		if (monitor.isConnectedCamera(cameraId)) {
			view.setImageBitmap(image.toBitmap());
			overlay.setVisibility(View.VISIBLE);
			overlay.setText("Δ " + image.getCurrentDelay() + " ms");
			if (image.isVideoMode()) {
				overlay.append(" / V");
			}
		}
	}

}
