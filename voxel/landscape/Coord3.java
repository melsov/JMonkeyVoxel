package voxel.landscape;

import com.jme3.math.Vector3f;

public class Coord3 
{
	public int x,y,z;
	
	public Coord3(int _x, int _y, int _z)
	{
		x = _x; y = _y; z = _z;
	}
	
	public Coord3 multy(Coord3 other)
	{
		return new Coord3(this.x * other.x, this.y * other.y, this.z * other.z);
	}
	
	public Coord3 multy(int i)
	{
		return new Coord3(this.x * i, this.y * i, this.z * i);
	}
	
	public Coord3 add(Coord3 other)
	{
		return new Coord3(this.x + other.x, this.y + other.y, this.z + other.z);		
	}
	
	public Coord3 add(int i)
	{
		return new Coord3(this.x + i, this.y + i, this.z + i);
	}
	
	public Coord3 copy() {
		return new Coord3(this.x, this.y, this.z);
	}
	
	public static Coord3 ZeroFlipsToOneNonZeroFlipsToZero(Coord3 coord) {
		Coord3 co = coord.copy(); 
		co.x = co.x != 0 ?  0 : 1;
		co.y = co.y != 0 ?  0 : 1;
		co.z = co.z != 0 ?  0 : 1;
		return co;
	}
	
	public Vector3f toVector3()
	{
		return new Vector3f(this.x, this.y, this.z);
	}
}
