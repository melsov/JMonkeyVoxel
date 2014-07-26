package voxel.landscape.chunkbuild;

import voxel.landscape.collection.ColumnMap;
import voxel.landscape.map.TerrainMap;
import voxel.landscape.map.light.ChunkSunLightComputer;

public class AsyncGenerateColumnData extends ResponsiveRunnable
{

	private int x,z;
	private TerrainMap terrainMap;
	private ColumnMap columnMap;
	public AsyncGenerateColumnData(final TerrainMap _terrainMap, final ColumnMap _columnMap, int xx, int zz) {
		columnMap = _columnMap;
		x = xx; z = zz;
		terrainMap = _terrainMap;
	}
	@Override
	public void doRun() {
		terrainMap.generateNoiseForChunkColumn(x,z);
		ChunkSunLightComputer.ComputeRays(terrainMap, x, z);
		ChunkSunLightComputer.Scatter(terrainMap, columnMap, x, z); //TEST WANT
	}
	
	public int getX() { return x; }
	public int getZ() { return z; }
	
}