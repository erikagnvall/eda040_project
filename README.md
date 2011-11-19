#EDA040 camera project
 
Project in the course EDA040 at Lund University.


# How to build and run
	$git clone git://github.com/Meldanya/eda040_project.git
	$wget http://dl.google.com/android/android-sdk_r15-linux.tgz
	$tar xvzf android-sdk_r15-linux.tgz
	$PATH=$PATH:"`pwd`/android-sdk-linux/tools"
	$cd eda040_project

Beware! You have to redo the second last command if you switch shell.
## Server
Server should be started before trying to connect with client....

	$cd Server

### Cross compiled
Make sure you did not execute the commands in `Proxy server` below.

	$cat 'CAMERAPASSWORD' > password.local
	$./compile2c.sh argus-X argus-Y < password.local

Enjoy, ice cold.

### Proxy server
	$vi src/ImageCapture.java

Change to:

	import se.lth.cs.cameraproxy.Axis211A;
	import se.lth.cs.cameraproxy.MotionDetector;
	// import se.lth.cs.camera.Axis211A;
	// import se.lth.cs.camera.MotionDetector;

	// Real and fake.
	// this.camera = new Axis211A(); 
	// this.motionDetector = new MotionDetector();

	// Proxy.
	this.camera = new Axis211A("argus-4", 8080); // Proxycamera.
	this.motionDetector = new MotionDetector("argus-4", 8080); // Proxycamera

Because we're now using modern `Java`, exceptions must be caught.

	$vi src/CameraServer.java

In `run()`, change to:

	try {
		socket.close();
	} catch (IOException e) {
		System.err.println("cold not close connection");
	}	

The files we move in this step is only needed as mock placeholders for
the C-crosscompiler.

	$mv src/se /tmp

Now you can compile the junk:

	$ant

Open a new shell and run the following:

	$telnet argus-4
	telnet> /etc/CameraProxy 8080

Now return to the previous shell. It's time for execution! (not the lethal one):

	$java -cp bin:lib/* CameraServer

## Client
	$cd Client
### Emulator
	$android create avd -n api_10 -t target-10
	$emulator -avd api_10 &

Wait until emulator is started and Android OS i up an running. Then compile, upload and watch the lolcat.

	$ant debug
	$ant installd
	$adb lolcat

If the program should freeze at any point, run the following:

	$ddms &

And find the process `se.lth.student.eda040.a1` in the upper left frame
and press the stop button.

Start the application	
#### Proxy
Choose proxy-camera.
#### Direct connection
Choose a real camera.
	
### Real phone
Connect the phone to your computer.

	$ant debug
	$ant installd
	$adb lolcat

Start the application	
Choose a real camera.

##Contributors
Erik Jansson

Erik Westrup

Oscar Olsson

Tommy Olofsson
