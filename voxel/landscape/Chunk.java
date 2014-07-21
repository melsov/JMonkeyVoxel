package voxel.landscape;
/*
 * Notes: mini-class graph: Chunk owns a --> ref to Chunk's geometry object.
 * geom object provides a getter method for its 'controls'. A 'ChunkBrain' (extends AbstractControl, i.e. thing that gets an update loop call)
 * is attached to the geom object.
 * ChunkBrain objects 'wait around' until they are 'dirty' or 'lightdirty'.
 * In those cases, they update the chunk's mesh geometry (dirty) and/or mesh colors (lightdirty).  
 * NOTE: purge chunks ref to its geom? (let control own/create?) (why not? better this way...)
 * chunk still has to have a getter for its geometry...
 */
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import voxel.landscape.chunkbuild.ChunkBrain;
import voxel.landscape.collection.ByteArray3D;
import voxel.landscape.map.TerrainMap;
import static java.lang.System.out;
/**
 * Owns a mesh representing a 
 * XLEN by YLEN by ZLEN piece of a voxel landscape.  
 * 
 */
public class Chunk 
{
	public Mesh mesh;
	public Coord3 position;

	private ByteArray3D blocks = new ByteArray3D(new Coord3(XLENGTH, YLENGTH, ZLENGTH));
	
	public static final int SIZE_X_BITS = 4;
	public static final int SIZE_Y_BITS = 4;
	public static final int SIZE_Z_BITS = 4;
	
	/*
	 * bitwise multiplication by a power of 2. literally we are sliding 1 to the left by SIZE_X_BITS.
	 * Or in other words, 1 becomes binary 10000 which is decimal 16  
	 */
	public static final int XLENGTH = 1 << SIZE_X_BITS;
	public static final int YLENGTH = 1 << SIZE_Y_BITS;
	public static final int ZLENGTH = 1 << SIZE_Z_BITS;
	
	// TODO: unfortunately, purge this var. make XYZLENGTH public instead
	public static Coord3 CHUNKDIMS = new Coord3(XLENGTH, YLENGTH, ZLENGTH);
	
//	private Coord3 worldPositionBlocks;
	
//	private Geometry geomObject;
	private ChunkBrain chunkBrain;
	
	private TerrainMap terrainMap;
	
	public Chunk(Coord3 _coord, TerrainMap _terrainMap)
	{
		position = _coord;
		terrainMap = _terrainMap;

		chunkBrain = new ChunkBrain(this);
	}
	
	public Geometry getGeometryObject()
	{
		return (Geometry) chunkBrain.getSpatial();
//		if (this.geomObject == null) 
//		{
//			this.geomObject = new Geometry("chunk_geom", this.mesh());
//			this.geomObject.move( this.originInBlockCoords().toVector3());
//		}
//		return geomObject;
	}
	public ChunkBrain getChunkBrain() { return chunkBrain; }
	
	public TerrainMap getTerrainMap() { return terrainMap; }
	
	/*
	 * Block info...
	 */
	public static Coord3 ToChunkPosition(Coord3 point) {
		return ToChunkPosition( point.x, point.y, point.z );
	}
	public static Coord3 ToChunkPosition(int pointX, int pointY, int pointZ) {
		/*
		 * Bit-wise division: this is equivalent to pointX / (2 to the power of SIZE_X_BITS)  
		 * in other words pointX divided by 16. (only works for powers of 2 divisors)  
		 * This operation is much faster than the normal division operation ("/")
		 */
		int chunkX = pointX >> SIZE_X_BITS;
		int chunkY = pointY >> SIZE_Y_BITS;
		int chunkZ = pointZ >> SIZE_Z_BITS;
		return new Coord3(chunkX, chunkY, chunkZ);
	}
	public static Coord3 toChunkLocalCoord(Coord3 woco) {
		return toChunkLocalCoord(woco.x, woco.y, woco.z);
	}
	public static Coord3 toChunkLocalCoord(int x, int y, int z) {
		/*
		 * Bitwise mod by X/Y/ZLENGTH. but better. since this is much faster than '%' and as a bonus will always return positive numbers.
		 * the normal modulo operator ("%") will return negative for negative left-side numbers. (for example -14 % 10 becomes -4)
		 */
		int xlocal = x & (XLENGTH - 1);
		int ylocal = y & (YLENGTH - 1);
		int zlocal = z & (ZLENGTH - 1);
		return new Coord3(xlocal, ylocal, zlocal);
	}
	
	public static Coord3 ToWorldPosition(Coord3 chunkPosition) {
		return ToWorldPosition(chunkPosition, Coord3.Zero);
	}
	
