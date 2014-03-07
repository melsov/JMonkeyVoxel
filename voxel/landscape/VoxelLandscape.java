package voxel.landscape;

import java.util.*;
import java.awt.List;
import java.util.ArrayList;
import static java.lang.System.out;

import jme3test.helloworld.HelloJME3;

import com.jme3.app.SimpleApplication;
import com.jme3.app.Application;
//import com.jme3.bounding.BoundingVolume.Type.Position;
//import com.jme3.bounding.BoundingVolume.Type;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import com.jme3.math.ColorRGBA;

public class VoxelLandscape extends SimpleApplication
{
	private static final int WORLD_DIMS_CHUNKS = 4;
	private Chunk[][] chunks = new Chunk[WORLD_DIMS_CHUNKS][WORLD_DIMS_CHUNKS];
	
	private void attachMeshToScene(Chunk testChunk)
	{
		Geometry geo = testChunk.getGeometryObject(); // new Geometry("OurMesh", mesh); // using our custom mesh object
		this.addGeometryToScene(geo);
	}
	
	private void addGeometryToScene(Geometry geo)
	{
		
//		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//    	mat.setBoolean("VertexColor", true);

		Material mat = new Material(assetManager, "MatDefs/BlockTex.j3md");

		Texture blockTex = assetManager.loadTexture("Textures/dog_64d.jpg");

		blockTex.setMagFilter(Texture.MagFilter.Nearest);
		blockTex.setWrap(Texture.WrapMode.Repeat);
		
    	mat.setTexture("ColorMap", blockTex);
    	mat.setColor("Color", new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
    	
    	geo.setMaterial(mat);
    	rootNode.attachChild(geo);
	}


	/*
	 * Everything start here
	 */
	@Override
	public void simpleInitApp() 
	{
		flyCam.setMoveSpeed(50);

		loadChunks();
	}
	
	private void loadChunks()
	{
		for(int i = 0; i < WORLD_DIMS_CHUNKS; ++i)
		{
			for(int j = 0; j < WORLD_DIMS_CHUNKS; ++j)
			{
				Chunk ch = new Chunk(new Coord2(i,j));
				chunks[i][j] = ch;
				attachMeshToScene(ch);
			}
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
	 * OK. Really everything start here... 
	 */
    public static void main(String[] args)
    {
        VoxelLandscape app = new VoxelLandscape();
        app.start(); // start the game
    }
}
