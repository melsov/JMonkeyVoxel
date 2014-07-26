package voxel.landscape.collection;

import java.lang.reflect.Array;
import voxel.landscape.Coord3;
/*
 * List3D ported from Mr. Wishmaster's c# implementation
 */

public class List3D<T> 
{
	private volatile Coord3 min, max;
	private volatile T list[][][];
	private Class<T> type;
	
	public List3D(Class<T> _type) { this( new Coord3(0), new Coord3(0), _type); }
	
	@SuppressWarnings("unchecked")
	public List3D(Coord3 _min, Coord3 _max, Class<T> _type) {
		this.min = _min; this.max = _max;
		this.type = _type;
		Coord3 size = getSize();
		list = (T[][][]) Array.newInstance(_type, size.x, size.y, size.z);
	}
	public Coord3 getSize() {
		return max.minus(min);
	}
	public T GetInstance(Coord3 pos) {
		return GetInstance(pos.x, pos.y, pos.z);
	}
	public T GetInstance(int x, int y, int z) {
		T obj = SafeGet(x, y, z);
		if( obj == null  ) {
			try {
				obj = type.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} 
			AddOrReplace(obj, x, y, z);
		}
		return obj;
	}
	public T Get(Coord3 pos) {
		return Get(pos.x, pos.y, pos.z);
	}
	public T Get(int x, int y, int z) {
		return list[x-min.x][y-min.y][z-min.z];
	}
	
	public T SafeGet(Coord3 pos) {
		return SafeGet(pos.x, pos.y, pos.z);
	}
	public T SafeGet(int x, int y, int z) {
		if(!IndexWithinBounds(x, y, z)) return null;
		return Get(x, y, z);
	}
	
	public void AddOrReplace(T obj, Coord3 pos) {
		Coord3 newMin = Coord3.Min(min, pos);
		Coord3 newMax = Coord3.Max(max, pos.add(Coord3.One));
		if(newMin != min || newMax != max) {
			Resize(newMin, newMax);
		}
		Set(obj, pos);
	}
	public void AddOrReplace(T obj, int x, int y, int z) {
		AddOrReplace(obj, new Coord3(x, y, z));
	}
	
	// CONSIDER: optimize-able?
	@SuppressWarnings("unchecked")
	private void Resize(Coord3 newMin, Coord3 newMax) {
		Coord3 oldMin = min;
		Coord3 oldMax = max;
		T[][][] oldList = list;
		
		min = newMin;
		max = newMax;
		Coord3 size = newMax.minus(newMin); // newMin;
		list = (T[][][]) Array.newInstance(this.type, size.x, size.y, size.z);
		
		for(int x=oldMin.x; x<oldMax.x; x++) {
			for(int y=oldMin.y; y<oldMax.y; y++) {
				for(int z=oldMin.z; z<oldMax.z; z++) {
					T val = oldList[x-oldMin.x][y-oldMin.y][z-oldMin.z];
					Set(val, x, y, z);
				}
			}
		}
	}
	
	public void Set(T obj, Coord3 pos) {
		Set(obj, pos.x, pos.y, pos.z);
	}
	public void Set(T obj, int x, int y, int z) {
		list[x-min.x][y-min.y][z-min.z] = obj;
	}
	
	public boolean IndexWithinBounds(Coord3 pos) {
		return IndexWithinBounds(pos.x, pos.y, pos.z);
	}
	public boolean IndexWithinBounds(int x, int y, int z) {
		if(x<min.x  || y<min.y  || z<min.z) return false;
		if(x>=max.x || y>=max.y || z>=max.z) return false;
		return true;
	}
	
	public Coord3 GetMin() {
		return min;
	}
	public Coord3 GetMax() {
		return max;
	}
	public Coord3 GetSize() {
		return max.minus(min);
	}
	
	public int GetMinX() {
		return min.x;
	}
	public int GetMinY() {
		return min.y;
	}
	public int GetMinZ() {
		return min.z;
	}
	
	public int GetMaxX() {
		return max.x;
	}
	public int GetMaxY() {
		return max.y;
	}
	public int GetMaxZ() {
		return max.z;
	}
	
}
