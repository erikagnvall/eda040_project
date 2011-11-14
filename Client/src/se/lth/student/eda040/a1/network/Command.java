package se.lth.student.eda040.a1.network;

public class Command {
	public static final byte MODE_IDLE = 'i';
	public static final byte MODE_VIDEO = 'v';
	public static final byte DISCONNECT = 'd';

	private byte command;
	private byte cameraId;
	private ClientProtocol protocol;

	public Command(byte command, byte cameraId, ClientProtocol protocol) {
		this.command = command;
		this.cameraId = cameraId;
		this.protocol = protocol;
	}

	public byte getCommand() {
		return command;
	}

	/**
	 * Method to be run after command is sent to camera server.
	 */
	public void doPost() {
		switch(command) {
			case Command.DISCONNECT:
				protocol.gracefullDisconnect();
				break;
		}
	}
}

