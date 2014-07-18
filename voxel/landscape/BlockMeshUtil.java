package voxel.landscape;

import voxel.landscape.map.TerrainMap;
import voxel.landscape.map.light.BuildUtils;
import voxel.landscape.noise.IBlockDataProvider;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

// GLSL driven grass movement and footprints!!

public class BlockMeshUtil 
{
	public static void AddFaceMeshData(Coord3 pos, MeshSet mset, byte blockType, int direction, int triIndexStart, TerrainMap terrainData)
	{
		//make four verts
		//and for indices
		//make four UVS.
		//add to mesh Set

		int i = direction;
		//TODO: CHANGE funcs to take mesh set. then, they will just add to it as needed.
		Vector3f[] verts = BlockMeshUtil.FaceVertices(pos, i);
		Vector2f[] uvs = BlockMeshUtil.UVsForDirection(i);
		Vector2f[] texOffsets = BlockMeshUtil.TexCoordOffsetsForBlockType(blockType, i);
		Vector3f norm = BlockMeshUtil.FaceNormalsForDirection(i);
		
		int[]indices = BlockMeshUtil.IndicesForDirection();
//		BuildFaceLight(direction, terrainData, pos, mset);  // BlockMeshUtil.ColorsForDirection(i, terrainData);
		
		for(int j = 0; j < 4; ++j)
		{
			mset.vertices.add(verts[j]); 
			mset.uvs.add(uvs[j]); 
			mset.texMapOffsets.add(texOffsets[j]);
			mset.normals.add(norm);
		}
		for(int k = 0; k < 6; ++k) {
			mset.indices.add(triIndexStart + indices[k]);
		}
//		for(int m = 0; m < 16; ++m) {
//			mset.colors.add(colors[m]); 
//		}
	}
	
	public static void AddFaceMeshLightData(Coord3 pos, MeshSet mset, int direction, TerrainMap terrainData)
	{
		BuildFaceLight(direction, terrainData, pos, mset);  // BlockMeshUtil.ColorsForDirection(i, terrainData);
	}
	
	private static Vector2f[] TexCoordOffsetsForBlockType(int btype, int dir)
	{
		Vector2f ret = new Vector2f(0f,0f);
		if (BlockType.DIRT.equals(btype)) {
			ret.y = .25f;
		}
		else if (BlockType.GRASS.equals(btype) && dir != Direction.YPOS) {
			ret.y = .75f;
		}
		else if (BlockType.SAND.equals(btype)) {
			ret.x = .25f;
			ret.y = .75f;
		} 
		else if (BlockType.STONE.equals(btype)) {
			ret.x = .25f;
			ret.y = .25f;
		}
		else if (BlockType.CAVESTONE.equals(btype)) {
			ret.x = .75f;
			ret.y = .25f;
		}
		else if (BlockType.BEDROCK.equals(btype)) {
			ret.x = .75f;
		}
		return new Vector2f[]{ret,ret,ret,ret};
	}
	private static Vector3f FaceNormalsForDirection(int dir) {
		int axis = Direction.AxisForDirection(dir);
		Vector3f normv;
		if (axis == Axis.X) {
			normv = Vector3f.UNIT_X;
		} else if (axis == Axis.Y){
			normv = Vector3f.UNIT_Y;
		} else {
			normv = Vector3f.UNIT_Z;
		}
		if (Direction.IsNegDir(dir)) 
			normv = normv.mult(-1.0f);
		return normv;
	}
	
	/*
	 * returns an array of vertices that describe one of the 6 faces of a 3D box shaped column
	 * decide which face based on @param dir (direction) 
	 */
	private static Vector3f[] FaceVertices(Coord3 position, int dir )
	{
		Vector3f[] result = new Vector3f[4];

		float posX = position.x;
		float posZ = position.z;
		float posY = position.y; 
		
		for (int i = 0; i < 4; ++i) {
			result[i] = faceVertices[dir][i].add(new Vector3f(posX, posY, posZ)); // TODO: get rid of column mesh data?
		}
		
		return result;
	}
	
	private static int[] IndicesForDirection()
	{
		return new int[] {0,3,2, 0,2,1};
	}
	
	private static Vector2f[] UVsForDirection(int dir)
	{
		return BlockMeshUtil.UVsForDirection(dir, 1, 1);
//		return BlockMeshUtil.UVsForDirection(dir, height, 1);
	}
	
	private static Vector2f[] UVsForDirection(int dir, int height, int width)
	{
		if (Direction.AxisForDirection(dir) == Axis.Y) {
			return new Vector2f[] {new Vector2f(0,0),new Vector2f(width,0),new Vector2f(width,height),new Vector2f(0,height)};
		}
		//HACK FLIP HEIGHT AND WIDTH?? // TODO: FIGURE THIS WHOLE THING A LITTLE MORE...
		return new Vector2f[] {new Vector2f(0,0),new Vector2f(0,height),new Vector2f(width,height),new Vector2f(width,0)};
	}
	
