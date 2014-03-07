package voxel.landscape;

import com.jme3.math.Vector3f;

public class Direction {
	public static final int XNEG = 0, XPOS = 1, YNEG = 2, YPOS = 3, ZNEG=4, ZPOS=5;
	
	public static int OppositeDirection(int dir) 
	{
		if (dir % 2 == 0)
		{
			return dir + 1;
		}
		return dir - 1;
	}
	
	public static Coord3 DirectionCoordForDirection(int dir)
	{
		Coord3 result = Axis.PosCoordForAxis(Direction.AxisForDirection(dir));
		
		if (Direction.IsNegDir(dir))
			result = result.multy(-1);
		
		return result;
	}
	
	public static boolean IsNegDir(int dir)
	{
		return dir % 2 == 0;
	}
	
	public static Vector3f AddToComponentAtAxis(Vector3f vec, float whatToAdd, int axis)
	{
		if (axis == Axis.X)
			vec.x += whatToAdd;
		else if (axis == Axis.Y)
			vec.y += whatToAdd;
		else
			vec.z += whatToAdd;
		return vec;
	}
	
	
	public static int AxisForDirection(int dir)
	{
		if (dir <= Direction.XPOS)
		{
			return Axis.X;
		}
		if (dir <= Direction.YPOS)
		{
			return Axis.Y;
		}

		return Axis.Z;

	}
}
