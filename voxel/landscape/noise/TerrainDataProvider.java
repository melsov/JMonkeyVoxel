package voxel.landscape.noise;

import simplex.noise.SimplexNoise;
import voxel.landscape.BlockType;
import voxel.landscape.Coord2;
import voxel.landscape.map.TerrainMap;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

//import com.sudoplay.joise.examples.Canvas;
import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBias;
import com.sudoplay.joise.module.ModuleCache;
import com.sudoplay.joise.module.ModuleCombiner;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleGradient;
import com.sudoplay.joise.module.ModuleScaleDomain;
import com.sudoplay.joise.module.ModuleScaleOffset;
import com.sudoplay.joise.module.ModuleSelect;
import com.sudoplay.joise.module.ModuleTranslateDomain;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleCombiner.CombinerType;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

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


//public class TerrainDataProvider implements IBlockDataProvider, IBlockTypeDataProvider
public class TerrainDataProvider
{
	public enum Mode {
		SimplexNoiseMode, ImageMode
	}
	
	private Mode mode = Mode.SimplexNoiseMode;
	private BufferedImage buffIm;
	Coord2 buffiDims;

	Module noiseModule;
	public static final double WORLD_TO_NOISE_SCALE = TerrainMap.GetWorldHeightInBlocks() * 1.2;
	
	private static final int ARGB_POS_MAX = (256*256*256);
	private static final int ARGB_POS_ONE_CHANNEL_MAX = 256;
	
