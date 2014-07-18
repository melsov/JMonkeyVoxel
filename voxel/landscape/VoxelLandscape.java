package voxel.landscape;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import voxel.landscape.collection.ColumnMap;
import voxel.landscape.debugmesh.DebugChart;
import voxel.landscape.debugmesh.DebugChart.DebugShapeType;
import voxel.landscape.debugmesh.IDebugGet2D;
import voxel.landscape.debugmesh.IDebugGet3D;
import voxel.landscape.map.TerrainMap;
import voxel.landscape.map.debug.Array2DViewer;
import voxel.landscape.map.light.ChunkSunLightComputer;
import static java.lang.System.out;

import com.jme3.app.SimpleApplication;
import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.terrain.noise.Color;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.ui.Picture;
import com.jme3.util.SkyFactory;


// TODO: Separate world builder and game logic, etc., everything else...
public class VoxelLandscape extends SimpleApplication
{
	private static boolean UseTextureMap = true;
	
	private TerrainMap terrainMap = new TerrainMap();
	private ColumnMap columnMap = new ColumnMap();
	
	private boolean debugInfoOn = false; 
	
	private void attachMeshToScene(Chunk chunk)
	{
		Geometry geo = chunk.getGeometryObject(); 
		this.addGeometryToScene(geo);
	}
	
	private void addGeometryToScene(Geometry geo)
	{
		Material mat; 
		if (UseTextureMap)
		{
			mat = new Material(assetManager, "MatDefs/BlockTex2.j3md");
			
			Texture blockTex = assetManager.loadTexture("Textures/dog_64d_.jpg");
//			Texture blockTex = assetManager.loadTexture("Textures/dog_64d_light.jpg");
			
			blockTex.setMagFilter(Texture.MagFilter.Nearest);
			blockTex.setWrap(Texture.WrapMode.Repeat);
			
	    	mat.setTexture("ColorMap", blockTex);
		} else {
			mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			mat.setBoolean("VertexColor", true);
		}
    	
    	geo.setMaterial(mat);
    	rootNode.attachChild(geo);
	}

	
	private void makeWorld()
	{
		rootNode.attachChild(SkyFactory.createSky(
	            assetManager, VoxelLandscape.TexFromBufferedImage(SmallBufferedImage(new java.awt.Color(.3f,.6f,1f,1f))) , true));
		
		Coord3 hedgeMin = Coord3.Zero; // new Coord3(1,0,1);
		Coord3 hedgeMax = new Coord3(1,0,1);
		Coord3 minChCo = terrainMap.getMinChunkCoord().add(hedgeMin);
		Coord3 maxChCo = terrainMap.getMaxChunkCoord().minus(hedgeMax);

		for(int i = minChCo.x; i < maxChCo.x; ++i)
		{
			for(int j = minChCo.z; j < maxChCo.z; ++j)
			{
				generateColumnData(i,j);
				buildColumn(i,j);
			}
		} 
//		for(int i = minChCo.x; i < maxChCo.x; ++i)
//		{
//			for(int j = minChCo.z; j < maxChCo.z; ++j)
//			{
//			}
//		} 
		if (!debugInfoOn) return;
		/*
		 * debugging
		 */
		Array2DViewer.getInstance().saveToPNG("_debugPicture.png");
		showImageOnScreen(Array2DViewer.getInstance().getImage());
		addDebugGeometry();
	}
	private void addDebugGeometry()
	{
		Coord3 addMin = Coord3.Zero;
		Coord3 minusMax = Coord3.Zero;
		Coord3 min = Chunk.ToWorldPosition(terrainMap.getMinChunkCoord().add(addMin));
		Coord3 max = Chunk.ToWorldPosition(terrainMap.getMaxChunkCoord().minus(minusMax));
		
		DebugChart debugChart = new DebugChart(min, max);
//		Geometry terrainHeights = debugChart.makeHeightMapVertsUVs(DebugShapeType.QUAD, 0f, wireFrameMaterialWithColor(ColorRGBA.Red), new IDebugGet2D() {
//			public float getAValue(int x, int z) {
//				return terrainMap.GetMaxY(x, z);
//			}
//		});
//		rootNode.attachChild(terrainHeights);
//		Geometry terrainSunHeight = debugChart.makeHeightMapVertsUVs(DebugShapeType.QUAD, 0f, wireFrameMaterialWithColor(ColorRGBA.Yellow), new IDebugGet2D() {
//			public float getAValue(int x, int z) {
//				return terrainMap.GetSunLightmap().GetSunHeight(x, z);
//			}
//		});
//		rootNode.attachChild(terrainSunHeight);
		Geometry terrainLight = debugChart.makeTerrainInfoVertsUVs3D(DebugShapeType.QUAD, 0f, wireFrameMaterialVertexColor(), new IDebugGet3D() {
			public float getAValue(int x, int y, int z) {
				return terrainMap.GetSunLightmap().GetLight(x, y, z);
			}
		});
		rootNode.attachChild(terrainLight);
	}
	private Material wireFrameMaterialWithColor(ColorRGBA color) {
		Material wireMaterial = new Material(assetManager, "/Common/MatDefs/Misc/Unshaded.j3md");
    	wireMaterial.setColor("Color", color);
    	wireMaterial.getAdditionalRenderState().setWireframe(true);
    	return wireMaterial;
	}
	private Material wireFrameMaterialVertexColor() {
		Material wireMaterial = new Material(assetManager, "/Common/MatDefs/Misc/Unshaded.j3md");
		wireMaterial.setBoolean("VertexColor", true);
    	wireMaterial.getAdditionalRenderState().setWireframe(true);
    	return wireMaterial;
	}
	private static BufferedImage SmallBufferedImage(java.awt.Color color) {
		BufferedImage image = new BufferedImage(10,10, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0 ; x < image.getWidth(); ++x) {
			  for (int y = 0; y < image.getHeight() ; ++y) {
				  image.setRGB(x, y, color.getRGB() );
			  }
		  }
		return image;
	}
	private static Texture2D TexFromBufferedImage(BufferedImage bim) {
		AWTLoader awtl = new AWTLoader();
		Image im = awtl.load(bim, false);
		return new Texture2D(im);
	}
	private void showImageOnScreen(BufferedImage bim) {
		Texture2D tex = TexFromBufferedImage(bim);
		tex.setMagFilter(Texture.MagFilter.Nearest);
		tex.setWrap(Texture.WrapMode.Repeat);
    	
    	Picture pic = new Picture("Pic from BufferedImage");
    	pic.setTexture(assetManager, tex, false);
    	pic.setWidth(200);
    	pic.setHeight(200);
    	pic.setPosition(0f, 100f);
    	guiNode.attachChild(pic);
	}
	private void generateColumnData(int x, int z) 
	{
		columnMap.SetBuilt(x, z);
		terrainMap.generateNoiseForChunkColumn(x, z);
		ChunkSunLightComputer.ComputeRays(terrainMap, x, z);
		ChunkSunLightComputer.Scatter(terrainMap, columnMap, x, z); //TEST WANT
	}
	private void buildColumn(int x, int z)
	{
		Coord3 minChCo = terrainMap.getMinChunkCoord();
		Coord3 maxChCo = terrainMap.getMaxChunkCoord();
		for (int k = minChCo.y; k < maxChCo.y; ++k )
		{
			Chunk ch = terrainMap.GetChunkInstance(x, k, z);
			attachMeshToScene(ch);
		}
	}
	
