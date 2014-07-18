package voxel.landscape;

import java.util.Vector;

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

public class MeshSet 
{
	public Vector<Vector3f> vertices = new Vector<Vector3f>();
	public Vector<Integer> indices = new Vector<Integer>();
	public Vector<Vector2f> uvs = new Vector<Vector2f>();
	public Vector<Vector2f> texMapOffsets = new Vector<Vector2f>();
	public Vector<Float> colors = new Vector<Float>();
	public Vector<Vector3f> normals = new Vector<Vector3f>();
}
