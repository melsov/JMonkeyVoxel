package voxel.landscape.collection.chunkarray;

import static org.junit.Assert.*;

import org.junit.Test;


public class ChunkByte3DTest {

	@Test
	public void ubyteInt() {
		double eps = 0f;
		for (int i=0; i<256; ++i) {
			byte b = (byte) i;
			assertEquals(i, fromByte(b), eps);
		}
	}
	
	private static int fromByte(byte b) {
		return b & 255;
	}
	
	private static String eightCharsOf(byte b) {
		char[] res = new char[8];
		byte mask = 1;
		for(int i = 7; i >= 0; --i) {
			char c = '0';
			if ((b & mask) == 1) c = '1';
			res[i] = c;
			b = (byte) (b >> 1);
		}
		return new String(res);
	}
	private static String eightCharsOf(int b) {
		char[] res = new char[8];
		byte mask = 1;
		for(int i = 7; i >= 0; --i) {
			char c = '0';
			if ((b & mask) == 1) c = '1';
			res[i] = c;
			b =  (b >> 1);
		}
		return new String(res);
	}
}
