package voxel.landscape.map.light;

import java.util.ArrayList;
import java.util.List;

import voxel.landscape.BlockType;
import voxel.landscape.Chunk;
import voxel.landscape.Coord3;
import voxel.landscape.Direction;
import voxel.landscape.collection.ColumnMap;
import voxel.landscape.map.TerrainMap;
import voxel.landscape.util.Asserter;


public class ChunkSunLightComputer 
{
	
	private final static byte MIN_LIGHT = 1;
	private final static byte MAX_LIGHT = 15;
	private final static byte STEP_LIGHT = 1;
	
	private static ArrayList<Coord3> list = new ArrayList<Coord3>();
	
	public static void ComputeRays(TerrainMap map, int cx, int cz) {
		int x1 = cx*Chunk.CHUNKDIMS.x - 1; // SIZE_X-1;
		int z1 = cz*Chunk.CHUNKDIMS.z - 1; // SIZE_Z-1;
		
		int x2 = x1+Chunk.CHUNKDIMS.x + 2; //.SIZE_X+2;
		int z2 = z1+Chunk.CHUNKDIMS.z + 2; // SIZE_Z+2;
		
		for(int z=z1; z<z2; z++) {
			for(int x=x1; x<x2; x++) {
				SunLightComputer.ComputeRayAtPosition(map, x, z);
			}
		}
	}
	
	public static void Scatter(TerrainMap map, ColumnMap columnMap, int cx, int cz) {
		int x1 = cx*Chunk.CHUNKDIMS.x - 1; 
		int z1 = cz*Chunk.CHUNKDIMS.z - 1; 
		
		int x2 = x1 + Chunk.CHUNKDIMS.x + 2; 
		int z2 = z1 + Chunk.CHUNKDIMS.z + 2; 
		
		SunLightMap sunlightmap = map.GetSunLightmap();
		list.clear();
		for(int x=x1; x<x2; x++) {
			for(int z=z1; z<z2; z++) {
				int maxY = ComputeMaxY(sunlightmap, x, z)+1;
				for(int y=0; y<maxY; y++) {
					if(sunlightmap.GetLight(x, y, z) > MIN_LIGHT) {
						list.add( new Coord3(x, y, z) );
					}
				}
			}
		}
		Scatter(map, columnMap, list);
	}
	
	private static void Scatter(TerrainMap map, ColumnMap columnMap, List<Coord3> list) { 
		SunLightMap sunlightmap = map.GetSunLightmap();
		for(int i=0; i<list.size(); i++) {
			Coord3 pos = list.get(i);
			if(pos.y<0) continue;
			
			byte block = map.lookupBlock(pos);
			int light = sunlightmap.GetLight(pos) - LightComputerUtils.GetLightStep(block);
			if(light <= MIN_LIGHT) continue;
			
			Coord3 chunkPos = Chunk.ToChunkPosition(pos);
			if(!columnMap.IsBuilt(chunkPos.x, chunkPos.z)) continue;
			
			for (Coord3 dir : Direction.DirectionCoords ) {
				Coord3 nextPos = pos.add(dir);
				block = map.lookupBlock(nextPos);
				if( BlockType.isTranslucent(block) && sunlightmap.SetMaxLight((byte)light, nextPos) ) {
					list.add( nextPos );
				}
				if(!BlockType.IsEmpty(block)) LightComputerUtils.SetLightDirty(map, nextPos);
			}
		}
	}
	
	private static int ComputeMaxY(SunLightMap sunLightmap, int x, int z) {
		int maxY = sunLightmap.GetSunHeight(x, z);
		maxY = Math.max(maxY, sunLightmap.GetSunHeight(x-1, z  ));
		maxY = Math.max(maxY, sunLightmap.GetSunHeight(x+1, z  ));
		maxY = Math.max(maxY, sunLightmap.GetSunHeight(x,   z-1));
		maxY = Math.max(maxY, sunLightmap.GetSunHeight(x,   z+1));
		return maxY;
	}
	
}