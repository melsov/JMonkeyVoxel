package voxel.landscape.collection;

import java.lang.reflect.Array;

import voxel.landscape.Coord2;
import voxel.landscape.Coord3;

public class List2D<T> {
	
	private volatile T[][] list;
	private volatile Coord2 min, max;
	private Class<T> type;
	
	public List2D(Class<T> _type) { this(Coord2.zero, Coord2.zero, _type); }
	
	@SuppressWarnings("unchecked")
	public List2D(Coord2 _min, Coord2 _max, Class<T> _type) {
		min = _min; max = _max;
		type = _type;
		Coord2 size = GetSize();
		list = (T[][]) Array.newInstance(_type, size.x, size.y);
	}
	public Coord2 GetSize() {
		return max.minus(min);	
	}
	
	public void Set(T obj, Coord2 pos) {
		Set(obj, pos.x, pos.y);
	}
	public void Set(T obj, int x, int y) {
		list[x-min.x][y-min.y] = obj;
	}
	
	public T GetInstance(Coord2 pos) {
		return GetInstance(pos.x, pos.y);
	}
	@SuppressWarnings("unchecked")
	public T GetInstance(int x, int y) {
		T obj = SafeGet(x, y);
		if( obj == null ) {
			try {
				obj = type.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			AddOrReplace(obj, x, y);
		}
		return obj;
	}
	
	public T Get(Coord2 pos) {
		return Get(pos.x, pos.y);
	}
	public T Get(int x, int y) {
		return list[x-min.x][y-min.y];
	}
	
	public T SafeGet(Coord2 pos) {
		return SafeGet(pos.x, pos.y);
	}
	public T SafeGet(int x, int y) {
		if(!IsCorrectIndex(x, y)) return null;
		return list[x-min.x][y-min.y];
	}
	
	public void AddOrReplace(T obj, Coord2 pos) {
		Coord2 newMin = Coord2.Min(min, pos);
		Coord2 newMax = Coord2.Max(max, pos.add(Coord2.one));
		if(!newMin.equals(min) || !newMax.equals(max)) {
			Resize(newMin, newMax);
		}
		Set(obj, pos);
	}
	public void AddOrReplace(T obj, int x, int y) {
		AddOrReplace(obj, new Coord2(x, y));
	}
	@SuppressWarnings("unchecked")
	private void Resize(Coord2 newMin, Coord2 newMax) {
		Coord2 oldMin = min;
		Coord2 oldMax = max;
		T[][] oldList = list;
		
		min = newMin;
		max = newMax;
		Coord2 size = newMax.minus(newMin);
		list =(T[][]) Array.newInstance(type, size.x, size.y);
		for(int x=oldMin.x; x<oldMax.x; x++) {
			for(int y=oldMin.y; y<oldMax.y; y++) {
				T val = oldList[x-oldMin.x][y-oldMin.y];
				Set(val, x, y);
			}
		}
	}
	
	public boolean IsCorrectIndex(Coord2 pos) {
		return IsCorrectIndex(pos.x, pos.y);
	}
	private boolean IsCorrectIndex(int x, int y) {
		if(x<min.x  || y<min.y) return false;
		if(x>=max.x || y>=max.y) return false;
		return true;
	}
	
	
}