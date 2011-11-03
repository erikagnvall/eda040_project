package se.lth.student.eda040.a1.network;

public class Command {
	public static byte MODE_IDLE = 0;
	public static byte MODE_VIDEO = 1;
	public static byte DISCONNECT = 2;

	private byte command;
	public Command(byte command) {
		this.command = command;
	}

	public byte getCommand() {
		return command;
	}
}

