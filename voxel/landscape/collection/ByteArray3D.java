package voxel.landscape.collection;

import voxel.landscape.Coord3;

public class ByteArray3D 
{
	private Coord3 size;
	private byte list[][][];
	
	public ByteArray3D(Coord3 _size) {
		this.size = _size;
		list = new byte[size.x][size.y][size.z];
	}
	public Coord3 getSize() {
		return size;
	}

	public byte Get(Coord3 pos) {
		return Get(pos.x, pos.y, pos.z);
	}
	public byte Get(int x, int y, int z) {
		return list[z][y][x];
	}
	
	public byte SafeGet(Coord3 pos) {
		return SafeGet(pos.x, pos.y, pos.z);
	}
	public byte SafeGet(int x, int y, int z) {
		if(!IndexWithinBounds(x, y, z))
			try {
				throw new Exception ("byte array out of bounds: x " + x + " y: " + y + " z: " + z);
			} catch (Exception e) {
				e.printStackTrace();
			}
		return Get(x, y, z);
	}
	
	public void Set(byte obj, Coord3 pos) {
		Set(obj, pos.x, pos.y, pos.z);
	}
	public void Set(byte obj, int x, int y, int z) {
		list[z][y][x] = obj;
	}
	
	public boolean IndexWithinBounds(Coord3 pos) {
		return IndexWithinBounds(pos.x, pos.y, pos.z);
	}
	public boolean IndexWithinBounds(int x, int y, int z) {
		if(x>=size.x || y>=size.y || z>=size.z) return false;
		return true;
	}

	public Coord3 GetSize() {
		return size; 
	}

	public int GetSizeX() {
		return size.x;
	}
	public int GetSizeY() {
		return size.y;
	}
	public int GetSizeZ() {
		return size.z;
	}
	
}