	public static Coord3 ToWorldPosition(Coord3 chunkPosition, Coord3 localPosition) {
		int worldX = (chunkPosition.x << SIZE_X_BITS) + localPosition.x;
		int worldY = (chunkPosition.y << SIZE_Y_BITS) + localPosition.y;
		int worldZ = (chunkPosition.z << SIZE_Z_BITS) + localPosition.z;
		return new Coord3(worldX, worldY, worldZ);
	}
	
	public byte blockAt(Coord3 co) { return blockAt(co.x, co.y, co.z); }
	
	public byte blockAt(int x, int y, int z) {
		return blocks.SafeGet(x, y, z);
	}
	public void setBlockAt(byte block, Coord3 co) { setBlockAt(block, co.x, co.y, co.z); }
	
	public void setBlockAt(byte block, int x, int y, int z) {
		blocks.Set(block, x, y, z);
	}
	
	public Coord3 originInBlockCoords() { return Chunk.ToWorldPosition(position); }
	/*
	private Mesh mesh() //TODO: move this whole func..
	{
		if (this.mesh == null) {
			this.mesh = buildMesh(this, new Mesh());
		}
		return this.mesh;
	}
	*/
//	private static Mesh buildMesh(Chunk chunk, Mesh bigMesh)
//	{
//		return buildMesh(chunk, bigMesh, false);
//	}
//	
//	//AS A SEPARATE PROCESS...
//	//GENERATE THE MAP INFO (BLOCK TYPE, SUNLIGHT AND LIGHT) FOR AN ENTIRE COLUMN OF CHUNKS...OR AT LEAST A SINGLE CHUNK
//	
//	private static Mesh buildMesh(Chunk chunk, Mesh bigMesh, boolean lightOnly)
//	{
//		MeshSet mset = new MeshSet();
//		
//		int xin = 0, yin = 0, zin = 0;
//		Coord3 posi;
//		int triIndex = 0;
//		int i = 0, j = 0, k = 0;
//		
//		TerrainMap map = chunk.getTerrainMap();
//		
//		Coord3 worldPosBlocks = chunk.originInBlockCoords();
//
//		for(i = 0; i < Chunk.XLENGTH; ++i)
//		{
//			for(j = 0; j < Chunk.ZLENGTH; ++j)
//			{
//				for (k = 0; k < Chunk.YLENGTH; ++k) 
//				{
//					xin = i + worldPosBlocks.x; yin = k  + worldPosBlocks.y; zin = j  + worldPosBlocks.z;
//					posi = new Coord3(i,k,j);
//					byte btype = (byte) map.lookupOrCreateBlock(xin, yin, zin);
//					
//					chunk.setBlockAt(btype, posi);
//					if (BlockType.AIR.equals(btype)) {
//						continue;
//					}
//					
//					for (int dir = 0; dir <= Direction.ZPOS; ++dir) // Direction ZPOS = 5 (0 to 5 for the 6 sides of the column)
//					{
//						Coord3 worldcoord = new Coord3(xin, yin, zin);
//						if (IsFaceVisible(worldcoord, dir)) {
//							if (!lightOnly) BlockMeshUtil.AddFaceMeshData(posi, mset, btype, dir, triIndex, map);
//							BlockMeshUtil.AddFaceMeshLightData(worldcoord, mset, dir, map);
//							triIndex += 4;
//						}
//					}
//				}
//			}
//		}
//		
//		bigMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(mset.vertices.toArray(new Vector3f[0])));
//		bigMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(mset.uvs.toArray(new Vector2f[0])));
//		bigMesh.setBuffer(Type.TexCoord2, 2, BufferUtils.createFloatBuffer(mset.texMapOffsets.toArray(new Vector2f[0])));
//
//		// google guava library helps with turning Lists into primitive arrays
//		// "Ints" and "Floats" are guava classes. 
//		bigMesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(Ints.toArray(mset.indices)));
//		bigMesh.setBuffer(Type.Color, 4, Floats.toArray(mset.colors));
//		bigMesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(mset.normals.toArray(new Vector3f[0])));
//
//		bigMesh.updateBound();
//		bigMesh.setMode(Mesh.Mode.Triangles);
//
//		return bigMesh;
//	}
//	
//	private boolean IsFaceVisible(Coord3 woco, int direction) {
//		byte btype = (byte) terrainMap.lookupOrCreateBlock(woco.add(Direction.DirectionCoordForDirection(direction))); 
//		return BlockType.isTranslucent(btype);
//	}



}
