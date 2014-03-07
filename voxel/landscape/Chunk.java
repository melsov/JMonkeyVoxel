package voxel.landscape;

import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
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

import simplex.noise.*;

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
	private static final int BASE_HEIGHT= (YLENGTH - 4) / 16;
	
	public static Coord3 CHUNKDIMS = new Coord3(XLENGTH, YLENGTH, ZLENGTH);
	
	private Coord2 worldPositionBlocks;
	
	private Geometry geomObject;
	
	private static final double noise_zoom = 1d; // 40d;
	
	private static IHeightDataProvider ChunkHeightDataProvider = new TerrainDataProvider(TerrainDataProvider.Mode.ImageMode);
	private static IBlockTypeDataProvider BlockDataProvider = (IBlockTypeDataProvider) ChunkHeightDataProvider;
	
	public Chunk(Coord2 _coord)
	{
		position = _coord;
	}
	
	public Geometry getGeometryObject()
	{
		if (this.geomObject == null) 
		{
			this.geomObject = new Geometry("chunk_geom", this.mesh());
			this.geomObject.move( this.originInBlockCoords().toVec3XZ());
		}
		return geomObject;
	}
	
	public Coord2 originInBlockCoords()
	{
		if (worldPositionBlocks == null) {
			worldPositionBlocks = position.multy(new Coord2(XLENGTH, ZLENGTH));
		}
		return worldPositionBlocks;
	}
	
	private Mesh mesh()
	{
		if (this.mesh == null) {
			this.mesh = makeMesh();
		}
		return this.mesh;
	}
	
	private Mesh makeMesh()
	{
		Mesh bigMesh = new Mesh();
		MeshSet bigMSet = new MeshSet();
		
		double xin = 0d;
		double zin = 0d;
		
		int triIndexCursor = 0;
		int i = 0, j = 0, k = 0;
		
		Coord2 worldPosBlocks = this.originInBlockCoords();
		
		//test version
		for(i = 0; i < Chunk.XLENGTH; ++i)
		{
			for(j = 0; j < Chunk.ZLENGTH; ++j)
			{
				
				int startHeight = 0;
				xin = (double)(i + worldPosBlocks.x)/noise_zoom;
				zin = (double)(j + worldPosBlocks.y)/noise_zoom;
				
				double noise_val = ChunkHeightDataProvider.heightAt(xin, zin );
				
				int height = (int)(BASE_HEIGHT + (BASE_HEIGHT - 1) * noise_val);
				Coord3 posi = new Coord3(i,startHeight,j);
				int btype = BlockDataProvider.blockTypeAt(xin, zin);
				ColumnMeshData cmd = Chunk.MakeColMeshData(height, posi, btype);
				MeshSet mset = BlockMeshUtil.MeshSetFromColumnData(cmd);
				
//				int colorIndex = 0;
				int vertexCount = mset.vertices.size();
				for(k = 0; k < vertexCount; k++)
				{
					bigMSet.vertices.add(mset.vertices.elementAt(k));
					bigMSet.uvs.add(mset.uvs.elementAt(k));
					bigMSet.texMapOffsets.add(mset.texMapOffsets.elementAt(k));
					
//					colorIndex = k * 4;
//					bigMSet.colors.add(mset.colors.elementAt(colorIndex++));
//					bigMSet.colors.add(mset.colors.elementAt(colorIndex++));
//					bigMSet.colors.add(mset.colors.elementAt(colorIndex++));
//					bigMSet.colors.add(mset.colors.elementAt(colorIndex));
				}
				
				for(k = 0; k < mset.indices.size(); k++)
				{
					bigMSet.indices.add(mset.indices.elementAt(k) + triIndexCursor);
				}
				triIndexCursor += vertexCount;
			}
		}
		
		bigMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(bigMSet.vertices.toArray(new Vector3f[0])));
		bigMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(bigMSet.uvs.toArray(new Vector2f[0])));
		bigMesh.setBuffer(Type.TexCoord2, 2, BufferUtils.createFloatBuffer(bigMSet.texMapOffsets.toArray(new Vector2f[0])));

		// google guava library helps with turning Lists into primitive arrays
		// "Ints" and "Floats" are guava classes. 
		bigMesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(Ints.toArray(bigMSet.indices)));
//		bigMesh.setBuffer(Type.Color, 4, Floats.toArray(bigMSet.colors));

		bigMesh.updateBound();
		bigMesh.setMode(Mesh.Mode.Triangles);

		return bigMesh;
	}

	private static ColumnMeshData MakeColMeshData(int height, Coord3 position, int blocktype)
	{
		ColumnMeshData cmd = new ColumnMeshData();
		cmd.column = new Column(position.y, height);
		cmd.position = position; // new Coord3(0,0,0);
		cmd.type = blocktype;
		
		return cmd;
	}
	
	

}
