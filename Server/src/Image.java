
public class Image {
	/*
	 * ||| byte currentMode | int picLen | byte[] picData |||
	 */
	public static final int OFFSET = 5;
	private byte[] pic;
	private int actualLength;
	
	public Image(byte[] buff, int actualLength) {
		this.actualLength = actualLength;
		System.arraycopy(buff, 0, pic, OFFSET, actualLength);
	}
	
	public byte[] toBytes() {
		byte[] msg = new byte[OFFSET + actualLength];
		return msg;
	}
}
