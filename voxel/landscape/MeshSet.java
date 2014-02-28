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

public class MeshSet 
{
	public Vector3f[] vertices;
	public int[] indices;
	public Vector2f[] uvs;
	public float[] colors;
	
	public MeshSet(int vertexCount)
	{
		//there will always be the same
		//number of vertices and uvs
		//and always 6 indices for every 4 verts/uvs
		
		if (vertexCount % 2 != 0) {
			//Get upset
		}
		
		vertices = new Vector3f[vertexCount];
		uvs = new Vector2f[vertexCount];
		indices = new int[(vertexCount/2) * 3];
		colors = new float[vertexCount * 4];
		
	}
}
