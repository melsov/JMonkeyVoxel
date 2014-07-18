package voxel.landscape.collection;

import voxel.landscape.Coord3;

public class ColumnMap {

	private List2D<ColumnChunk> columns = new List2D<ColumnChunk>(ColumnChunk.class);
	
	public void SetBuilt(int x, int z) {
		GetColumnChunk(x, z).built = true;
	}
	
	public boolean IsBuilt(int x, int z) {
		return GetColumnChunk(x, z).built;
	}
	
	
	public Coord3 GetClosestEmptyColumn(int cx, int cz, int rad) {
		Coord3 center = new Coord3(cx, 0, cz);
		Coord3 closest = null;
		for(int z=cz-rad; z<=cz+rad; z++) {
			for(int x=cx-rad; x<=cx+rad; x++) {
				Coord3 current = new Coord3(x, 0, z);
				int dis = (int) center.minus(current).distanceSquared();
				if(dis > rad*rad) continue;
				if( IsBuilt(x, z) ) continue;
				if(closest == null) {
					closest = current;
				} else {
					int oldDis = (int) center.minus(closest).distanceSquared(); 
					if(dis < oldDis) closest = current;
				}
			}
		}
		return closest;
	}
	
	
	private ColumnChunk GetColumnChunk(int x, int z) {
		return columns.GetInstance(x, z);
	}
	
	
//	public class ColumnChunk {
//		public boolean built = false;
//	}
	
}
