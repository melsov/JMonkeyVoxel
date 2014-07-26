package voxel.landscape.collection.chunkarray;

import voxel.landscape.Chunk;
import voxel.landscape.Coord3;

public class ChunkByte3D extends ChunkPrimitiveType3D 
{
	private volatile byte[][][] chunk = new byte[Chunk.CHUNKDIMS.x][Chunk.CHUNKDIMS.y][Chunk.CHUNKDIMS.z];
	
	@Override
	public void Set(int val, Coord3 pos) {
		Set(val, pos.x, pos.y, pos.z);
	}
	@Override
	public void Set(int val, int x, int y, int z) {
		chunk[z][y][x] = (byte) val;
	}
	@Override
	public int Get(Coord3 pos) {
		return Get(pos.x, pos.y, pos.z);
	}
	@Override
	public int Get(int x, int y, int z) {
		return chunk[z][y][x];
	}
}
