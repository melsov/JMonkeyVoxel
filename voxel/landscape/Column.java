package voxel.landscape;

public class Column 
{
	public int start;
	public int height;
	
	public Column(int _start, int _height)
	{
		start = _start;
		height = _height;
	}
	
	public int extent() 
	{
		return start + height;
	}
}
