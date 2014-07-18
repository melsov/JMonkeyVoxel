package voxel.landscape.collection.chunkarray;

import voxel.landscape.Coord3;

public abstract class ChunkPrimitiveType3D {

	public abstract void Set(int val, Coord3 pos);
	public abstract void Set(int val, int x, int y, int z);
	
	public abstract int Get(Coord3 pos) ;
	public abstract int Get(int x, int y, int z);
}
