package voxel.landscape.collection.chunkarray;

import java.lang.reflect.Array;

import voxel.landscape.Chunk;
import voxel.landscape.Coord3;


@SuppressWarnings("hiding")
public class Chunk3D<I> {
	
	private I[][][] chunk;
	private Class<I> type;
	
	@SuppressWarnings("unchecked")
	public Chunk3D(Class<I> _type) {
		type = _type;
		Coord3 size = Chunk.CHUNKDIMS;
		chunk = (I[][][]) Array.newInstance(type, size.x, size.y, size.z);
	}
	public Chunk3D(Class<I> _type, boolean NoArray) {
		type = _type;
	}
	
	public void Set(I val, Coord3 pos) {
		Set(val, pos.x, pos.y, pos.z);
	}
	public void Set(I val, int x, int y, int z) {
		chunk[z][y][x] = val;
	}
	
	public I Get(Coord3 pos) {
		return Get(pos.x, pos.y, pos.z);
	}
	public I Get(int x, int y, int z) {
		return chunk[z][y][x];
	}
}