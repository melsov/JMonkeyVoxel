package voxel.landscape.chunkbuild;

import voxel.landscape.BlockMeshUtil;
import voxel.landscape.BlockType;
import voxel.landscape.Chunk;
import voxel.landscape.Coord3;
import voxel.landscape.Direction;
import voxel.landscape.MeshSet;
import voxel.landscape.map.TerrainMap;

import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

public class ChunkBuilder 
{
	public static MeshSet buildMesh(Chunk chunk, boolean lightOnly) {
		return buildMesh(chunk, null, lightOnly);
	}
	public static MeshSet buildMesh(Chunk chunk, Mesh mesh)
	{
		return buildMesh(chunk, mesh, false);
	}
	
	public static MeshSet buildMesh(Chunk chunk, Mesh mesh, boolean lightOnly)
	{
		MeshSet mset = new MeshSet();
		
		int xin = 0, yin = 0, zin = 0;
		Coord3 posi;
		int triIndex = 0;
		int i = 0, j = 0, k = 0;
		
		TerrainMap map = chunk.getTerrainMap();
		
		Coord3 worldPosBlocks = chunk.originInBlockCoords();

		for(i = 0; i < Chunk.XLENGTH; ++i)
		{
			for(j = 0; j < Chunk.ZLENGTH; ++j)
			{
				for (k = 0; k < Chunk.YLENGTH; ++k) 
				{
					xin = i + worldPosBlocks.x; yin = k  + worldPosBlocks.y; zin = j  + worldPosBlocks.z;
					posi = new Coord3(i,k,j);
					byte btype = (byte) map.lookupOrCreateBlock(xin, yin, zin);
					
					chunk.setBlockAt(btype, posi);
					if (BlockType.AIR.equals(btype)) {
						continue;
					}
					
					for (int dir = 0; dir <= Direction.ZPOS; ++dir) // Direction ZPOS = 5 (0 to 5 for the 6 sides of the column)
					{
						Coord3 worldcoord = new Coord3(xin, yin, zin);
						if (IsFaceVisible(map, worldcoord, dir)) {
							if (!lightOnly) BlockMeshUtil.AddFaceMeshData(posi, mset, btype, dir, triIndex, map);
							BlockMeshUtil.AddFaceMeshLightData(worldcoord, mset, dir, map);
							triIndex += 4;
						}
					}
				}
			}
		}
//		ApplyMeshSet(mset, mesh, lightOnly);
		return mset;
	}
	
	public static void ApplyMeshSet(MeshSet mset, Mesh bigMesh, boolean lightOnly) {
		if (!lightOnly) 
		{
			bigMesh.clearBuffer(Type.Position);
			bigMesh.clearBuffer(Type.TexCoord);
			bigMesh.clearBuffer(Type.TexCoord2);
			bigMesh.clearBuffer(Type.Index);
			bigMesh.clearBuffer(Type.Normal);
			
			bigMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(mset.vertices.toArray(new Vector3f[0])));
			bigMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(mset.uvs.toArray(new Vector2f[0])));
			bigMesh.setBuffer(Type.TexCoord2, 2, BufferUtils.createFloatBuffer(mset.texMapOffsets.toArray(new Vector2f[0])));
	
			// google guava library helps with turning Lists into primitive arrays
			// "Ints" and "Floats" are guava classes. 
			bigMesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(Ints.toArray(mset.indices)));
			bigMesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(mset.normals.toArray(new Vector3f[0])));
		}
		bigMesh.clearBuffer(Type.Color);
		bigMesh.setBuffer(Type.Color, 4, Floats.toArray(mset.colors));
		
//		bigMesh.setDynamic();
//		bigMesh.setMode(Mesh.Mode.Triangles);
//		bigMesh.updateBound();
	}
	
	private static boolean IsFaceVisible(TerrainMap terrainMap, Coord3 woco, int direction) {
		byte btype = (byte) terrainMap.lookupOrCreateBlock(woco.add(Direction.DirectionCoordForDirection(direction))); 
		return BlockType.isTranslucent(btype);
	}
}
