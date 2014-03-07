package voxel.landscape;

import com.jme3.math.Vector3f;


public class Coord2 
{
	public int x;
	public int y;
	
	public Coord2(int _x, int _y)
	{
		x = _x;
		y = _y;
	}
	
	public Coord2 multy(Coord2 other)
	{
		return new Coord2(this.x * other.x, this.y * other.y);
	}
	
	public Coord2 multy(int scaleBy)
	{
		return new Coord2(this.x * scaleBy, this.y * scaleBy);
	}
	
	public Vector3f toVec3XZ()
	{
		return new Vector3f(x,0,y);
	}
}
