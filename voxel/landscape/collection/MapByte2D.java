package voxel.landscape.collection;

import voxel.landscape.Chunk;
import voxel.landscape.Coord3;
import voxel.landscape.collection.chunkarray.ChunkByte2D;

public class MapByte2D 
{
	private List2D<ChunkByte2D> chunks; 
	private byte defaultValue;
	
	public MapByte2D() {
		this((byte) 0);
	}
	
	public MapByte2D(byte defaultValue) {
		chunks = new List2D<ChunkByte2D>(ChunkByte2D.class);
		this.defaultValue = defaultValue;
	}
	
	public void Set(byte val, int x, int z) {
		Coord3 chunkPos = Chunk.ToChunkPosition(x, 0, z);
		Coord3 localPos = Chunk.toChunkLocalCoord(x, 0, z);
		ChunkByte2D chunk = GetChunkInstance(chunkPos.x, chunkPos.z);
		chunk.Set(val, localPos.x, localPos.z);
	}
	
	public int Get(int x, int z) {
		Coord3 chunkPos = Chunk.ToChunkPosition(x, 0, z);
		Coord3 localPos = Chunk.toChunkLocalCoord(x, 0, z);
		ChunkByte2D chunk = GetChunk(chunkPos.x, chunkPos.z);
		if(chunk != null) return chunk.Get(localPos.x, localPos.z);
		return defaultValue;
	}

	public ChunkByte2D GetChunkInstance(int x, int z) {
		return chunks.GetInstance(x, z);
	}
	public ChunkByte2D GetChunk(int x, int z) {
		return chunks.SafeGet(x, z);
	}
}
