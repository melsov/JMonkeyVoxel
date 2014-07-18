package voxel.landscape.collection;



import voxel.landscape.Chunk;
import voxel.landscape.Coord3;
import voxel.landscape.collection.chunkarray.Chunk2D;

public class Map2D<I> {
	
	private List2D<Chunk2D<I>> chunks; // = new List2D<Chunk2D<I>>();
	private I defaultValue;
	Class<I> type;
	
	public Map2D(Class<I> _type) {
		this(null, _type);
	}
	
	@SuppressWarnings("unchecked")
	public Map2D(I defaultValue, Class<I> _type) {
		type = _type;
		Chunk2D<I> dummy = new Chunk2D<I>(_type, true);
		chunks = new List2D<Chunk2D<I>>((Class<Chunk2D<I>>) dummy.getClass());
		this.defaultValue = defaultValue;
	}
	
	public void Set(I val, int x, int z) {
		Coord3 chunkPos = Chunk.ToChunkPosition(x, 0, z);
		Coord3 localPos = Chunk.toChunkLocalCoord(x, 0, z);
		Chunk2D<I> chunk = GetChunkInstance(chunkPos.x, chunkPos.z);
		chunk.Set(val, localPos.x, localPos.z);
	}
	
	public I Get(int x, int z) {
		Coord3 chunkPos = Chunk.ToChunkPosition(x, 0, z);
		Coord3 localPos = Chunk.toChunkLocalCoord(x, 0, z);
		Chunk2D<I> chunk = GetChunk(chunkPos.x, chunkPos.z);
		if(chunk != null) return chunk.Get(localPos.x, localPos.z);
		return defaultValue;
	}

	public Chunk2D<I> GetChunkInstance(int x, int z) {
		return chunks.GetInstance(x, z);
	}
	public Chunk2D<I> GetChunk(int x, int z) {
		return chunks.SafeGet(x, z);
	}

}

