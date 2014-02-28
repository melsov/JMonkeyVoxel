package voxel.landscape;

public class Axis 
{
	 public static final int X = 0,Y = 1,Z = 2;
	 
	 public static Coord3 PosCoordForAxis(int axis)
	 {
		 if (axis == Axis.X)
			 return new Coord3(1,0,0);
		 if (axis == Axis.Y)
			 return new Coord3(0,1,0);
		 return new Coord3(0,0,1);
	 }
	 
	 public static Coord3 LateralCoordForAxis(int axis)
	 {
		 if (axis == Axis.X)
			 return new Coord3(0,0,1);
		 if (axis == Axis.Y)
			 return new Coord3(1,0,0);
		 return new Coord3(1,0,0);
	 }
	 
	 public static Coord3 UpCoordForAxis(int axis)
	 {
		 if (axis == Axis.X || axis == Axis.Z)
			 return new Coord3(0,1,0);
		 return new Coord3(0,0,1); 
	 }

	 
}
