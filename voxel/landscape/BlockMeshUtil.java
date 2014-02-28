package voxel.landscape;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class BlockMeshUtil 
{
	public static MeshSet MeshSetFromColumnData(ColumnMeshData columnMeshData)
	{
		MeshSet mset = new MeshSet(6 * 4);
		
		//for each of the 6 directions
		//make four verts
		//and for indices
		//add four * iterationCount to each index
		//make four UVS.
		//add to mesh Set
		for (int i = 0; i <= Direction.ZPOS; ++i) // Direction ZPOS = 5 (0 to 5 for the 6 sides of the column)
		{
			Vector3f[] verts = BlockMeshUtil.FaceVerticesForColumnAndDirection(columnMeshData, i);
			Vector2f[] uvs = BlockMeshUtil.UVsForDirection(i);
			int[]indices = BlockMeshUtil.IndicesForDirection(i);
			float[] colors = BlockMeshUtil.ColorsForDirection(i);
			
			int index = i * 4;
			for(int j = 0; j < 4; ++j)
			{
				mset.vertices[index + j] = verts[j];
				mset.uvs[index + j] = uvs[j];
			}
			
			for(int k = 0; k < 6; ++k)
			{
				// for example: if indices are 0,2,3
				// the first face has verts numbers 0,1,2,3
				// the second face has verts numbers 4,5,6,7
				// the 3rd face has verts numbers: (3rd - 1) * (verts per face (4)) + 0 (and then + 1 or + 2 or + 3)
				// in other words 2 * 4 + 0,1,2,3 or index + 0,1,2,3 or... 8,9,10,11
				mset.indices[i * 6 + k] = index + indices[k]; 
			}
			
			for(int m = 0; m < 16; ++m)
			{
				mset.colors[i * 16 + m] = colors[m];  
			}
			
		}
		
		return mset;
	}
	
	/*
	 * returns an array of vertices that describe the face of a box shaped column
	 * decide which face based on @param dir (direction) 
	 */
	private static Vector3f[] FaceVerticesForColumnAndDirection(ColumnMeshData cmd, int dir )
	{
		Vector3f[] result = new Vector3f[4];
		int axis = Direction.AxisForDirection(dir);
		
		float height = cmd.column.height;
		float posX = cmd.position.x;
		float pozZ = cmd.position.z;
		float posY = cmd.position.y;
		
		result[0] = new Vector3f(posX,posY,pozZ);
		result[1] = new Vector3f(posX,posY + height,pozZ);
		result[2] = new Vector3f(posX + 1,posY + height,pozZ);
		result[3] = new Vector3f(posX + 1,posY,pozZ);
		
		return result;
		
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
//		
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
	
	private static Vector2f[] UVsForDirection(int dir)
	{
		return new Vector2f[] {new Vector2f(0,0),new Vector2f(1,0),new Vector2f(1,1),new Vector2f(0,1)};
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
		if (dir == Direction.XNEG)
			faceColor = yellow;
		else if (dir == Direction.ZNEG)
			faceColor = blue;
		
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
