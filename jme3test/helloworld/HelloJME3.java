package jme3test.helloworld;
 
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
 
/** Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys. */
public class HelloJME3 extends SimpleApplication 
{
 
    public static void main(String[] args){
        HelloJME3 app = new HelloJME3();
        app.start(); // start the game
    }
 
    @Override
    public void simpleInitApp() 
    {
//        Box b = new Box(1f, 1f, .3f);  // create cube shape
//        Geometry geom = new Geometry("Box", b);  // create cube geometry from the shape
//        Material mat = new Material(assetManager,
//          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
//        mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
//        geom.setMaterial(mat);                   // set the cube's material
//        rootNode.attachChild(geom);              // make the cube appear in the scene
    	
    	
    }
    
    private Mesh testMakeMesh()
    {
    	Mesh mesh = new Mesh();
    	Vector3f[] vs = new Vector3f[] {
    		new Vector3f(0,0,0),
    		new Vector3f(3,0,0),
    		new Vector3f(0,3,0),
    		new Vector3f(3,3,0),
    	};
    	
    	Vector2f[] texCoord = new Vector2f[4];
    	texCoord[0] = new Vector2f(0,0);
    	texCoord[1] = new Vector2f(1,0);
    	texCoord[2] = new Vector2f(0,1);
    	texCoord[3] = new Vector2f(1,1);
    	
    	int [] indexes = { 2,0,1, 1,3,2 };
    	
    	float[] colorArray = new float[4*4];
    	int colorIndex=0;
    	for(int i = 0; i < 4; i++)
    	{
		   // Red value (is increased by .2 on each next vertex here)
		   colorArray[colorIndex++]= 0.1f+(.2f*i);
		   // Green value (is reduced by .2 on each next vertex)
		   colorArray[colorIndex++]= 0.9f-(0.2f*i);
		   // Blue value (remains the same in our case)
		   colorArray[colorIndex++]= 0.5f;
		   // Alpha value (no transparency set here)
		   colorArray[colorIndex++]= 1.0f;
		}
    	
    	mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vs));
    	mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
    	mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));
    	mesh.setBuffer(Type.Color, 4, colorArray);
    	mesh.updateBound();
    	mesh.setMode(Mesh.Mode.LineLoop);
    	
    	Geometry geo = new Geometry("OurMesh", mesh); // using our custom mesh object
    	Material mat = new Material(assetManager, 
    	    "Common/MatDefs/Misc/Unshaded.j3md");
    	mat.setBoolean("VertexColor", true);
//    	mat.setColor("Color", ColorRGBA.Red);
    	geo.setMaterial(mat);
    	rootNode.attachChild(geo);
    	
    	return mesh;
    }
}
