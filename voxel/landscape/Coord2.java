package voxel.landscape;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;


public class Coord2 
{
	public int x;
	public int y;
	
	public static final Coord2 one = new Coord2(1,1);
	public static final Coord2 zero = new Coord2(0,0);
	
	public Coord2(int _x, int _y)
	{
		x = _x;
		y = _y;
	}
	public Coord2(int a) { this(a,a); }
	
	public Coord2(double a, double b) { this((int) a, (int) b); }
	
	public Coord2 multy(Coord2 other) {
		return new Coord2(this.x * other.x, this.y * other.y);
	}
	public Coord2 multy(int scaleBy){
		return new Coord2(this.x * scaleBy, this.y * scaleBy);
	}
	public Coord2 multy(double scaleBy){
		return new Coord2(this.x * scaleBy, this.y * scaleBy);
	}
	public Coord2 divideBy(Coord2 other) {
		return new Coord2(this.x / other.x, this.y / other.y);
	}
	public Coord2 add(Coord2 other) {
		return new  Coord2(this.x + other.x, this.y + other.y);
	}
	public Coord2 minus(Coord2 other) {
		return new  Coord2(this.x - other.x, this.y - other.y);
	}
	public static Coord2 Max(Coord2 a, Coord2 b) {
		return new Coord2(a.x > b.x ? a.x : b.x, a.y > b.y ? a.y : b.y);
	}
	public static Coord2 Min(Coord2 a, Coord2 b) {
		return new Coord2(a.x < b.x ? a.x : b.x, a.y < b.y ? a.y : b.y);
	}
	public boolean equals(Object other) {
		if (other.getClass() != Coord2.class) return false;
		return equals((Coord2) other);
	}
	public boolean equals(Coord2 other) { return x == other.x && y == other.y; }
	
	public Vector3f toVec3XZ() {
		return new Vector3f(x,0,y);
	}
	public Vector2f toVector2f() { return new Vector2f(x,y); }
	
	public String toString() { return String.format("Coord2:x: %d y: %d", x,y); }
}
