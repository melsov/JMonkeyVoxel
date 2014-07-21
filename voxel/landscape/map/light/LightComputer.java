package voxel.landscape.map.light;

import java.util.ArrayList;
import java.util.List;

import voxel.landscape.Coord3;
import voxel.landscape.Direction;
import voxel.landscape.BlockType;
import voxel.landscape.map.TerrainMap;

public class LightComputer 
{
	
	public static final byte MIN_LIGHT = 1;
	public static final byte MAX_LIGHT = 15;
	public static final byte STEP_LIGHT = 1;
	
	private static List<Coord3> list = new ArrayList<Coord3>();
	
	public static void RecomputeLightAtPosition(TerrainMap map, Coord3 pos) {
		LightMap lightmap = map.GetLightmap();
		int oldLight = lightmap.GetLight(pos);
		int light = lightAtWorldCoord(map, pos); // map.blockAtWorldCoord(pos).GetLight();
		
		if(oldLight > light) {
			RemoveLight(map, pos);
		}
		if(light > MIN_LIGHT) {
			Scatter(map, pos);
		}
	}
	private static int lightAtWorldCoord(TerrainMap map, Coord3 pos) {
		byte block = map.lookupBlock(pos);
		return BlockType.LightLevelForType(block);
	}
	
	private static void Scatter(TerrainMap map, Coord3 pos) {
		list.clear();
		list.add( pos );
		Scatter(map, list);
	}
	private static void Scatter(TerrainMap map, List<Coord3> list) { 
		LightMap lightmap = map.GetLightmap();
		
		for( Coord3 pos : list ) {
			byte light = (byte) lightAtWorldCoord(map, pos); // map.GetBlock(pos).GetLight();
			if(light > MIN_LIGHT) lightmap.SetMaxLight(light, pos);
		}
		
        for(int i=0; i<list.size(); i++) {
        	Coord3 pos = list.get(i);
			if(pos.y<0) continue;
			
			byte block = map.lookupBlock(pos);
            int light = lightmap.GetLight(pos) - LightComputerUtils.GetLightStep(block);
            if(light <= MIN_LIGHT) continue;
			
            for(Coord3 dir : Direction.DirectionCoords) {
            	Coord3 nextPos = pos.add(dir); 
				block = map.lookupBlock(nextPos);
                if( BlockType.isTranslucent(block) && lightmap.SetMaxLight((byte)light, nextPos) ) {
                	list.add( nextPos );
                }
				if(!BlockType.IsEmpty(block)) LightComputerUtils.SetLightDirty(map, nextPos);
            }
        }
    }
	
	private static void RemoveLight(TerrainMap map, Coord3 pos) {
        list.clear();
		list.add(pos);
        RemoveLight(map, list);
    }
	
	private static void RemoveLight(TerrainMap map, List<Coord3> list) {
		LightMap lightmap = map.GetLightmap();
		for(Coord3 pos : list) {
			lightmap.SetLight(MAX_LIGHT, pos);
		}
		
		List<Coord3> lightPoints = new ArrayList<Coord3>();
		for(int i=0; i<list.size(); i++) {
			Coord3 pos = list.get(i);
			if(pos.y<0) continue;
			
			int light = lightmap.GetLight(pos) - STEP_LIGHT;
			
			lightmap.SetLight(MIN_LIGHT, pos);
            if (light <= MIN_LIGHT) continue;
			
			for(Coord3 dir : Direction.DirectionCoords) {
				Coord3 nextPos = pos.add(dir);
				byte block = map.lookupBlock(nextPos);
				
				if(BlockType.isTranslucent(block)) {
					if(lightmap.GetLight(nextPos) <= light) {
						list.add( nextPos );
					} else {
						lightPoints.add( nextPos );
					}
				}
				if(lightAtWorldCoord(map, nextPos) > MIN_LIGHT) {
					lightPoints.add( nextPos );
				}
				
				if(!BlockType.IsEmpty(block)) LightComputerUtils.SetLightDirty(map, nextPos);
			}	
		}
		
		
        Scatter(map, lightPoints);
    }
	
	
}
