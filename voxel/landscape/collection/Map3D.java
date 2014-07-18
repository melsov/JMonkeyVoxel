package voxel.landscape.collection;

import java.lang.reflect.Array;

import voxel.landscape.Chunk;
import voxel.landscape.Coord3;
import voxel.landscape.collection.chunkarray.Chunk3D;

public class Map3D<I> {
	
	private List3D<Chunk3D<I>> chunks; 
	private I defaultValue;
	private Class<I> type;
	
	public Map3D(Class<I> _type) {
		this(null, _type);
	}
	
	@SuppressWarnings("unchecked")
	public Map3D(I defaultValue, Class<I> _type) {
		type = _type;
		this.defaultValue = defaultValue;
		@SuppressWarnings({ "rawtypes" })
		Chunk3D dummy = new Chunk3D(type, true);
		chunks = new List3D<Chunk3D<I>>((Class<Chunk3D<I>>) dummy.getClass());
	}
	
	public void Set(I val, Coord3 pos) {
		Set(val, pos.x, pos.y, pos.z);
	}
	public void Set(I val, int x, int y, int z) {
		Coord3 chunkPos = Chunk.ToChunkPosition(x, y, z);
		Coord3 localPos = Chunk.toChunkLocalCoord(x, y, z);
		Chunk3D<I> chunk = GetChunkInstance(chunkPos);
		chunk.Set(val, localPos);
	}
	
	public I Get(Coord3 pos) {
		return Get(pos.x, pos.y, pos.z);
	}
	public I Get(int x, int y, int z) {
		Coord3 chunkPos = Chunk.ToChunkPosition(x, y, z);
		Coord3 localPos = Chunk.toChunkLocalCoord(x, y, z); 
		Chunk3D<I> chunk = GetChunk(chunkPos);
		if(chunk != null) return chunk.Get(localPos);
		return defaultValue;
	}
	
	public Chunk3D<I> GetChunkInstance(Coord3 pos) {
		return chunks.GetInstance(pos);
	}
	public Chunk3D<I> GetChunkInstance(int x, int y, int z) {
		return chunks.GetInstance(x, y, z);
	}
	
	public Chunk3D<I> GetChunk(Coord3 pos) {
		return chunks.SafeGet(pos);
	}
	public Chunk3D<I> GetChunk(int x, int y, int z) {
		return chunks.SafeGet(x, y, z);
	}
	

}