	public TerrainDataProvider() 
	{
		this(TerrainDataProvider.Mode.SimplexNoiseMode); // misnomer
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
		} else {
			setupModule(-21234);
		}
	}
	
	public int getBlockDataAtPosition(int xin , int yin, int zin) 
	{
		if (mode == Mode.ImageMode)
		{
			return 4; // (int) readImageAt(xin, yin);
		}
		double x = xin/WORLD_TO_NOISE_SCALE;
		double y = (WORLD_TO_NOISE_SCALE - yin)/WORLD_TO_NOISE_SCALE; //flip y
		double z = zin/WORLD_TO_NOISE_SCALE;
		double r = noiseModule.get(x, y, z);
		return r < 0.001 ? BlockType.AIR.ordinal() :  (int) r;
		
//		return SimplexNoise.noise(xin, yin);
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


	public byte getBlockTypeAt(double xin, double yin) 
	{
		if (xin == 4 && yin == 4) return (byte) BlockType.DIRT.ordinal();
		return (byte)BlockType.GRASS.ordinal();
//		if (xin > 0 && xin < 15) return (byte) BlockType.GRASS.ordinal();
//		if (yin > 14) return (byte) BlockType.DIRT.ordinal();
//		return (byte)BlockType.SAND.ordinal();
//		if (mode == Mode.ImageMode)
//		{
//			int rgb = getRGB(xin,yin);
//			
//			int green = (rgb >> 8) & 0xFF;
//
//			if (green % 255 > 122) {
//				return BlockType.GRASS;
//			}
//			
//			int red = (rgb >> 16) & 0xFF;
//			if (red % 255 > 122) {
//				return BlockType.DIRT;
//			}
//			
//			return BlockType.SAND;
//		}
//		return BlockType.SAND;
	}
	
	private void setupModulePrev(long seed) 
	{
	    // ========================================================================
	    // = Joise module chain
	    // ========================================================================

	    /*
	     * ground_gradient
	     */

	    // ground_gradient
	    ModuleGradient groundGradient = new ModuleGradient();
	    groundGradient.setGradient(0, 0, 0, 1);

	    /*
	     * lowland
	     */

	    // lowland_shape_fractal
	    ModuleFractal lowlandShapeFractal = new ModuleFractal(FractalType.BILLOW, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    lowlandShapeFractal.setNumOctaves(2);
	    lowlandShapeFractal.setFrequency(0.25);
	    lowlandShapeFractal.setSeed(seed);

	    // lowland_autocorrect
	    ModuleAutoCorrect lowlandAutoCorrect = new ModuleAutoCorrect(0, 1);
	    lowlandAutoCorrect.setSource(lowlandShapeFractal);
	    lowlandAutoCorrect.calculate();

	    // lowland_scale
	    ModuleScaleOffset lowlandScale = new ModuleScaleOffset();
	    lowlandScale.setScale(0.125);
	    lowlandScale.setOffset(-0.45);
	    lowlandScale.setSource(lowlandAutoCorrect);

	    // lowland_y_scale
	    ModuleScaleDomain lowlandYScale = new ModuleScaleDomain();
	    lowlandYScale.setScaleY(0);
	    lowlandYScale.setSource(lowlandScale);

	    // lowland_terrain
	    ModuleTranslateDomain lowlandTerrain = new ModuleTranslateDomain();
	    lowlandTerrain.setAxisYSource(lowlandYScale);
	    lowlandTerrain.setSource(groundGradient);

	    /*
	     * highland
	     */

	    // highland_shape_fractal
	    ModuleFractal highlandShapeFractal = new ModuleFractal(FractalType.FBM, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    highlandShapeFractal.setNumOctaves(4);
	    highlandShapeFractal.setFrequency(2);
	    highlandShapeFractal.setSeed(seed);

	    // highland_autocorrect
	    ModuleAutoCorrect highlandAutoCorrect = new ModuleAutoCorrect(-1, 1);
	    highlandAutoCorrect.setSource(highlandShapeFractal);
	    highlandAutoCorrect.calculate();

	    // highland_scale
	    ModuleScaleOffset highlandScale = new ModuleScaleOffset();
	    highlandScale.setScale(0.25);
	    highlandScale.setOffset(0);
	    highlandScale.setSource(highlandAutoCorrect);

	    // highland_y_scale
	    ModuleScaleDomain highlandYScale = new ModuleScaleDomain();
	    highlandYScale.setScaleY(0);
	    highlandYScale.setSource(highlandScale);

	    // highland_terrain
	    ModuleTranslateDomain highlandTerrain = new ModuleTranslateDomain();
	    highlandTerrain.setAxisYSource(highlandYScale);
	    highlandTerrain.setSource(groundGradient);

	    /*
	     * mountain
	     */

	    // mountain_shape_fractal
	    ModuleFractal mountainShapeFractal = new ModuleFractal(FractalType.RIDGEMULTI, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    mountainShapeFractal.setNumOctaves(8);
	    mountainShapeFractal.setFrequency(1);
	    mountainShapeFractal.setSeed(seed);

	    // mountain_autocorrect
	    ModuleAutoCorrect mountainAutoCorrect = new ModuleAutoCorrect(-1, 1);
	    mountainAutoCorrect.setSource(mountainShapeFractal);
	    mountainAutoCorrect.calculate();

	    // mountain_scale
	    ModuleScaleOffset mountainScale = new ModuleScaleOffset();
	    mountainScale.setScale(0.45);
	    mountainScale.setOffset(0.15);
	    mountainScale.setSource(mountainAutoCorrect);

	    // mountain_y_scale
	    ModuleScaleDomain mountainYScale = new ModuleScaleDomain();
	    mountainYScale.setScaleY(0.1);
	    mountainYScale.setSource(mountainScale);

	    // mountain_terrain
	    ModuleTranslateDomain mountainTerrain = new ModuleTranslateDomain();
	    mountainTerrain.setAxisYSource(mountainYScale);
	    mountainTerrain.setSource(groundGradient);

	    /*
	     * terrain
	     */

	    // terrain_type_fractal
	    ModuleFractal terrainTypeFractal = new ModuleFractal(FractalType.FBM, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    terrainTypeFractal.setNumOctaves(3);
	    terrainTypeFractal.setFrequency(0.125);
	    terrainTypeFractal.setSeed(seed);

	    // terrain_autocorrect
	    ModuleAutoCorrect terrainAutoCorrect = new ModuleAutoCorrect(0, 1);
	    terrainAutoCorrect.setSource(terrainTypeFractal);
	    terrainAutoCorrect.calculate();

	    // terrain_type_y_scale
	    ModuleScaleDomain terrainTypeYScale = new ModuleScaleDomain();
	    terrainTypeYScale.setScaleY(0);
	    terrainTypeYScale.setSource(terrainAutoCorrect);

	    // terrain_type_cache
	    ModuleCache terrainTypeCache = new ModuleCache();
	    terrainTypeCache.setSource(terrainTypeYScale);

	    // highland_mountain_select
	    ModuleSelect highlandMountainSelect = new ModuleSelect();
	    highlandMountainSelect.setLowSource(highlandTerrain);
	    highlandMountainSelect.setHighSource(mountainTerrain);
	    highlandMountainSelect.setControlSource(terrainTypeCache);
	    highlandMountainSelect.setThreshold(0.65);
	    highlandMountainSelect.setFalloff(0.2);

	    // highland_lowland_select
	    ModuleSelect highlandLowlandSelect = new ModuleSelect();
	    highlandLowlandSelect.setLowSource(lowlandTerrain);
	    highlandLowlandSelect.setHighSource(highlandMountainSelect);
	    highlandLowlandSelect.setControlSource(terrainTypeCache);
	    highlandLowlandSelect.setThreshold(0.25);
	    highlandLowlandSelect.setFalloff(0.15);

	    // highland_lowland_select_cache
	    ModuleCache highlandLowlandSelectCache = new ModuleCache();
	    highlandLowlandSelectCache.setSource(highlandLowlandSelect);

	    // ground_select
	    ModuleSelect groundSelect = new ModuleSelect();
	    groundSelect.setLowSource(0);
	    groundSelect.setHighSource(1);
	    groundSelect.setThreshold(0.5);
	    groundSelect.setControlSource(highlandLowlandSelectCache);

	    /*
	     * cave
	     */

	    // cave_shape
	    ModuleFractal caveShape = new ModuleFractal(FractalType.RIDGEMULTI, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    caveShape.setNumOctaves(1);
	    caveShape.setFrequency(8);
	    caveShape.setSeed(seed);

	    // cave_attenuate_bias
	    ModuleBias caveAttenuateBias = new ModuleBias(0.825);
	    caveAttenuateBias.setSource(highlandLowlandSelectCache);

	    // cave_shape_attenuate
	    ModuleCombiner caveShapeAttenuate = new ModuleCombiner(CombinerType.MULT);
	    caveShapeAttenuate.setSource(0, caveShape);
	    caveShapeAttenuate.setSource(1, caveAttenuateBias);

	    // cave_perturb_fractal
	    ModuleFractal cavePerturbFractal = new ModuleFractal(FractalType.FBM, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    cavePerturbFractal.setNumOctaves(6);
	    cavePerturbFractal.setFrequency(3);
	    cavePerturbFractal.setSeed(seed);

	    // cave_perturb_scale
	    ModuleScaleOffset cavePerturbScale = new ModuleScaleOffset();
	    cavePerturbScale.setScale(0.25);
	    cavePerturbScale.setOffset(0);
	    cavePerturbScale.setSource(cavePerturbFractal);

	    // cave_perturb
	    ModuleTranslateDomain cavePerturb = new ModuleTranslateDomain();
	    cavePerturb.setAxisXSource(cavePerturbScale);
	    cavePerturb.setSource(caveShapeAttenuate);

	    // cave_select
	    ModuleSelect caveSelect = new ModuleSelect();
	    caveSelect.setLowSource(1);
	    caveSelect.setHighSource(0);
	    caveSelect.setControlSource(cavePerturb);
	    caveSelect.setThreshold(0.8);
	    caveSelect.setFalloff(0);
 

	    /*
	     * final
	     */

	    // ground_cave_multiply
	    ModuleCombiner groundCaveMultiply = new ModuleCombiner(CombinerType.MULT);
	    groundCaveMultiply.setSource(0, caveSelect);
	    groundCaveMultiply.setSource(1, groundSelect);
	    /*
	     * Draw it
	     */

	    noiseModule = groundCaveMultiply;

	  }

	private void setupModule(long seed) {
	    // ========================================================================
	    // = Based on Joise module chain Example 2
	    // ========================================================================
	    
	    /*
	     * ground_gradient
	     */
	    ModuleGradient groundGradient = new ModuleGradient();
	    groundGradient.setGradient(0, 0, 0, 1);

	    /*
	     * lowland
	     */
	    // lowland_shape_fractal
	    ModuleFractal lowlandShapeFractal = new ModuleFractal(FractalType.BILLOW, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    lowlandShapeFractal.setNumOctaves(2);
	    lowlandShapeFractal.setFrequency(0.25);
	    lowlandShapeFractal.setSeed(seed);

	    // lowland_autocorrect
	    ModuleAutoCorrect lowlandAutoCorrect = new ModuleAutoCorrect(0, 1);
	    lowlandAutoCorrect.setSource(lowlandShapeFractal);
	    lowlandAutoCorrect.calculate();
	    
	    // lowland_scale
	    ModuleScaleOffset lowlandScale = new ModuleScaleOffset();
	    lowlandScale.setScale(0.125);
	    lowlandScale.setOffset(-0.45);
	    lowlandScale.setSource(lowlandAutoCorrect);
	    
	    // lowland_y_scale
	    ModuleScaleDomain lowlandYScale = new ModuleScaleDomain();
	    lowlandYScale.setScaleY(0);
	    lowlandYScale.setSource(lowlandScale);

	    // lowland_terrain
	    ModuleTranslateDomain lowlandTerrain = new ModuleTranslateDomain();
	    lowlandTerrain.setAxisYSource(lowlandYScale);
	    lowlandTerrain.setSource(groundGradient);

	    /*
	     * highland
	     */

	    // highland_shape_fractal
	    ModuleFractal highlandShapeFractal = new ModuleFractal(FractalType.FBM, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    highlandShapeFractal.setNumOctaves(4);
	    highlandShapeFractal.setFrequency(2);
	    highlandShapeFractal.setSeed(seed);

	    // highland_autocorrect
	    ModuleAutoCorrect highlandAutoCorrect = new ModuleAutoCorrect(-1, 1);
	    highlandAutoCorrect.setSource(highlandShapeFractal);
	    highlandAutoCorrect.calculate();

	    // highland_scale
	    ModuleScaleOffset highlandScale = new ModuleScaleOffset();
	    highlandScale.setScale(0.25);
	    highlandScale.setOffset(0);
	    highlandScale.setSource(highlandAutoCorrect);

	    // highland_y_scale
	    ModuleScaleDomain highlandYScale = new ModuleScaleDomain();
	    highlandYScale.setScaleY(0);
	    highlandYScale.setSource(highlandScale);

	    // highland_terrain
	    ModuleTranslateDomain highlandTerrain = new ModuleTranslateDomain();
	    highlandTerrain.setAxisYSource(highlandYScale);
	    highlandTerrain.setSource(groundGradient);
	    
	    /*
	     * mountain
	     */

	    // mountain_shape_fractal
	    ModuleFractal mountainShapeFractal = new ModuleFractal(FractalType.RIDGEMULTI, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    mountainShapeFractal.setNumOctaves(8);
	    mountainShapeFractal.setFrequency(1);
	    mountainShapeFractal.setSeed(seed);
	    /*
	     * MMP: cache for bedrock
	     */
	    ModuleCache mp_mountainCache = new ModuleCache();
	    mp_mountainCache.setSource(mountainShapeFractal);

	    // mountain_autocorrect
	    ModuleAutoCorrect mountainAutoCorrect = new ModuleAutoCorrect(-1, 1);
	    mountainAutoCorrect.setSource(mountainShapeFractal);
	    mountainAutoCorrect.setSource(mp_mountainCache);
	    mountainAutoCorrect.calculate();
	    

	    // mountain_scale
	    ModuleScaleOffset mountainScale = new ModuleScaleOffset();
	    mountainScale.setScale(0.45);
	    mountainScale.setOffset(0.15);
	    mountainScale.setSource(mountainAutoCorrect);

	    // mountain_y_scale
	    ModuleScaleDomain mountainYScale = new ModuleScaleDomain();
	    mountainYScale.setScaleY(0.1);
	    mountainYScale.setSource(mountainScale);

	    // mountain_terrain
	    ModuleTranslateDomain mountainTerrain = new ModuleTranslateDomain();
	    mountainTerrain.setAxisYSource(mountainYScale);
	    mountainTerrain.setSource(groundGradient);

	    /*
	     * terrain
	     */

	    // terrain_type_fractal
	    ModuleFractal terrainTypeFractal = new ModuleFractal(FractalType.FBM, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    terrainTypeFractal.setNumOctaves(3);
	    terrainTypeFractal.setFrequency(0.125);
	    terrainTypeFractal.setSeed(seed);

	    // terrain_autocorrect
	    ModuleAutoCorrect terrainAutoCorrect = new ModuleAutoCorrect(0, 1);
	    terrainAutoCorrect.setSource(terrainTypeFractal);
	    terrainAutoCorrect.calculate();

	    // terrain_type_y_scale
	    ModuleScaleDomain terrainTypeYScale = new ModuleScaleDomain();
	    terrainTypeYScale.setScaleY(0);
	    terrainTypeYScale.setSource(terrainAutoCorrect);

	    // terrain_type_cache
	    ModuleCache terrainTypeCache = new ModuleCache();
	    terrainTypeCache.setSource(terrainTypeYScale);

	    // highland_mountain_select
	    ModuleSelect highlandMountainSelect = new ModuleSelect();
	    highlandMountainSelect.setLowSource(highlandTerrain);
	    highlandMountainSelect.setHighSource(mountainTerrain);
	    highlandMountainSelect.setControlSource(terrainTypeCache);
	    highlandMountainSelect.setThreshold(0.65);
	    highlandMountainSelect.setFalloff(0.2);

	    // highland_lowland_select
	    ModuleSelect highlandLowlandSelect = new ModuleSelect();
	    highlandLowlandSelect.setLowSource(lowlandTerrain);
	    highlandLowlandSelect.setHighSource(highlandMountainSelect);
	    highlandLowlandSelect.setControlSource(terrainTypeCache);
	    highlandLowlandSelect.setThreshold(0.25);
	    highlandLowlandSelect.setFalloff(0.15);

	    // highland_lowland_select_cache
	    ModuleCache highlandLowlandSelectCache = new ModuleCache();
	    highlandLowlandSelectCache.setSource(highlandLowlandSelect);

	    // ground_select
	    ModuleSelect groundSelect = new ModuleSelect();
	    groundSelect.setLowSource(0);
	    groundSelect.setHighSource(1);
	    groundSelect.setThreshold(0.5);
	    groundSelect.setControlSource(highlandLowlandSelectCache);

	    /*
	     * cave
	     */
	   
	    ModuleFractal caveShape = new ModuleFractal(FractalType.RIDGEMULTI, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    caveShape.setNumOctaves(1);
	    caveShape.setFrequency(4);
	    caveShape.setSeed(seed);

	    ModuleFractal caveShape2 = new ModuleFractal(FractalType.RIDGEMULTI, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    caveShape2.setNumOctaves(1);
	    caveShape2.setFrequency(4);
	    caveShape2.setSeed(seed+1000);
	    
	    ModuleCombiner caveShape22 = new ModuleCombiner(CombinerType.MULT);
	    caveShape22.setSource(0, caveShape2);
	    caveShape22.setSource(1, caveShape2);
	    
	    // combined, 'pre-perturbed' cave shape
	    ModuleCombiner caveShapeA = new ModuleCombiner(CombinerType.MULT);
	    caveShapeA.setSource(0, caveShape);
	    caveShapeA.setSource(1, caveShape22);
	    
	    ModuleCache caveShapeCache = new ModuleCache();
	    caveShapeCache.setSource(caveShapeA); // use for terrain types as well 
	    
	    Module mp_caveModule = caveModuleCreate(caveShapeCache, seed);

	    /*
	     * Terrain Type
	     */
	    ModuleFractal terrainTypeHelperModule = new ModuleFractal(FractalType.RIDGEMULTI, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    terrainTypeHelperModule.setNumOctaves(1);
	    terrainTypeHelperModule.setFrequency(2); //high for testing
	    terrainTypeHelperModule.setSeed(seed);
	    
	    ModuleAutoCorrect terrAutoCorrect = new ModuleAutoCorrect(0, 1);
	    terrAutoCorrect.setSource(terrainTypeHelperModule);
	    terrAutoCorrect.calculate();

	    // lowland_scale
	    ModuleScaleOffset terrScaleOffset = new ModuleScaleOffset();
	    terrScaleOffset.setScale(.5);
	    terrScaleOffset.setOffset(-0.1);
	    terrScaleOffset.setSource(terrAutoCorrect);
	    
	    ModuleScaleOffset caveTampDown = new ModuleScaleOffset();
	    caveTampDown.setScale(.2);
	    caveTampDown.setSource(caveShapeCache);
	    
	    ModuleScaleOffset terrOffByCaveFrac = new ModuleScaleOffset();
	    terrOffByCaveFrac.setOffset(caveTampDown);
	    terrOffByCaveFrac.setSource(terrScaleOffset);
	    
	    // lowland_y_scale
	    ModuleScaleDomain terrScaleYDomain = new ModuleScaleDomain();
	    terrScaleYDomain.setScaleY(.2);
	    terrScaleYDomain.setSource(terrOffByCaveFrac);
	    
//	    // sand or grass ?
	    ModuleCombiner terrTypePlusMountainsNoise = new ModuleCombiner(CombinerType.ADD);
	    terrTypePlusMountainsNoise.setSource(0, terrainTypeCache);
	    terrTypePlusMountainsNoise.setSource(1, mp_mountainCache); //ever useful
	    
	    ModuleScaleOffset scaleTerrMountain = new ModuleScaleOffset();
	    scaleTerrMountain.setScale(.5);
	    scaleTerrMountain.setSource(terrTypePlusMountainsNoise);
	    
	    ModuleSelect terrainSelect = new ModuleSelect();
	    terrainSelect.setLowSource(BlockType.SAND.getFloat());
	    terrainSelect.setHighSource(BlockType.DIRT.getFloat());
	    terrainSelect.setControlSource(scaleTerrMountain);
	    terrainSelect.setThreshold(.9);
	    terrainSelect.setFalloff(0);
	    
	    ModuleTranslateDomain strataGradientPerturb = new ModuleTranslateDomain();
	    strataGradientPerturb.setAxisYSource(terrScaleYDomain);
	    strataGradientPerturb.setSource(groundGradient);
	    
	    //SELECT SAND/GRASS or STONE
	    ModuleSelect stoneSandGrassSelect = new ModuleSelect();
	    stoneSandGrassSelect.setLowSource(terrainSelect); //stone value
	    stoneSandGrassSelect.setHighSource(BlockType.STONE.getFloat());
	    stoneSandGrassSelect.setControlSource(strataGradientPerturb);
	    stoneSandGrassSelect.setThreshold(.94);
	    stoneSandGrassSelect.setFalloff(0);
	    
	    //ADD AREAS NEAR CAVES AS CAVE-ISH STONE
	    ModuleSelect stoneSandGrassCaveSelect = new ModuleSelect();
	    stoneSandGrassCaveSelect.setLowSource(stoneSandGrassSelect); //stone value
	    stoneSandGrassCaveSelect.setHighSource(BlockType.CAVESTONE.getFloat());
	    stoneSandGrassCaveSelect.setControlSource(caveShapeCache);
	    stoneSandGrassCaveSelect.setThreshold(.75);
	    stoneSandGrassCaveSelect.setFalloff(0);
	        
	    ModuleCache terrSelectCache = new ModuleCache();
	    terrSelectCache.setSource(terrainSelect);

	    /*
	     * final-almost
	     */
	    ModuleCombiner groundCaveMultiply = new ModuleCombiner(CombinerType.MULT);
	    groundCaveMultiply.setSource(0, mp_caveModule);
	    groundCaveMultiply.setSource(1, groundSelect);
	    groundCaveMultiply.setSource(2, stoneSandGrassCaveSelect);
	    
	    /*
	     * Bedrock
	     */
	    ModuleGradient bedrockGradient = new ModuleGradient();
	    bedrockGradient.setGradient(0, 0, .95, 1);
	    
	    ModuleScaleOffset bedrockScaleOffset = new ModuleScaleOffset();
	    bedrockScaleOffset.setScale(.05);
	    bedrockScaleOffset.setOffset(0.0);
	    bedrockScaleOffset.setSource(mp_mountainCache);
	    
	    ModuleScaleDomain bedrockYScale = new ModuleScaleDomain();
	    bedrockYScale.setScaleY(0);
	    bedrockYScale.setSource(bedrockScaleOffset);

	    ModuleTranslateDomain bedrockTerrain = new ModuleTranslateDomain();
	    bedrockTerrain.setAxisYSource(bedrockYScale);
	    bedrockTerrain.setSource(bedrockGradient);
	    
	    ModuleSelect bedrockSelect = new ModuleSelect();
	    bedrockSelect.setLowSource(groundCaveMultiply);
	    bedrockSelect.setHighSource(BlockType.BEDROCK.getFloat()); //BEDROCK VALUE
	    bedrockSelect.setControlSource(bedrockTerrain);
	    bedrockSelect.setThreshold(0.9);
	    bedrockSelect.setFalloff(0);

	    /*
	     * Draw it
	     */
	    
	    noiseModule = bedrockSelect;

	  }
	 private static Module caveModuleCreate(Module caveShapeA, long seed) {
		  int moduleSelect = 0;
		  // cave_shape


		    // cave_perturb_fractal
		    ModuleFractal cavePerturbFractal = new ModuleFractal(FractalType.FBM, BasisType.GRADIENT, InterpolationType.QUINTIC);
		    cavePerturbFractal.setNumOctaves(6);
		    cavePerturbFractal.setFrequency(3);
		    cavePerturbFractal.setSeed(seed);

		    // cave_perturb_scale
		    ModuleScaleOffset cavePerturbScale = new ModuleScaleOffset();
		    cavePerturbScale.setScale(0.25);
		    cavePerturbScale.setOffset(0); // 0 was orig val
		    cavePerturbScale.setSource(cavePerturbFractal);

		    // cave_perturb
		    ModuleTranslateDomain cavePerturb = new ModuleTranslateDomain();
		    cavePerturb.setAxisXSource(cavePerturbScale);
		    cavePerturb.setSource(caveShapeA);
		    
		    /*
		     * reduce caves at low Y with gradient
		     */
		    ModuleGradient caveDepthGradient = new ModuleGradient();
		    caveDepthGradient.setGradient(0,0,.85,1);
		    ModuleBias caveGradientBias = new ModuleBias();
		    caveGradientBias.setSource(caveDepthGradient);
		    caveGradientBias.setBias(.75);
		    ModuleScaleOffset flipCaveDepthGradient = new ModuleScaleOffset();
		    flipCaveDepthGradient.setScale(-3.5);
		    flipCaveDepthGradient.setOffset(1.5);
		    flipCaveDepthGradient.setSource(caveDepthGradient);
		    
		    ModuleCombiner minCombiner = new ModuleCombiner(CombinerType.MIN);
		    minCombiner.setSource(0, 1);
		    minCombiner.setSource(1, flipCaveDepthGradient);
		    
		    ModuleCombiner caveDepthCombiner = new ModuleCombiner(CombinerType.MULT);
		    caveDepthCombiner.setSource(0, cavePerturb);
		    caveDepthCombiner.setSource(1, minCombiner);

		    // cave_select
		    ModuleSelect caveSelect = new ModuleSelect();
		    caveSelect.setLowSource(1);
		    caveSelect.setHighSource(0);
//		    caveSelect.setControlSource(cavePerturb);
		    caveSelect.setControlSource(caveDepthCombiner);
		    caveSelect.setThreshold(0.8);
		    caveSelect.setFalloff(0);
		    
		    return caveSelect;
//		    canvas.updateImage(caveSelect);
//		    canvas.updateImage(caveDepthCombiner);
//		    canvas.updateImage(caveGradientBias);
//		    canvas.updateImage(minCombiner);
//		    canvas.updateImage(flipCaveDepthGradient);
//		    canvas.updateImage(caveShapeAttenuate);
//		    canvas.updateImage(caveAttenuateBias);
	  }

}
