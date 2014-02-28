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

		//make the origin always be the lower left corner if we are looking at the face
		Coord3 oco = cmd.position;
		Coord3 over = Axis.LateralCoordForAxis(axis);
		Coord3 up = Axis.UpCoordForAxis(axis);
		Coord3 faceDirectionCoord = Direction.DirectionCoordForDirection(dir);
		
//		if (dir == Direction.XNEG)
//		{
//			oco = oco.add(over);
//		}
//		else if (dir == Direction.XPOS)
//		{
//			oco = oco.add(faceDirectionCoord);
//		}
//		else if (dir == Direction.ZPOS)
//		{
//			oco = oco.add(faceDirectionCoord);
//			oco = oco.add(over);
//		}
		
		Vector3f origin = oco.toVector3();
		
		
		if (axis != Axis.Y)
			up = up.multy(cmd.column.height);
		
		//if dir is positive
		//nudge the origin over by one in that dir 
		//(or if y, by one * height of column)
		if (!Direction.IsNegDir(dir))
		{
			int nudgeBy = 1;
			if (axis == Axis.Y)
			{
				nudgeBy = cmd.column.height;
			}
			origin = origin.add(faceDirectionCoord.multy(nudgeBy).toVector3());
		}
		
		float upv = 1f;
		if (axis != Axis.Y)
			upv = (float)(upv * (float)cmd.column.height);
		
		float axisPos = (float) BlockMeshUtil.ComponentFromCoord3AndAxis(cmd.position, axis);
		if (dir == Direction.YPOS)
			axisPos += (float)cmd.column.height;
		else if (!Direction.IsNegDir(dir))
			axisPos += 1f;
		
		Vector3f lowerLeft = BlockMeshUtil.PositionVectorWithDirection(axis, 0f, 0f, axisPos);
		Vector3f upperLeft = BlockMeshUtil.PositionVectorWithDirection(axis, 0f, upv, axisPos);
		Vector3f upperRight = BlockMeshUtil.PositionVectorWithDirection(axis, 1f, upv, axisPos);
		Vector3f lowerRight = BlockMeshUtil.PositionVectorWithDirection(axis, 1f, 0f, axisPos);
		
		if (Direction.IsNegDir(dir))
			return new Vector3f[] {lowerLeft, upperLeft, upperRight, lowerRight}; // good for neg...
		
		return new Vector3f[] {lowerRight, upperRight, upperLeft, lowerLeft}; // good for pos...
		
//		result[0] = origin;
//		result[1] = origin.add(over.toVector3());
//		result[2] = origin.add(over.add(up).toVector3());
//		result[3] = origin.add(up.toVector3());
//		
//		return result;
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
