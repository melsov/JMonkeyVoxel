package voxel.landscape.noise;

import voxel.landscape.Coord3;

public interface IBlockDataProvider 
{
	public abstract int blockDataAtPosition(int xin, int yin, int zin);
	public abstract int blockDataAtPosition(Coord3 woco);
}
