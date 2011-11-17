//import java.util.Arrays;

public class Image {

	/*
	 * ||| byte currentMode | int actualLength | byte[] image |||
	 */
	public static final int OFFSET = 5;
	private byte[] image;
	
	public Image(byte[] buff, int actualLength, byte mode) {
		image = new byte[OFFSET + actualLength];
		image[0] = mode;
		byte b;
		for (int i = 0; i < 4; i++) {
			b = (byte) (actualLength >> (8 * (3-i)) & 0xFF) ;
			//System.out.println(String.format("bits: %02X", b));
			image[i + 1] = b; 
		}
		//System.out.println("actualLength = " + actualLength);
		//System.out.println(Arrays.toString(image));
		System.arraycopy(buff, 0, image, OFFSET, actualLength);
	}
	
	public byte[] toBytes() {
		return image;
	}
}
