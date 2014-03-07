package voxel.landscape;

import simplex.noise.SimplexNoise;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.System.out;

/*
 * This class's job is to tell
 * whoever wants to know
 * what terrain height and block type exists
 * at a given x and z
 * 
 * it has two ways of doing this:
 * "SimplexNoiseMode" : do some math to generate 'smooth' random numbers
 * "ImageMode" : read the pixels in an image and base the terrain data off of them.
 */


public class TerrainDataProvider implements IHeightDataProvider, IBlockTypeDataProvider 
{
	public enum Mode {
		SimplexNoiseMode, ImageMode
	}
	
	private Mode mode = Mode.SimplexNoiseMode;
	private BufferedImage buffIm;
	Coord2 buffiDims;
//	ColorModel
	
	private static final int ARGB_POS_MAX = (256*256*256);
	private static final int ARGB_POS_ONE_CHANNEL_MAX = 256;
	
	public TerrainDataProvider() 
	{
		
	}
	
	public TerrainDataProvider(TerrainDataProvider.Mode _mode)
	{
		mode = _mode;
		if (mode== Mode.ImageMode)
		{
			String heightMapSrc = "inputTexturesMP/heightMapTex.png";
			buffIm = getBufferedImage(heightMapSrc);
			// TODO: sep ims for heightmap and btype map
			buffiDims = new Coord2(buffIm.getWidth(), buffIm.getHeight() );
			
			
		}
	}
	
	@Override
	public double heightAt(double xin, double yin) 
	{
		if (mode == Mode.ImageMode)
		{
			return readImageAt(xin,yin);
		}
		
		return SimplexNoise.noise(xin, yin);
	}
	
	private BufferedImage getBufferedImage(String src)
	{
		File imFile = new File(src);

		BufferedImage buffi = null;
		try {
			buffi = ImageIO.read(imFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffi;
	}
	
	
	private double readImageAt(double xin, double yin)
	{
		
		int rgb = getRGB(xin, yin);
		
		//result is neg for some reason when reading pngs...oh well
		int alpha = (rgb >> 24) & 0xFF;

		return (double)(alpha/((double)ARGB_POS_ONE_CHANNEL_MAX * 3) * 2 - 1);
	}
	
	private int getRGB(double xin, double yin)
	{
		int x = (int) Math.abs(xin), y = (int) Math.abs(yin);
		return buffIm.getRGB(x, y) + ARGB_POS_MAX;
	}

	@Override
	public int blockTypeAt(double xin, double yin) 
	{
		if (mode == Mode.ImageMode)
		{

			int rgb = getRGB(xin,yin);
			
			int green = (rgb >> 8) & 0xFF;

			if (green % 255 > 122) {
				return BlockType.GRASS;
			} 
			
			int red = (rgb >> 16) & 0xFF;
			if (red % 255 > 122) {
				return BlockType.DIRT;
			}
			
			return BlockType.SAND;
		}
		return BlockType.SAND;
	}

}
