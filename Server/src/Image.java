
public class Image {
	public static final int offset = 1;
	private byte[] msg;
	
	public Image(byte[] jpg) {
		System.arraycopy(jpg, 0, msg, offset, jpg.length);
	}
	
	public byte[] toBytes() {
		return msg;
	}
}
