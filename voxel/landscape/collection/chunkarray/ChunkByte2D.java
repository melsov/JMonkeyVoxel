package voxel.landscape.collection.chunkarray;
import voxel.landscape.Chunk;

public class ChunkByte2D extends ChunkPrimitiveType2D
{
	private byte[][] chunk = new byte [Chunk.CHUNKDIMS.x][Chunk.CHUNKDIMS.z]; 
	
	@Override
	public void Set(int val, int x, int z) {
		chunk[z][x] = (byte) val;
	}
	@Override
	public int Get(int x, int z) {
		return chunk[z][x];
	}
	
}