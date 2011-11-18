#EDA040 camera project
 
Project in the course EDA040 at Lund University.


# How to build
	$git clone git://github.com/Meldanya/eda040_project.git
	$wget http://dl.google.com/android/android-sdk_r15-linux.tgz
	$tar xvzf android-sdk_r15-linux.tgz
	$PATH=$PATH:"`pwd`/android-sdk-linux/tools"
	$cd eda040_project

Be aware, you have to redo the second last command if you switch shell.
## Server
Server should be started before trying to connect with client....

	$cd Server

### Cross compiled
Make sure you did not execute the above changes.

	$cat 'CAMERAPASSWORD' > password.local
	$./compile2c.sh argus-X argus-Y < password.local

Enjoy

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

Because we're now using modern Java, exceptions must be caugt.

	$vi src/CameraServer.java

In run(), change to:

	try {
		socket.close();
	} catch (IOException e) {
		System.err.println("cold not close connection");
	}	

Now you can compile it:

	$ant
	$java -cp bin:lib/* CameraServer


## Client
	$cd Client
### Emulator
	$android create avd -n api_10 -t target-10
	$emulator -avd api_10 &
	wait until emulator is started.
	$ant debug
	$ant installd
	$adb lolcat

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

