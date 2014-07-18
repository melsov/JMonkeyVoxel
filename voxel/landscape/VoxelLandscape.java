package voxel.landscape;

import java.util.*;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import voxel.landscape.collection.ColumnMap;
import voxel.landscape.jmonrenderutil.WireProcessor;
import voxel.landscape.map.TerrainMap;
import voxel.landscape.map.debug.Array2DViewer;
import voxel.landscape.map.light.ChunkSunLightComputer;
import static java.lang.System.out;

import jme3test.helloworld.HelloJME3;

import com.jme3.app.SimpleApplication;
import com.jme3.app.Application;
//import com.jme3.bounding.BoundingVolume.Type.Position;
//import com.jme3.bounding.BoundingVolume.Type;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.ui.Picture;
import com.jme3.util.BufferUtils;
import com.jme3.math.ColorRGBA;

// TODO: Separate world builder and game logic, etc., everything else...
public class VoxelLandscape extends SimpleApplication
{
	private static boolean UseTextureMap = true;
	
	private TerrainMap terrainMap = new TerrainMap();
	private ColumnMap columnMap = new ColumnMap();
	
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
			
			Texture blockTex = assetManager.loadTexture("Textures/dog_64d.jpg");
			
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
		Coord3 minChCo = terrainMap.getMinChunkCoord();
		Coord3 maxChCo = terrainMap.getMaxChunkCoord();

		for(int i = minChCo.x; i < maxChCo.x; ++i)
		{
			for(int j = minChCo.z; j < maxChCo.z; ++j)
			{
				generateColumnData(i,j);
				buildColumn(i,j); //TEST
				
			}
		}
		Array2DViewer.getInstance().saveToPNG("_debugPicture.png");
		showImageOnScreen(Array2DViewer.getInstance().getImage());
	}
	private void showImageOnScreen(BufferedImage bim) {
		AWTLoader awtl = new AWTLoader();
		Image im = awtl.load(bim, false);
		Texture2D tex = new Texture2D(im);
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
		ChunkSunLightComputer.ComputeRays(terrainMap, x, z);
		ChunkSunLightComputer.Scatter(terrainMap, columnMap, x, z);
		terrainMap.generateNoiseForChunkColumn(x, z);	
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
    	cam.lookAt(new Vector3f(0, 30, 0), Vector3f.UNIT_Y);
    }
    
	/*
	 * Program starts here... 
	 */
    public static void main(String[] args)
    {
        VoxelLandscape app = new VoxelLandscape();
        app.start(); // start the game
        
    }
}
