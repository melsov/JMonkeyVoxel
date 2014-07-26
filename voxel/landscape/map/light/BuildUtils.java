package voxel.landscape.map.light;

import com.jme3.math.ColorRGBA;
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
			ColorRGBA c1 = GetBlockLight(map, a);
			ColorRGBA c2 = GetBlockLight(map, b);
			ColorRGBA c3 = GetBlockLight(map, c);
			ColorRGBA c4 = GetBlockLight(map, d);
			ColorRGBA res = c1.add(c2).add(c3).add(c4).mult(.25f); // (c1 + c2 + c3 + c4)/4f;
			return res.toArray(null);// new float[] {res,res,res,res};
		} else {
			ColorRGBA c1 = GetBlockLight(map, a);
			ColorRGBA c2 = GetBlockLight(map, b);
			ColorRGBA c3 = GetBlockLight(map, c);
			ColorRGBA res = c1.add(c2).add(c3).mult(.33f); // (c1 + c2 + c3)/3f;
			return res.toArray(null); // new float[] {res,res,res,res};
		}
	}
	public static ColorRGBA GetBlockLight(TerrainMap map, Coord3 pos) {
		Coord3 chunkPos = Chunk.ToChunkPosition(pos);
		Coord3 localPos = Chunk.toChunkLocalCoord(pos);
		/*
		 * TODO: use light in shader....
		 */
		float light = (float) map.GetLightmap().GetLight( chunkPos, localPos ) / (float) SunLightComputer.MAX_LIGHT;
		float sun = (float) map.GetSunLightmap().GetLight( chunkPos, localPos, pos.y ) / (float) SunLightComputer.MAX_LIGHT;
		return new ColorRGBA(sun,sun,sun,sun); //  sun; //??? TODO: figure how to work this... 
	}
}
