package voxel.landscape.map.light;

import com.jme3.math.Vector3f;

import voxel.landscape.Coord3;
import voxel.landscape.Chunk;
import voxel.landscape.Direction;
import voxel.landscape.map.TerrainMap;

public class BuildUtils {

	public static float[] GetSmoothVertexLight(TerrainMap map, Coord3 pos, Vector3f vertex, int face) {
		int dx = (int)Math.signum( vertex.x );
		int dy = (int)Math.signum( vertex.y );
		int dz = (int)Math.signum( vertex.z );
		
		Coord3 a, b, c, d;
		if(face == Direction.XNEG || face == Direction.XPOS) { // X
			a = pos.add(new Coord3(dx, 0,  0));
			b = pos.add(new Coord3(dx, dy, 0));
			c = pos.add(new Coord3(dx, 0,  dz));
			d = pos.add(new Coord3(dx, dy, dz));
		} else 
		if(face == Direction.YNEG || face == Direction.YPOS) { // Y
			a = pos.add(new Coord3(0,  dy, 0));
			b = pos.add(new Coord3(dx, dy, 0));
			c = pos.add(new Coord3(0,  dy, dz));
			d = pos.add(new Coord3(dx, dy, dz));
		} else { // Z
			a = pos.add(new Coord3(0,  0,  dz));
			b = pos.add(new Coord3(dx, 0,  dz));
			c = pos.add(new Coord3(0,  dy, dz));
			d = pos.add(new Coord3(dx, dy, dz));
		}
		
		if(map.blockAtWorldCoordIsTranslucent(b) || map.blockAtWorldCoordIsTranslucent(c)) {
			float c1 = GetBlockLight(map, a);
			float c2 = GetBlockLight(map, b);
			float c3 = GetBlockLight(map, c);
			float c4 = GetBlockLight(map, d);
			float res = (c1 + c2 + c3 + c4)/4f;
			return new float[] {res,res,res,res};
		} else {
			float c1 = GetBlockLight(map, a);
			float c2 = GetBlockLight(map, b);
			float c3 = GetBlockLight(map, c);
			float res = (c1 + c2 + c3)/3f;
			return new float[] {res,res,res,res};
		}
	}
	public static float GetBlockLight(TerrainMap map, Coord3 pos) {
		Coord3 chunkPos = Chunk.ToChunkPosition(pos);
		Coord3 localPos = Chunk.toChunkLocalCoord(pos);
		float light = (float) map.GetLightmap().GetLight( chunkPos, localPos ) / SunLightComputer.MAX_LIGHT;
		float sun = (float) map.GetSunLightmap().GetLight( chunkPos, localPos, pos.y ) / SunLightComputer.MAX_LIGHT;
		return sun; //??? TODO: figure how to work this... 
	}
}
