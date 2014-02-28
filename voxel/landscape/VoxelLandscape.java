package voxel.landscape;

import jme3test.helloworld.HelloJME3;

import com.jme3.app.SimpleApplication;
//import com.jme3.bounding.BoundingVolume.Type.Position;
//import com.jme3.bounding.BoundingVolume.Type;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;
import com.jme3.math.ColorRGBA;

public class VoxelLandscape extends SimpleApplication
{
	private Geometry testGeom;
	
	private void attachMeshToScene(Mesh mesh)
	{
		Geometry geo = new Geometry("OurMesh", mesh); // using our custom mesh object
		
		testGeom = geo;
		
    	Material mat = new Material(assetManager, 
    	    "Common/MatDefs/Misc/Unshaded.j3md");
    	mat.setBoolean("VertexColor", true);
//    	mat.setColor("Color", ColorRGBA.Red);
    	geo.setMaterial(mat);
    	rootNode.attachChild(geo);
	}
	
	private ColumnMeshData makeTestColMeshData()
	{
		ColumnMeshData cmd = new ColumnMeshData();
		cmd.column = new Column(1, 1);
		cmd.position = new Coord3(0,0,0);
		cmd.type = BlockType.AIR;
		
		return cmd;
	}
	
	private Mesh meshFromCMD(ColumnMeshData cmd)
	{
		MeshSet mset = BlockMeshUtil.MeshSetFromColumnData(cmd);
		Mesh mesh = new Mesh();
		
		mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(mset.vertices));
    	mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(mset.uvs));
    	mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(mset.indices));
    	mesh.setBuffer(Type.Color, 4,mset.colors);
    	mesh.updateBound();
    	mesh.setMode(Mesh.Mode.Triangles);
    	
    	return mesh;
	}

	@Override
	public void simpleInitApp() {
		// TODO Auto-generated method stub
		
		Mesh mesh = meshFromCMD(makeTestColMeshData());
		attachMeshToScene(mesh);
	}
	
	/* Use the main event loop to trigger repeating actions. */
    @Override
    public void simpleUpdate(float tpf) {
        // make the player rotate:
        testGeom.rotate(0, 2*tpf, 0); 
    }
	
	 
    public static void main(String[] args){
        VoxelLandscape app = new VoxelLandscape();
        app.start(); // start the game
    }
}
