package voxel.landscape.noise;

import voxel.landscape.Coord3;

public interface IBlockDataProvider 
{
	public abstract int lookupOrCreateBlock(int xin, int yin, int zin);
	public abstract int lookupOrCreateBlock(Coord3 woco);
}
