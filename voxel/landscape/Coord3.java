package voxel.landscape;

import com.jme3.math.Vector3f;

public class Coord3 
{
	public int x,y,z;
	
	public static final Coord3 xpos = new Coord3(1,0,0);
	public static final Coord3 ypos = new Coord3(0,1,0);
	public static final Coord3 zpos = new Coord3(0,0,1);
	
	public static final Coord3 xneg = new Coord3(-1,0,0);
	public static final Coord3 yneg = new Coord3(0,-1,0);
	public static final Coord3 zneg = new Coord3(0,0,-1);
	
	public static final Coord3 right = xpos;
	public static final Coord3 up = ypos;
	public static final Coord3 forward = zpos;
	
	public Coord3(int a) { this(a,a,a); }
	
	public Coord3(int _x, int _y, int _z)
	{
		x = _x; y = _y; z = _z;
	}
	public Coord3(double _x, double _y, double _z)
	{
		x = (int) _x; y = (int) _y; z = (int) _z;
	}
	
	public Coord3 multy(Coord3 other)
	{
		return new Coord3(this.x * other.x, this.y * other.y, this.z * other.z);
	}
	public Coord3 multy(Vector3f other)
	{
		return new Coord3(this.x * other.x, this.y * other.y, this.z * other.z);
	}
	public Coord3 multy(int i)
	{
		return new Coord3(this.x * i, this.y * i, this.z * i);
	}
	public Coord3 divideBy(Coord3 other) {
		return new Coord3(x/other.x, y/other.y, z/other.z);
	}
	public Coord3 divideBy(double other) {
		return new Coord3(x/other, y/other, z/other);
	}
	public Coord3 add(Coord3 other)
	{
		return new Coord3(this.x + other.x, this.y + other.y, this.z + other.z);		
	}
	public Coord3 add(int i)
	{
		return new Coord3(this.x + i, this.y + i, this.z + i);
	}
	public Coord3 minus (Coord3 other)
	{
		return new Coord3(this.x - other.x, this.y - other.y, this.z - other.z);		
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
	public static Coord3 Min (Coord3 a, Coord3 b) {
		return new Coord3(a.x < b.x ? a.x : b.x , a.y < b.y ? a.y : b.y , a.z < b.z ? a.z : b.z);
	}
	public static Coord3 Max (Coord3 a, Coord3 b) {
		return new Coord3(a.x > b.x ? a.x : b.x , a.y > b.y ? a.y : b.y , a.z > b.z ? a.z : b.z);
	}
	public boolean greaterThan(Coord3 other) {
		return x > other.x && y > other.y && z > other.z;
	}
	public boolean lessThan(Coord3 other) {
		return x < other.x && y < other.y && z < other.z;
	}
	public double distanceSquared() {
		return x*x + y*y;
	}
	public Coord3 sign() {
		return new Coord3(Math.signum(x), Math.signum(y), Math.signum(z));
	}
	public static Coord3 Zero = new Coord3(0,0,0); 
	public static Coord3 One = new Coord3(1,1,1); 
	
	public Vector3f toVector3()
	{
		return new Vector3f(this.x, this.y, this.z);
	}
	public static Coord3 FromVector3f(Vector3f v) { return new Coord3(v.x, v.y, v.z); }
	
    public static Coord3 GreatestDirectionCoord(Vector3f dir) {
    	Coord3 res = dir.x < 0 ? Coord3.xneg : Coord3.xpos;
    	if (Math.abs(dir.y) > Math.abs(dir.x) && Math.abs(dir.y) > Math.abs(dir.z)) {
    		res = dir.y < 0 ? Coord3.yneg : Coord3.ypos;
    	}
    	if (Math.abs(dir.z) > Math.abs(dir.x)) {
    		res = dir.z < 0 ? Coord3.zneg : Coord3.zpos;
    	}
    	return res;
    }
    
	@Override
	public String toString() { return String.format("Coord3 x: %d, y: %d, z: %d", x,y,z); }
}
