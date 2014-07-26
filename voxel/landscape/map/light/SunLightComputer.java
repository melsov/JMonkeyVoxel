package voxel.landscape.map.light;

import java.util.ArrayList;

import voxel.landscape.BlockType;
import voxel.landscape.Coord3;
import voxel.landscape.Direction;
import voxel.landscape.map.TerrainMap;

public class SunLightComputer {
	
	public final static byte MIN_LIGHT = 1;
	public final static byte MAX_LIGHT = 15;
	public final static byte STEP_LIGHT = 1;
	
	private static ArrayList<Coord3> list = new ArrayList<Coord3>();

	public static void ComputeRayAtPosition(TerrainMap map, int x, int z) {
		int maxY = map.GetMaxY( x, z );
		map.GetSunLightmap().SetSunHeight(maxY+1, x, z);
	}
	
	private static void Scatter(TerrainMap map, ArrayList<Coord3> list) { 
		SunLightMap sunlightmap = map.GetSunLightmap();
        for(int i=0; i<list.size(); i++) {
            Coord3 pos = list.get(i);
			if(pos.y<0) continue;
			
			byte block = map.lookupBlock(pos);
			int light = sunlightmap.GetLight(pos) - LightComputerUtils.GetLightStep(block);
            if(light <= MIN_LIGHT) continue;
			
            for(Coord3 dir : Direction.DirectionCoords) {
				Coord3 nextPos = pos.add(dir);
				block = map.lookupBlock(nextPos);
                if( BlockType.isTranslucent(block) && sunlightmap.SetMaxLight((byte)light, nextPos) ) {
                	list.add( nextPos );
                }
				if(!BlockType.IsEmpty(block)) LightComputerUtils.SetLightDirty(map, nextPos);
            }
        }
    }
	
	public static void RecomputeLightAtPosition(TerrainMap map, Coord3 pos) {
		SunLightMap lightmap = map.GetSunLightmap();
		int oldSunHeight = lightmap.GetSunHeight(pos.x, pos.z);
		ComputeRayAtPosition(map, pos.x, pos.z);
		int newSunHeight = lightmap.GetSunHeight(pos.x, pos.z);
		
		if(newSunHeight < oldSunHeight) { //sun reaching further down
			list.clear();
            for (int ty = newSunHeight; ty <= oldSunHeight; ty++) {
				pos.y = ty;
                lightmap.SetLight(MIN_LIGHT, pos);
                list.add( pos );
            }
            Scatter(map, list);
		}
		if(newSunHeight > oldSunHeight) { //sun not reaching as far
			list.clear();
            for (int ty = oldSunHeight; ty <= newSunHeight; ty++) {
				pos.y = ty;
				list.add( pos );
            }
            RemoveLight(map, list);
		}
		
		if(newSunHeight == oldSunHeight) {
			if(BlockType.isTranslucent(map.lookupBlock(pos) ) ) {
				UpdateLight(map, pos);
			} else {
				RemoveLight(map, pos);
			}
		}
	}
	
	
	private static void UpdateLight(TerrainMap map, Coord3 pos) {
        list.clear();
		for(Coord3 dir : Direction.DirectionCoords) {
			list.add( pos.add(dir) );
		}
        Scatter(map, list);
	}
    
	private static void RemoveLight(TerrainMap map, Coord3 pos) {
        list.clear();
		list.add(pos);
        RemoveLight(map, list);
    }
	
	private static void RemoveLight(TerrainMap map, ArrayList<Coord3> list) {
		SunLightMap lightmap = map.GetSunLightmap();
		for(Coord3 pos : list) {
			lightmap.SetLight(MAX_LIGHT, pos);
		}
		
		ArrayList<Coord3> lightPoints = new ArrayList<Coord3>();
		for(int i=0; i<list.size(); i++) {
            Coord3 pos = list.get(i);
			if(pos.y<0) continue;
			if(lightmap.IsSunLight(pos.x, pos.y, pos.z)) {
				lightPoints.add( pos );
				continue;
			}
			byte light = (byte) (lightmap.GetLight(pos) - STEP_LIGHT);
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
				if(!BlockType.IsEmpty(block)) LightComputerUtils.SetLightDirty(map, nextPos);
			}	
		}
		
        Scatter(map, lightPoints);
    }

}

