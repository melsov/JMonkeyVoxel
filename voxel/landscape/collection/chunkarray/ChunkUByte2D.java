package voxel.landscape.collection.chunkarray;

import voxel.landscape.Chunk;
import voxel.landscape.util.Asserter;

public class ChunkUByte2D extends ChunkPrimitiveType2D
{
	private byte[][] chunk = new byte [Chunk.CHUNKDIMS.x][Chunk.CHUNKDIMS.z]; 
	
	@Override
	public void Set(int val, int x, int z) {
		Asserter.assertTrue(val >= 0, "Ubyte takes only positive values");
		Asserter.assertTrue(val < 256, "Ubyte takes only values between 0 and 255 inclusive");
		chunk[z][x] = (byte)val;
	}
	@Override
	public int Get(int x, int z) {
		return chunk[z][x] & 255; //bitwise conversion to positive
	}
	
}
