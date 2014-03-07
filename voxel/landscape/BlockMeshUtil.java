package voxel.landscape;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class BlockMeshUtil 
{
	public static MeshSet MeshSetFromColumnData(ColumnMeshData columnMeshData)
	{
		MeshSet mset = new MeshSet(); //(6 * 4);
		
		//for each of the 6 directions
		//make four verts
		//and for indices
		//add four * iterationCount to each index
		//make four UVS.
		//add to mesh Set

		for (int i = 0; i <= Direction.ZPOS; ++i) // Direction ZPOS = 5 (0 to 5 for the 6 sides of the column)
		{
			Vector3f[] verts = BlockMeshUtil.FaceVerticesForColumnAndDirection(columnMeshData, i);
			Vector2f[] uvs = BlockMeshUtil.UVsForDirection(i, columnMeshData.column.height);
			Vector2f[] texOffsets = BlockMeshUtil.TexCoordOffsetsForBlockType(columnMeshData.type, i);
			int[]indices = BlockMeshUtil.IndicesForDirection(i);
//			float[] colors = BlockMeshUtil.ColorsForDirection(i);
			
			int index = i * 4;
			for(int j = 0; j < 4; ++j)
			{
				mset.vertices.add(verts[j]); // [index + j] = verts[j];
				mset.uvs.add(uvs[j]); //[index + j] = uvs[j];
				mset.texMapOffsets.add(texOffsets[j]);
			}
			
			for(int k = 0; k < 6; ++k)
			{
				// for example: if indices are 0,2,3
				// the first face has verts numbers 0,1,2,3
				// the second face has verts numbers 4,5,6,7
				// the 3rd face has verts numbers: (3rd - 1) * (verts per face (4)) + 0 (and then + 1 or + 2 or + 3)
				// in other words 2 * 4 + 0,1,2,3 or index + 0,1,2,3 or... 8,9,10,11
				mset.indices.add(index + indices[k]);//[i * 6 + k] = index + indices[k]; 
			}
			
//			for(int m = 0; m < 16; ++m)
//			{
//				mset.colors.add(colors[m]); //[i * 16 + m] = colors[m];  
//			}
		}
		
		return mset;
	}
	
	private static Vector2f[] TexCoordOffsetsForBlockType(int btype, int dir)
	{
		Vector2f ret = new Vector2f(0f,0f);
		if (btype == BlockType.DIRT)
		{
			ret.y = .25f;
		}
		else if (btype == BlockType.GRASS && dir != Direction.YPOS)
		{
			ret.y = .25f;
		}
		else if (btype == BlockType.SAND)
		{
			ret.x = .25f;
			ret.y = .25f;
		}
		return new Vector2f[]{ret,ret,ret,ret};
	}
	
	/*
	 * returns an array of vertices that describe one of the 6 faces of a 3D box shaped column
	 * decide which face based on @param dir (direction) 
	 */
	private static Vector3f[] FaceVerticesForColumnAndDirection(ColumnMeshData cmd, int dir )
	{
		Vector3f[] result = new Vector3f[4];
		int axis = Direction.AxisForDirection(dir);
		
		float height = cmd.column.height;
		float posX = cmd.position.x;
		float posZ = cmd.position.z;
		float posY = cmd.position.y;
		
		if (axis == Axis.X)
		{
			result[0] = new Vector3f(posX,posY,posZ);
			result[1] = new Vector3f(posX,posY + height,posZ);
			result[2] = new Vector3f(posX,posY + height,posZ + 1);
			result[3] = new Vector3f(posX,posY,posZ + 1);
		} else if (axis == Axis.Y) {
			result[0] = new Vector3f(posX + 1,posY,posZ);
			result[1] = new Vector3f(posX,posY,posZ);
			result[2] = new Vector3f(posX,posY,posZ + 1);
			result[3] = new Vector3f(posX + 1,posY,posZ + 1);
		} else { //Z Axis
			result[0] = new Vector3f(posX + 1,posY, posZ);
			result[1] = new Vector3f(posX + 1,posY + height, posZ);
			result[2] = new Vector3f(posX,posY + height, posZ);
			result[3] = new Vector3f(posX,posY,posZ);
		}
		
		if (!Direction.IsNegDir(dir))
		{
			// This line is the same as:
			// float whatToAdd;
			// if (axis == Axis.Y)
			// { whatToAdd = height }
			// else
			// { whatToAdd = 1f; }
			float whatToAdd = axis == Axis.Y ? height : 1f;
			for(int i = 0; i < 4; ++i)
			{
				result[i] = Direction.AddToComponentAtAxis(result[i], whatToAdd, axis);
			}
			result = BlockMeshUtil.FlipQuadBySwappingComponents(result);
		}
		
		return result;
	}
	
	private static Vector3f[] FlipQuadBySwappingComponents(Vector3f[] fourVecs)
	{
		Vector3f temp1 = fourVecs[1];
		fourVecs[1] = fourVecs[2];
		fourVecs[2] = temp1; 
		Vector3f temp0 = fourVecs[0];
		fourVecs[0] = fourVecs[3];
		fourVecs[3] = temp0;
		return fourVecs;
	}
	
	private static Vector3f PositionVectorWithDirection(int axis, float over, float up, float axisPos)
	{
		if (axis == Axis.X)
		{
			return new Vector3f(axisPos, up, over);
		}
		if (axis == Axis.Y)
		{
			return new Vector3f(over, axisPos, up);
		}
		
		return new Vector3f(over, up, axisPos);
	}
	
	private static int ComponentFromCoord3AndAxis(Coord3 co, int axis)
	{
		if (axis == Axis.X)
		{
			return co.x;
		}
		if (axis == Axis.Y)
		{
			return co.y;
		}
		return co.z;
	}
	
//	private static Vector3f[] SquareVerticesForDirection(int dir)
//	{
//		//ZNEG
//		Vector3f ll = new Vector3f(0f, 0f, 0f);
//		Vector3f ul = new Vector3f(0f, 1f, 0f);
//		Vector3f ur = new Vector3f(0f, 1f, 1f);
//		Vector3f lr = new Vector3f(0f, 0f, 1f);
//		
//		return new Vector3f[]{ll,ul,ur,lr};
//	}
	
	private static int[] IndicesForDirection(int dir)
	{
//		if (Direction.IsNegDir(dir))
//		return new int[] {0,1,2, 0,1,2}; //TEST
//			return new int[] {0,2,3, 0,1,2};
		
		return new int[] {0,3,2, 0,2,1};
	}
	
	private static Vector2f[] UVsForDirection(int dir, int height)
	{
		return BlockMeshUtil.UVsForDirection(dir, height, 1);
	}
	
	private static Vector2f[] UVsForDirection(int dir, int height, int width)
	{
		if (Direction.AxisForDirection(dir) == Axis.Y) {
			height = 1; // HACK FOR NOW...
			return new Vector2f[] {new Vector2f(0,0),new Vector2f(width,0),new Vector2f(width,height),new Vector2f(0,height)};
		}
		//HACK FLIP HEIGHT AND WIDTH?? // TODO: FIGURE THIS WHOLE THING A LITTLE MORE...
		return new Vector2f[] {new Vector2f(0,0),new Vector2f(0,height),new Vector2f(width,height),new Vector2f(width,0)};
	}
	
	private static float[] ColorsForDirection(int dir)
	{
		float flipper = 1f;
		if (dir > 2)
			flipper = -1f;
		
		float[] red = new float[]{1f,0f,0f,1f};
		float[] blue = new float[]{0f,0f,1f,1f};
		float[] yellow = new float[]{1f,1f,0f,1f};
		
		float[] faceColor = red;
		if (dir == Direction.XNEG) {
			return new float[] {1f,1f,0f,1f,1f,1f,0f,1f,1f,1f,0f,1f,1f,1f,0f,1f};
//			faceColor = yellow;
		}
		else if (dir == Direction.ZNEG) {
			return new float[] {0f,0f,1f,1f,0f,0f,1f,1f,0f,0f,1f,1f,0f,0f,1f,1f};
		}
//			faceColor = blue;
		
		float[] colorArray = new float[4*4];
    	int colorIndex=0;
    	for(int i = 0; i < 4; i++)
    	{
//    		float[] color = red;
    		
		   // Red value (is increased by .2 on each next vertex here)
			colorArray[colorIndex++]=  0.5f+(.1f* i * flipper);
//		   colorArray[colorIndex++]= faceColor[i];// 0.5f+(.1f* i * flipper);
		   // Green value (is reduced by .2 on each next vertex)
//		   colorArray[colorIndex++]=faceColor[i]; //0.5f-(0.1f*i * flipper);
			colorArray[colorIndex++]= 0.5f-(0.1f*i * flipper);
		   // Blue value (remains the same in our case)
		   colorArray[colorIndex++]= 0.5f ;
		   // Alpha value (no transparency set here)
		   colorArray[colorIndex++]= 1.0f;
		}
    	
    	return colorArray;
	}
}
