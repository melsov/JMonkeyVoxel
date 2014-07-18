package voxel.landscape.map.light;

import voxel.landscape.Chunk;
import voxel.landscape.Coord3;
import voxel.landscape.collection.MapByte3D;
import voxel.landscape.collection.chunkarray.ChunkByte3D;

public class LightMap {
	private MapByte3D lights = new MapByte3D();
	
	public boolean SetMaxLight(byte light, Coord3 pos) {
		return SetMaxLight(light, pos.x, pos.y, pos.z);
	}
	public boolean SetMaxLight(byte light, int x, int y, int z) {
		Coord3 chunkPos = Chunk.ToChunkPosition(x, y, z);
		Coord3 localPos = Chunk.toChunkLocalCoord(x, y, z); 
		ChunkByte3D chunk = lights.GetChunkInstance(chunkPos);
		byte oldLight = (byte) chunk.Get(localPos);
		if(oldLight < light) {
			chunk.Set(light, localPos);
			return true;
		}
		return false;
	}
	
	public void SetLight(byte light, Coord3 pos) {
		SetLight(light, pos.x, pos.y, pos.z);
	}
	public void SetLight(byte light, int x, int y, int z) {
		lights.Set(light, x, y, z);
	}
	
	public byte GetLight(Coord3 pos) {
		return GetLight(pos.x, pos.y, pos.z);
	}
	public byte GetLight(int x, int y, int z) {
		byte light = (byte) lights.Get(x, y, z);
		if(light < LightComputer.MIN_LIGHT) return LightComputer.MIN_LIGHT;
		return light;
	}
	public byte GetLight(Coord3 chunkPos, Coord3 localPos) {
		byte light = (byte) lights.GetChunkInstance(chunkPos).Get(localPos);
		if(light < LightComputer.MIN_LIGHT) return LightComputer.MIN_LIGHT;
		return light;
	}
	
}
