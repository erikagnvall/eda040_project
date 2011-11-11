package se.lth.student.eda040.a1;

//import se.lth.student.eda040.a1.network.Image;
import se.lth.student.eda040.a1.data.Input;
import se.lth.student.eda040.a1.data.Output;
import se.lth.student.eda040.a1.data.ClientMonitor;
import se.lth.student.eda040.a1.network.ClientProtocol;

import android.app.Activity;
import android.os.Bundle;


public class VideoActivity extends Activity {
	private Input in0;
	private Input in1;
	private Output out0;
	private Output out1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoview);
		//AwesomeVideoView avv = (AwesomeVideoView) findViewById(R.id.avv);
		
		// TODO the socket instantiation is blocking. OK for now but if possible do this in another setup-thread.
		ClientMonitor monitor = new ClientMonitor();
		ClientProtocol protocol0 = new ClientProtocol((byte) 0);
		ClientProtocol protocol1 = new ClientProtocol((byte) 1);
		in0 = new Input(monitor, protocol0);
		in1 = new Input(monitor, protocol1);
		out0 = new Output(monitor, protocol0);
		out1 = new Output(monitor, protocol1);

		in0.start();
		in1.start();
		out0.start();
		out1.start();
	}

}
