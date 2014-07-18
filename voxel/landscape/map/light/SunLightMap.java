package voxel.landscape.map.light;

import java.awt.Color;

//import voxel.landscape.BlockType;
import voxel.landscape.Chunk;
import voxel.landscape.Coord3;
import voxel.landscape.collection.MapByte3D;
import voxel.landscape.collection.MapPrimitive2D;
import voxel.landscape.collection.chunkarray.ChunkByte3D;
import voxel.landscape.collection.chunkarray.ChunkUByte2D;
import voxel.landscape.map.debug.Array2DViewer;



public class SunLightMap 
{	
	private MapPrimitive2D<ChunkUByte2D> rays = new MapPrimitive2D<ChunkUByte2D>((byte) 0, ChunkUByte2D.class);
	private MapByte3D lights = new MapByte3D((byte) 0);
	
	public void SetSunHeight(int height, int x, int z) {
//		debugWithArrayMap((byte)15, x, height, z); 
		rays.Set(height, x, z);
	}
	public int GetSunHeight(int x, int z) {
		return rays.Get(x, z);
	}
	public boolean IsSunLight(int x, int y, int z) {
		return GetSunHeight(x, z) <= y;
	}
	private boolean IsSunLight(Coord3 chunkPos, Coord3 localPos, int worldY) {
		ChunkUByte2D chunk = (ChunkUByte2D) rays.GetChunk(chunkPos.x, chunkPos.z);
		return chunk != null && chunk.Get(localPos.x, localPos.z) <= worldY;
	}
	public boolean SetMaxLight(byte light, Coord3 pos) {
		return SetMaxLight(light, pos.x, pos.y, pos.z);
	}
	public boolean SetMaxLight(byte light, int x, int y, int z) {
		Coord3 chunkPos = Chunk.ToChunkPosition(x, y, z);
		Coord3 localPos = Chunk.toChunkLocalCoord(x, y, z);
		
		if( IsSunLight(chunkPos, localPos, y) ) return false;
		
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
		debugWithArrayMap(light, x, y, z);
		lights.Set(light, x, y, z);
	}
	/*
	 * DEBUG!!
	 */
	private void debugWithArrayMap(byte light, int x, int y, int z) {
		Color color = new Color(Math.abs(z/164f), Math.abs(z/164f), .2f);
		Array2DViewer.getInstance().setPixel(x, y, color);
	}
	
	
	public void SetLight(byte light, Coord3 chunkPos, Coord3 localPos) {
		lights.GetChunkInstance(chunkPos).Set(light, localPos);
	}
	
	public byte GetLight(Coord3 pos) {
		return GetLight(pos.x, pos.y, pos.z);
	}
	public byte GetLight(int x, int y, int z) {
		Coord3 chunkPos = Chunk.ToChunkPosition(x, y, z);
		Coord3 localPos = Chunk.toChunkLocalCoord(x, y, z);
		return GetLight(chunkPos, localPos, y);
	}
	public byte GetLight(Coord3 chunkPos, Coord3 localPos, int worldY) {
		if(IsSunLight(chunkPos, localPos, worldY)) return SunLightComputer.MAX_LIGHT;
		
		ChunkByte3D chunk = lights.GetChunk(chunkPos);
		if(chunk != null) {
			byte light = (byte) chunk.Get(localPos);
			return (byte) Math.max(SunLightComputer.MIN_LIGHT, light);
		}
		return SunLightComputer.MIN_LIGHT;
	}
	
}
