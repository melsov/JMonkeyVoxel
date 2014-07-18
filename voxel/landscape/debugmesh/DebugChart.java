package voxel.landscape.debugmesh;

import java.util.Arrays;

import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Quad;
import com.jme3.util.BufferUtils;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import voxel.landscape.BlockMeshUtil;
import voxel.landscape.BlockType;
import voxel.landscape.Coord3;
import voxel.landscape.MeshSet;
import voxel.landscape.map.light.SunLightComputer;
import voxel.landscape.util.Asserter;

public class DebugChart 
{

	public enum DebugShapeType {
		QUAD, CYLINDER 
	}
	private Coord3 min;
	private Coord3 max;
	
	public Coord3 getSize() { return max.minus(min); }
	
	public DebugChart(Coord3 _min, Coord3 _max) {
		min = _min; max = _max;
		Asserter.assertTrue( max.greaterThan(min), "Kya? max less than min?? (debug chart)");
	}
	public DebugChart() {
		this(Coord3.Zero, new Coord3(16, 64, 16));
	}

	public Node makeHeightMap(DebugShapeType shapeType, float verticalOffSet, Material material, IDebugGet2D get2d) {
		BatchNode result = new BatchNode("debug root");
		for (int x = min.x; x < max.x; ++x) {
			for (int z = min.z; z < max.z; ++z) {
				float y = get2d.getAValue(x, z);
				Geometry g = geometryForType(DebugShapeType.QUAD);
				g.move(x, y, z);
//				g.rotate(90f, 0, 90f);
				g.rotateUpTo(Vector3f.UNIT_Z.subtract(new Vector3f(0,0,5f)));
				g.setMaterial(material);
				result.attachChild(g);
			}
		}
		result.batch();
		return result;   
	}
	
	public Geometry makeHeightMapVertsUVs(DebugShapeType shapeType, float verticalOffSet, Material material, IDebugGet2D get2d) {
		MeshSet mset = new MeshSet();
		
		Integer triIndex = 0;
		for (int x = min.x; x < max.x; ++x) {
			for (int z = min.z; z < max.z; ++z) {
				float y = get2d.getAValue(x, z);
				triIndex = AddVertsAndUVs(mset, x, y, z, verticalOffSet, triIndex);
			}
		}
		
		/*
		 * make mesh, convert data to arrays
		 */
		Mesh mesh = new Mesh();
		mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(mset.vertices.toArray(new Vector3f[0])));
		mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(mset.uvs.toArray(new Vector2f[0])));
//		bigMesh.setBuffer(Type.TexCoord2, 2, BufferUtils.createFloatBuffer(bigMSet.texMapOffsets.toArray(new Vector2f[0])));

		// google guava library helps with turning Lists into primitive arrays
		// "Ints" and "Floats" are guava classes. 
		mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(Ints.toArray(mset.indices)));
		mesh.setBuffer(Type.Color, 4, Floats.toArray(mset.colors));
//		bigMesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(bigMSet.normals.toArray(new Vector3f[0])));

		mesh.updateBound();
		mesh.setMode(Mesh.Mode.Triangles);
		
		Geometry g = new Geometry("Debug geom", mesh);
		g.setMaterial(material);
		return g;
	}
	public Geometry makeTerrainInfoVertsUVs3D(DebugShapeType shapeType, float verticalOffSet, Material material, IDebugGet3D get3d) {
		MeshSet mset = new MeshSet();
		
		Integer triIndex = 0;
		for (int x = min.x; x < max.x; ++x) {
			for (int z = min.z; z < max.z; ++z) {
				for(int y = min.y; y < max.y; ++y) {
					if (y > max.minus(min).y/2.0) break;
					int res = (int)get3d.getAValue(x, y, z);
//					java.awt.Color color = BlockType.debugColor((int) res);
//					if( res == SunLightComputer.MAX_LIGHT)
					if (res >= SunLightComputer.MIN_LIGHT && res < SunLightComputer.MAX_LIGHT ) {
						float light = res/(float)SunLightComputer.MAX_LIGHT;
						triIndex = AddVertsAndUVs(mset, x, y, z, verticalOffSet, triIndex, new float[] {light,light, .2f, 1f});
					}
				}
			}
		}
		
		/*
		 * make mesh, convert data to arrays
		 */
		Mesh mesh = new Mesh();
		mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(mset.vertices.toArray(new Vector3f[0])));
		mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(mset.uvs.toArray(new Vector2f[0])));
//		bigMesh.setBuffer(Type.TexCoord2, 2, BufferUtils.createFloatBuffer(bigMSet.texMapOffsets.toArray(new Vector2f[0])));

		// google guava library helps with turning Lists into primitive arrays
		// "Ints" and "Floats" are guava classes. 
		mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(Ints.toArray(mset.indices)));
		mesh.setBuffer(Type.Color, 4, Floats.toArray(mset.colors));
//		bigMesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(bigMSet.normals.toArray(new Vector3f[0])));

		mesh.updateBound();
		mesh.setMode(Mesh.Mode.Triangles);
		
		Geometry g = new Geometry("Debug geom", mesh);
		g.setMaterial(material);
		return g;
	}
	private static Integer AddVertsAndUVs(MeshSet mset, float x, float y, float z, float verticalOffset, Integer triIndex) {
		return AddVertsAndUVs(mset, x,y,z, verticalOffset, triIndex, new float[] {1f, 0f, 0f, 1f});
	}
	private static Integer AddVertsAndUVs(MeshSet mset, float x, float y, float z, float verticalOffset, Integer triIndex, float[] color) {
		Vector3f yPosVertices[] = BlockMeshUtil.faceVertices[3];
		
		for (int i=0; i < 3; ++i) {
			Vector3f v = yPosVertices[i];
			Vector3f vert = v.add(x, y, z);
			mset.vertices.add(vert);
			mset.indices.add(triIndex++);
			mset.uvs.add(new Vector2f(v.x,v.z));
			for (float c : color) { mset.colors.add(c); }
//			mset.colors.addAll(Arrays.asList());
		}
		mset.indices.addAll(Arrays.asList(triIndex - 1, triIndex - 2, triIndex - 3));
		return triIndex;
	}
	
	private Geometry geometryForType(DebugShapeType type) {
		return new Geometry("new g", new Quad(1f,1f));
	}
}
