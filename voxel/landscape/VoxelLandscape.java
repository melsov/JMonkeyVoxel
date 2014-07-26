package voxel.landscape;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import voxel.landscape.chunkbuild.AsyncGenerateColumnData;
import voxel.landscape.chunkbuild.ResponsiveRunnable;
import voxel.landscape.chunkbuild.ThreadCompleteListener;
import voxel.landscape.collection.ColumnMap;
import voxel.landscape.debugmesh.DebugChart;
import voxel.landscape.debugmesh.DebugChart.DebugShapeType;
import voxel.landscape.debugmesh.IDebugGet2D;
import voxel.landscape.debugmesh.IDebugGet3D;
import voxel.landscape.map.TerrainMap;
import voxel.landscape.map.debug.Array2DViewer;
import voxel.landscape.map.light.ChunkSunLightComputer;
import voxel.landscape.player.Audio;
import voxel.landscape.player.B;
import voxel.landscape.player.Player;
import static java.lang.System.out;

import com.jme3.app.SimpleApplication;
import com.jme3.app.Application;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.system.AppSettings;
import com.jme3.terrain.noise.Color;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.ui.Picture;
import com.jme3.util.SkyFactory;


// TODO: Separate world builder and game logic, everything else...
public class VoxelLandscape extends SimpleApplication implements ThreadCompleteListener
{
	private static boolean UseTextureMap = true;
	
	private TerrainMap terrainMap = new TerrainMap();
	private ColumnMap columnMap = new ColumnMap();
	private Player player;
	
	private boolean debugInfoOn = false; 
	private Node worldNode = new Node("world_node");
	private Node overlayNode = new Node("overlay_node");
	private boolean generatingBlockData = false;
	private List<Coord2> columnsToBeBuilt = new ArrayList<Coord2>();
	private static Coord2 screenDims;
	
	private CameraNode camNode;
	
	private void attachMeshToScene(Chunk chunk)
	{
		Geometry geo = chunk.getGeometryObject(); 
		this.addGeometryToScene(geo);
	}
	public Material getTexMapMaterial() {
		Material mat = new Material(assetManager, "MatDefs/BlockTex2.j3md");
		
		Texture blockTex = assetManager.loadTexture("Textures/dog_64d_.jpg");
		blockTex.setMagFilter(Texture.MagFilter.Nearest);
		blockTex.setWrap(Texture.WrapMode.Repeat);
		
    	mat.setTexture("ColorMap", blockTex);
    	return mat;
	}
	private void addGeometryToScene(Geometry geo)
	{
		if (geo == null) return;
		Material mat; 
		if (UseTextureMap)
		{
			mat = getTexMapMaterial();
		} else {
			mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			mat.setBoolean("VertexColor", true);
		}
    	
    	geo.setMaterial(mat);
    	worldNode.attachChild(geo);
	}
	
	private void makeInitialWorld()
	{
		rootNode.attachChild(worldNode);
		Texture2D skyTex = TexFromBufferedImage(OnePixelBufferedImage(new java.awt.Color(.3f,.6f,1f,1f)));
		rootNode.attachChild(SkyFactory.createSky( assetManager, skyTex , true) );
		
		Coord3 hedgeMin = Coord3.Zero; 
		Coord3 minChCo = terrainMap.getMinChunkCoord().add(hedgeMin);
		Coord3 maxChCo = new Coord3(2, 0, 2); 

		for(int i = minChCo.x; i < maxChCo.x; ++i)
		{
			for(int j = minChCo.z; j < maxChCo.z; ++j)
			{
				generateColumnData(i,j);
				buildColumn(i,j);
			}
		} 
		/*
		 * debugging
		 */
		if (!debugInfoOn) return;
		Array2DViewer.getInstance().saveToPNG("_debugPicture.png");
		showImageOnScreen(Array2DViewer.getInstance().getImage());
		addDebugGeometry();
	}
	private void runAsyncColumnData(int x, int z) {
		AsyncGenerateColumnData asyncColumnData = new AsyncGenerateColumnData(terrainMap, columnMap, x,z);
		asyncColumnData.addListener(this);
		Thread t = new Thread(asyncColumnData);
		t.start();
	}
	@Override
	public void notifyThreadComplete(ResponsiveRunnable responsizeRunnable) {
		if (responsizeRunnable.getClass() == AsyncGenerateColumnData.class) {
			AsyncGenerateColumnData async = (AsyncGenerateColumnData) responsizeRunnable;
			Coord2 asynccoord = new Coord2(async.getX(), async.getZ());
			columnsToBeBuilt.add(asynccoord);
			generatingBlockData = false;
		}
	}
	private void buildRestOfColumns() {
		if (columnsToBeBuilt.size() == 0) return;
		Coord2 nextCol = columnsToBeBuilt.remove(0);
		buildRestOfColumn(nextCol.x, nextCol.y);
	}
	private void buildRestOfColumn(int x, int z) {
		buildColumn(x, z);
	}
	