	private static float[] ColorsForDirection(int dir, IBlockDataProvider terrainData)
	{
//		float flipper = 1f;
//		if (dir > 2)
//			flipper = -1f;
//		
//		float[] black = new float[]{0f,0f,0f,1f};
//		float[] white = new float[]{1f,1f,1f,1f};
		
		float[] fakeAmbLights = new float[] {
				0f,0f,0f,1f,
				1f,1f,1f,1f,
				0f,0f,0f,1f,
				0f,0f,0f,1f
		};
		return fakeAmbLights;
		
//		float[] red = new float[]{1f,0f,0f,1f};
//		float[] blue = new float[]{0f,0f,1f,1f};
//		float[] yellow = new float[]{1f,1f,0f,1f};
//		
//		float[] faceColor = red;
//		if (dir == Direction.XNEG) {
//			return new float[] {1f,1f,0f,1f,1f,1f,0f,1f,1f,1f,0f,1f,1f,1f,0f,1f};
////			faceColor = yellow;
//		}
//		else if (dir == Direction.ZNEG) {
//			return new float[] {0f,0f,1f,1f,0f,0f,1f,1f,0f,0f,1f,1f,0f,0f,1f,1f};
//		}
////			faceColor = blue;
//		
//		float[] colorArray = new float[4*4];
//    	int colorIndex=0;
//    	for(int i = 0; i < 4; i++)
//    	{
////    		float[] color = red;
//    		
//		   // Red value (is increased by .2 on each next vertex here)
//			colorArray[colorIndex++]=  0.5f+(.1f* i * flipper);
////		   colorArray[colorIndex++]= faceColor[i];// 0.5f+(.1f* i * flipper);
//		   // Green value (is reduced by .2 on each next vertex)
////		   colorArray[colorIndex++]=faceColor[i]; //0.5f-(0.1f*i * flipper);
//			colorArray[colorIndex++]= 0.5f-(0.1f*i * flipper);
//		   // Blue value (remains the same in our case)
//		   colorArray[colorIndex++]= 0.5f ;
//		   // Alpha value (no transparency set here)
//		   colorArray[colorIndex++]= 1.0f;
//		}
//    	
//    	return colorArray;
	}
	
	private static void BuildFaceLight(int facedir, TerrainMap map, Coord3 pos, MeshSet mset) {
		for(Vector3f ver : faceVertices[facedir]) {
			float[] color = BuildUtils.GetSmoothVertexLight(map, pos, ver, facedir);
			for (float c : color) {
				mset.colors.add(c);
			}
		}
	}
	
	public static Vector3f[][] faceVertices = new Vector3f[][] {
		//Xneg
		new Vector3f[] {
				new Vector3f(-0.5f, -0.5f, -0.5f),
				new Vector3f(-0.5f,  0.5f, -0.5f),
				new Vector3f(-0.5f,  0.5f,  0.5f),
				new Vector3f(-0.5f, -0.5f,  0.5f),	
		},
		//Xpos
		new Vector3f[] {
			new Vector3f(0.5f, -0.5f,  0.5f),
			new Vector3f(0.5f,  0.5f,  0.5f),
			new Vector3f(0.5f,  0.5f, -0.5f),
			new Vector3f(0.5f, -0.5f, -0.5f),
		},
		//Yneg
		new Vector3f[] {
			new Vector3f(-0.5f, -0.5f, -0.5f),
			new Vector3f(-0.5f, -0.5f,  0.5f),
			new Vector3f( 0.5f, -0.5f,  0.5f),
			new Vector3f( 0.5f, -0.5f, -0.5f),
		},
		//Ypos
		new Vector3f[] {
				new Vector3f( 0.5f, 0.5f, -0.5f),
				new Vector3f( 0.5f, 0.5f,  0.5f),
				new Vector3f(-0.5f, 0.5f,  0.5f),
				new Vector3f(-0.5f, 0.5f, -0.5f),
		},
		//Zneg
		new Vector3f[] {
			new Vector3f( 0.5f, -0.5f, -0.5f),
			new Vector3f( 0.5f,  0.5f, -0.5f),
			new Vector3f(-0.5f,  0.5f, -0.5f),
			new Vector3f(-0.5f, -0.5f, -0.5f),
		},
		//Zpos
		new Vector3f[] {
				new Vector3f(-0.5f, -0.5f, 0.5f),
				new Vector3f(-0.5f,  0.5f, 0.5f),
				new Vector3f( 0.5f,  0.5f, 0.5f),
				new Vector3f( 0.5f, -0.5f, 0.5f),
		},
	};
	
}
