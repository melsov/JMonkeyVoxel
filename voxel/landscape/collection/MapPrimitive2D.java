package voxel.landscape.collection;


import voxel.landscape.Chunk;
import voxel.landscape.Coord3;
import voxel.landscape.collection.chunkarray.ChunkPrimitiveType2D;

public class MapPrimitive2D<T extends ChunkPrimitiveType2D> 
{
	private volatile List2D<T> chunks; 
	private byte defaultValue;
	private Class<T> type;

	public MapPrimitive2D(byte defaultValue, Class<T> _type) {
		type = _type;
		chunks = new List2D<T>(type);
		this.defaultValue = defaultValue;
	}
	
	public void Set(int val, int x, int z) {
		Coord3 chunkPos = Chunk.ToChunkPosition(x, 0, z);
		Coord3 localPos = Chunk.toChunkLocalCoord(x, 0, z);
		T chunk = GetChunkInstance(chunkPos.x, chunkPos.z);
		chunk.Set(val, localPos.x, localPos.z);
	}
	
	public int Get(int x, int z) {
		Coord3 chunkPos = Chunk.ToChunkPosition(x, 0, z);
		Coord3 localPos = Chunk.toChunkLocalCoord(x, 0, z);
		T chunk = GetChunk(chunkPos.x, chunkPos.z);
		if(chunk != null) return chunk.Get(localPos.x, localPos.z);
		return defaultValue;
	}

	public T GetChunkInstance(int x, int z) {
		return chunks.GetInstance(x, z);
	}
	public T GetChunk(int x, int z) {
		return chunks.SafeGet(x, z);
	}
}