	private void makeMoreWorld()
	{
		if (generatingBlockData) return;
		generatingBlockData = true;
		Vector3f camPos = cam.getLocation();
		Coord3 camPosC = Coord3.FromVector3f(camPos);
		camPosC = Chunk.ToChunkPosition(camPosC);
		Coord3 emptyCol = columnMap.GetClosestEmptyColumn(camPosC.x, camPosC.z, 7);
		if (emptyCol == null) return;
		generateColumnDataAsync(emptyCol.x, emptyCol.z);
	}
	private void generateColumnDataAsync(int x, int z)
	{
		columnMap.SetBuilt(x, z);
		runAsyncColumnData(x, z);	
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
			Chunk ch = terrainMap.lookupOrCreateChunkAtPosition(x, k, z);
			if (ch == null) continue;
			ch.getChunkBrain().SetDirty();
			attachMeshToScene(ch);
		}
	}
	
	/* Use the main event loop to trigger repeating actions. */
    @Override
    public void simpleUpdate(float tpf) 
    {
//    	makeMoreWorld();
//    	buildRestOfColumns();
    	rotatePlayer();
    }

    /*
     * Do initialization related stuff here
     */
    @Override
    public void simpleInitApp() 
    {
//    	viewPort.addProcessor(new WireProcessor(assetManager));
//    	viewPort.removeProcessor(...); // KEEP FOR REFERENCE: COULD PERHAPS USE THIS TO TOGGLE WIRE FRAMES
    	
    	makeInitialWorld();
    	Audio audio = new Audio(assetManager, rootNode);
//    	initCamera();
    	flyCam.setEnabled(false);
    	rootNode.attachChild(overlayNode);
    	player = new Player(terrainMap, cam, worldNode, audio, this, overlayNode);
    	
//    	ChaseCamera chaseCam = new ChaseCamera(cam, player.getPlayerNode(), inputManager);
//    	chaseCam.setSmoothMotion(true);
    	
    	// Disable the default flyby cam
    	flyCam.setEnabled(false);
    	//create the camera Node
//    	cam.setFrustumNear(.2f);
//    	cam.setFrustumFar(100f);
    	camNode = new CameraNode("Camera Node", cam);
    	//This mode means that camera copies the movements of the target:
    	camNode.setControlDir(ControlDirection.SpatialToCamera);
    	//Attach the camNode to the target:
    	player.getPlayerNode().attachChild(camNode);
    	//Move camNode, e.g. behind and above the target:
//    	camNode.setLocalTranslation(new Vector3f(0, 5, -5));
    	//Rotate the camNode to look at the target:
    	camNode.lookAt(player.getPlayerNode().getLocalTranslation(), Vector3f.UNIT_Y);
    	
    	initCrossHairs();
    	setupInputs();
    	setupWASDInput();

//    	flyCam.setMoveSpeed(20);
//    	cam.setLocation(new Vector3f(40,50,40));
//    	cam.lookAt(new Vector3f(0, 30, 0), Vector3f.UNIT_Y);
    }
    private void rotatePlayer() {
    	Vector2f mouse = inputManager.getCursorPosition();
    	Vector2f halfScreen = screenDims.multy(.5f).toVector2f();
//    	mouse = mouse.subtract(halfScreen);
//    	mouse = mouse.divide(300f);
    	
    	mouse = mouse.subtract(halfScreen);
    	Quaternion roll = new Quaternion(); 
    	roll.fromAngleAxis( 2f * FastMath.PI * -mouse.x /(halfScreen.x ) , new Vector3f(0,1,0) );
//    	player.getPlayerNode().setLocalRotation(roll);
    	Quaternion rollV = new Quaternion();
    	rollV.fromAngleAxis( FastMath.PI * -mouse.y /(halfScreen.y * 2.0f) , new Vector3f(1,0,0) );
    	roll = roll.mult(rollV);
    	player.getPlayerNode().setLocalRotation(roll);
    }

    /*
     * Mouse inputs
     */
    private void setupInputs() {
    	inputManager.addMapping("Break",
    			new KeyTrigger(KeyInput.KEY_T),
    			new MouseButtonTrigger(MouseInput.BUTTON_LEFT) );
    	
    	inputManager.addMapping("Place",
    			new KeyTrigger(KeyInput.KEY_G),
    			new MouseButtonTrigger(MouseInput.BUTTON_RIGHT) );
    	
    	inputManager.addListener(player.getUserInputListener(), "Break");
    	inputManager.addListener(player.getUserInputListener(), "Place");
    }

    private void setupWASDInput() {
      inputManager.addMapping("moveForward", new KeyTrigger(keyInput.KEY_UP), new KeyTrigger(keyInput.KEY_W));
      inputManager.addMapping("moveBackward", new KeyTrigger(keyInput.KEY_DOWN), new KeyTrigger(keyInput.KEY_S));
      inputManager.addMapping("moveRight", new KeyTrigger(keyInput.KEY_RIGHT), new KeyTrigger(keyInput.KEY_D));
      inputManager.addMapping("moveLeft", new KeyTrigger(keyInput.KEY_LEFT), new KeyTrigger(keyInput.KEY_A));
      inputManager.addMapping("moveUp",  new KeyTrigger(keyInput.KEY_Q));
      inputManager.addMapping("moveDown",  new KeyTrigger(keyInput.KEY_Z));
//      inputManager.addMapping("displayPosition", new KeyTrigger(keyInput.KEY_P));
      inputManager.addListener(player.getUserInputListener(), "moveForward", "moveBackward", "moveRight", "moveLeft", "moveDown", "moveUp");
//      inputManager.addListener(this, "displayPosition");
    }
    
    /** A centred plus sign to help the player aim. */
    protected void initCrossHairs() {
      setDisplayStatView(false);
      guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
      BitmapText ch = new BitmapText(guiFont, false);
      ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
      ch.setText("+"); // crosshairs
      ch.setLocalTranslation( // center
        settings.getWidth() / 2 - ch.getLineWidth()/2, settings.getHeight() / 2 + ch.getLineHeight()/2, 0);
      guiNode.attachChild(ch);
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
        float scale_screen = 1f; 
        screenDims = new Coord2((int)(modes[i].getWidth() * scale_screen - 20),(int)(modes[i].getHeight() * scale_screen - 40));
        settings.setResolution(screenDims.x, screenDims.y);
//        settings.setFrequency(modes[i].getRefreshRate());
//        settings.setBitsPerPixel(modes[i].getBitDepth());
//        settings.setFullscreen(device.isFullScreenSupported());
        app.setSettings(settings);
        app.setShowSettings(false);
        
        app.start(); // start the game
        
    }
    
    /*
     * Debug helper methods
     * 
     */
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
		rootNode.attachChild(terrainLight); // make a 'debug node?'
	}
	public Material wireFrameMaterialWithColor(ColorRGBA color) {
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
	private static BufferedImage OnePixelBufferedImage(java.awt.Color color) {
		BufferedImage image = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
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

}
