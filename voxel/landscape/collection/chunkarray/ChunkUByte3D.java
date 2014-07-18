package voxel.landscape.collection.chunkarray;

import voxel.landscape.Chunk;
import voxel.landscape.Coord3;

public class ChunkUByte3D extends ChunkPrimitiveType3D
{

	private byte[][][] chunk = new byte[Chunk.CHUNKDIMS.x][Chunk.CHUNKDIMS.y][Chunk.CHUNKDIMS.z];
	
	public void Set(int val, Coord3 pos) {
		Set(val, pos.x, pos.y, pos.z);
	}
	public void Set(int val, int x, int y, int z) {
		chunk[z][y][x] = (byte) val;
	}
	
	public int Get(Coord3 pos) {
		return Get(pos.x, pos.y, pos.z);
	}
	public int Get(int x, int y, int z) {
		return chunk[z][y][x] & 255; //bitwise conversion to positive 'ubyte range' int
	}
}
