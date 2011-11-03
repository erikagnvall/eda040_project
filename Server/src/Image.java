
public class Image {
	/*
	 * ||| byte currentMode | int actualLength | byte[] picData |||
	 */
	public static final int OFFSET = 5;
	private byte[] pic;
	
	public Image(byte[] buff, int actualLength, byte mode) {
		pic = new byte[OFFSET + actualLength];
		pic[0] = mode;
		for (int i = 0; i < 4; i++) {
			pic[i + 1] = (byte) (actualLength >> 8*(3-i));
		}
		System.arraycopy(buff, 0, pic, OFFSET, actualLength);
	}
	
	public byte[] toBytes() {
		return pic;
	}
}
