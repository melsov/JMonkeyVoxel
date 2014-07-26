package voxel.landscape.collection;

import voxel.landscape.Chunk;
import voxel.landscape.Coord3;
import voxel.landscape.collection.chunkarray.ChunkByte3D;

public class MapByte3D 
{
	private volatile List3D<ChunkByte3D> chunks; 
	private byte defaultValue;
	
	public MapByte3D() {
		this((byte) 0);
	}
	
	public MapByte3D(byte defaultValue) {
		this.defaultValue = defaultValue;
		chunks = new List3D<ChunkByte3D>(ChunkByte3D.class);
	}
	
	public void Set(byte val, Coord3 pos) {
		Set(val, pos.x, pos.y, pos.z);
	}
	public void Set(byte val, int x, int y, int z) {
		Coord3 chunkPos = Chunk.ToChunkPosition(x, y, z);
		Coord3 localPos = Chunk.toChunkLocalCoord(x, y, z);
		ChunkByte3D chunk = GetChunkInstance(chunkPos);
		chunk.Set(val, localPos);
	}
	
	public int Get(Coord3 pos) {
		return Get(pos.x, pos.y, pos.z);
	}
	public int Get(int x, int y, int z) {
		Coord3 chunkPos = Chunk.ToChunkPosition(x, y, z);
		Coord3 localPos = Chunk.toChunkLocalCoord(x, y, z); 
		ChunkByte3D chunk = GetChunk(chunkPos);
		if(chunk != null) return chunk.Get(localPos);
		return defaultValue;
	}
	
	public ChunkByte3D GetChunkInstance(Coord3 pos) {
		return chunks.GetInstance(pos);
	}
	public ChunkByte3D GetChunkInstance(int x, int y, int z) {
		return chunks.GetInstance(x, y, z);
	}
	
	public ChunkByte3D GetChunk(Coord3 pos) {
		return chunks.SafeGet(pos);
	}
	public ChunkByte3D GetChunk(int x, int y, int z) {
		return chunks.SafeGet(x, y, z);
	}
	
}
