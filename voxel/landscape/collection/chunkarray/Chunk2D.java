package voxel.landscape.collection.chunkarray;

import java.lang.reflect.Array;

import voxel.landscape.Chunk;

public class Chunk2D<K> 
{
	private K[][] chunk; 
	private Class<K> type;
	
	@SuppressWarnings("unchecked")
	public Chunk2D(Class<K> _type) {
		type = _type;
		chunk = (K[][]) Array.newInstance(type, Chunk.CHUNKDIMS.z, Chunk.CHUNKDIMS.x);
	}
	public Chunk2D(Class<K> _type, boolean noInstantiation) {
		type = _type;
		
	}
	
	public void Set(K val, int x, int z) {
		chunk[z][x] = val;
	}
	
	public K Get(int x, int z) {
		return chunk[z][x];
	}
	
}