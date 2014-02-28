package voxel.landscape;

import com.jme3.app.SimpleApplication;
//import com.jme3.bounding.BoundingVolume.Type.Position;
//import com.jme3.bounding.BoundingVolume.Type;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;
import com.jme3.math.ColorRGBA;

/**
 * Owns a mesh representing a 
 * piece of a voxel landscape.  
 * 
 */
public class Chunk 
{
	public Mesh mesh;
	public Coord2 position;
	
	private Column heightMap[][] = new Column[XLENGTH][ZLENGTH];
	
	private static final int XLENGTH = 16;
	private static final int YLENGTH = 256;
	private static final int ZLENGTH = 16;
	
	public static Coord3 CHUNKDIMS = new Coord3(XLENGTH, YLENGTH, ZLENGTH);
	
	public Chunk(Coord2 _coord)
	{
		position = _coord;
	}
}
