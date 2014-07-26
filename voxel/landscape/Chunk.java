package voxel.landscape;

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
	private ChunkBrain chunkBrain;
	private TerrainMap terrainMap;
	
	public Chunk(Coord3 _coord, TerrainMap _terrainMap)
	{
		position = _coord;
		terrainMap = _terrainMap;
		chunkBrain = new ChunkBrain(this);
	}
	
	public Geometry getGeometryObject() {
		return (Geometry) chunkBrain.getSpatial();
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
		 * Bitwise mod (%) by X/Y/ZLENGTH. but better. since this is much faster than '%' and as a bonus will always return positive numbers.
		 * the normal modulo operator ("%") will return negative for negative left-side numbers. (for example -14 % 10 becomes -4. <--bad. 
		 * since all local coords are positive we, want -14 mod 10 to be 6.)
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
		/*
		 * Opposite of ToChunkPosition
		 */
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

}
