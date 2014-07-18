package voxel.landscape.map.light;

import voxel.landscape.Chunk;
import voxel.landscape.Coord3;
import voxel.landscape.BlockType;
import voxel.landscape.map.TerrainMap;

class LightComputerUtils {

	
	public static void SetLightDirty(TerrainMap map, Coord3 pos) {
		Coord3 chunkPos = Chunk.ToChunkPosition(pos);
		Coord3 localPos = Chunk.toChunkLocalCoord(pos);
		
		SetChunkLightDirty(map, chunkPos);
		
		if(localPos.x == 0) SetChunkLightDirty(map, chunkPos.minus(Coord3.right));
		if(localPos.y == 0) SetChunkLightDirty(map, chunkPos.minus(Coord3.up));
		if(localPos.z == 0) SetChunkLightDirty(map, chunkPos.minus(Coord3.forward));
		
		if(localPos.x == Chunk.CHUNKDIMS.x-1) SetChunkLightDirty(map, chunkPos.add (Coord3.right));
		if(localPos.y == Chunk.CHUNKDIMS.y-1) SetChunkLightDirty(map, chunkPos.add (Coord3.up));
		if(localPos.z == Chunk.CHUNKDIMS.z-1) SetChunkLightDirty(map, chunkPos.add (Coord3.forward));
	}
	
	private static void SetChunkLightDirty(TerrainMap map, Coord3 chunkPos) {
		Chunk chunkData = map.GetChunk(chunkPos);
		if(chunkData == null) return;
//		ChunkRenderer chunk = chunkData.GetChunkRenderer();
//		if(chunk == null) return;
		chunkData.SetLightDirty();
	}
	
	public static int GetLightStep(byte block) {
		if(BlockType.isTranslucent(block)) {
			return 1;
		} else {
			return 2;
		}
	}
	
}