	/* Use the main event loop to trigger repeating actions. */
    @Override
    public void simpleUpdate(float tpf) 
    {
        // make the player rotate:

//        testGeom.rotate(0, 0, 2f*tpf);
//        testGeom.rotate(4f*tpf, 0, 0);
//        testGeom.move(2f * tpf, 0f, 0f);
    }
    
    /*
     * Do initialization related stuff here
     */
    @Override
    public void simpleInitApp() 
    {
//    	viewPort.addProcessor(new WireProcessor(assetManager));
//    	viewPort.removeProcessor(...); // COULD MAYBE USE THIS TO TOGGLE WIRE FRAMES
    	makeWorld();
    	flyCam.setMoveSpeed(45);
    	cam.setLocation(new Vector3f(20,50,20));
//    	cam.lookAt(new Vector3f(0, 30, 0), Vector3f.UNIT_Y);
    	cam.lookAt(rootNode.getLocalTranslation(), Vector3f.UNIT_Y);
    }
    
	/*
	 * Program starts here... 
	 */
    public static void main(String[] args)
    {
        VoxelLandscape app = new VoxelLandscape();
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode[] modes = device.getDisplayModes();
        for(DisplayMode mode : modes) {
        	out.println(mode.toString());
        }
     
        int i=0; // note: there are usually several, let's pick the first
        AppSettings settings = new AppSettings(true);
        settings.setResolution(modes[i].getWidth() - 20,modes[i].getHeight() - 40);
//        settings.setFrequency(modes[i].getRefreshRate());
//        settings.setBitsPerPixel(modes[i].getBitDepth());
//        settings.setFullscreen(device.isFullScreenSupported());
        app.setSettings(settings);

        
        app.setShowSettings(false);
        app.start(); // start the game
        
    }
}
