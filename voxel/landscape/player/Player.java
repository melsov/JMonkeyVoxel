package voxel.landscape.player;

import voxel.landscape.BlockType;
import voxel.landscape.Coord3;
import voxel.landscape.VoxelLandscape;
import voxel.landscape.map.TerrainMap;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.Savable;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;

public class Player 
{
    private ActionListener userInputListener = new ActionListener() {
    	public void onAction(String name, boolean keyPressed, float tpf) {
    		if (name.equals("Break") && !keyPressed) {
    			handleBreakBlock();
    		}
    	}
    };
    private TerrainMap terrainMap;
    private Camera cam;
    private Node terrainNode;
    private Audio audio;
    private VoxelLandscape app;
    private CamControl camControl = new CamControl();
    private Geometry blockCursor;
    private Geometry[] triMarkerSpheres = new Geometry[3];
    
    
    public Player(TerrainMap _terrainMap, Camera _camera, Node _worldNode, Audio _audio, VoxelLandscape _app)
    {
    	terrainMap = _terrainMap; cam = _camera; terrainNode = _worldNode;
    	audio = _audio;
    	terrainNode.addControl(camControl);
    	app = _app;
    	initBlockCursor();
    	terrainNode.attachChild(blockCursor);
    	Material sphereMat = app.wireFrameMaterialWithColor(ColorRGBA.Magenta);
    	int i=0;
    	for (; i < triMarkerSpheres.length; ++i) {
    		Geometry s = new Geometry("sphere", new Sphere(4,6,.1f));
    		s.setMaterial(sphereMat);
    		terrainNode.attachChild(s);
    		triMarkerSpheres[i] = s;
    	}
    }
    
    private void moveTriangleBy(Vector3f pos, Triangle tri) {
    	for (int i= 0; i < 3; ++i) {
    		Vector3f v = tri.get(i);
    		v = v.add(pos);
    		tri.set(i, v);
    	}
    }
    private void moveTriMarkers(Triangle tri) {
    	for (int i= 0; i < 3; ++i) {
    		Vector3f v = tri.get(i);
    		Geometry sg = triMarkerSpheres[i];
    		sg.setLocalTranslation(v);

    	}
    }
    
    public ActionListener getUserInputListener() { return userInputListener; }

    private void handleBreakBlock() 
    {
    	Coord3 hitV = closestCameraBlockCoord();
    	if (hitV == null) {
    		return;
    	} else {
    		System.out.println("got a collision");
    	}
        Coord3 closestCoo = hitV; // blockLocation(hitV);
        moveBlockCursor(closestCoo.toVector3());
        audio.playBreakCompleteSound();
        terrainMap.SetBlockAndRecompute((byte) BlockType.AIR.ordinal(), closestCoo);
    }
    private void moveBlockCursor(Vector3f rayHitV) {
    	if (rayHitV == null) return;
//    	blockCursor.move(rayHitV);
    	blockCursor.setLocalTranslation(rayHitV);
    }
    private Coord3 closestCameraBlockCoord()
    {
    	CollisionResults results = new CollisionResults();
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        terrainNode.collideWith(ray, results);
//        return null;
//
        CollisionResult closest = results.getClosestCollision();
        if (closest == null) return null;
        int triI = closest.getTriangleIndex();
        Geometry geom = closest.getGeometry();
        Mesh hitMesh = geom.getMesh();
        Triangle hitTri =  new Triangle();
        hitMesh.getTriangle(triI, hitTri);
        Vector3f average = blockVectorFromTriangle(hitTri);
        Vector3f geomLoc = geom.getLocalTranslation();
        Vector3f blockV = geomLoc.add(average);
        moveTriangleBy(geomLoc, hitTri);
        moveTriMarkers(hitTri);
        return Coord3.FromVector3f(blockV);
//        return closest.getContactPoint();
    }
    private Vector3f blockVectorFromTriangle(Triangle tri) {
    	return tri.getCenter();
//    	Vector3f average = tri.get(0).add(tri.get(1).add(tri.get(2)));
//    	average = average.divide(3f);
//    	return average;
    }
    /*
     * TODO: set up a block face cursor
     */
    //CONSIDER: inaccurate?
    private Coord3 blockLocation(Vector3f hitPoint) {
//    	return Coord3.FromVector3f(hitPoint);
    	Vector3f dif = hitPoint.subtract(cam.getLocation());
    	dif = dif.normalize();
    	Vector3f sign = dif; // Coord3.FromVector3f(dif).sign().toVector3();
//    	sign = sign.mult(.5f);
    	return Coord3.FromVector3f(hitPoint.add(sign));
    }
    /** A red ball that marks the last spot that was "hit" by the "shot". */
    protected void initBlockCursor() {
      Sphere sphere = new Sphere(4, 6, .5f);
      blockCursor = new Geometry("block_cursor", sphere);
      Material mark_mat = app.wireFrameMaterialWithColor(ColorRGBA.Red); // new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//      mark_mat.setColor("Color", ColorRGBA.Red);
      blockCursor.setMaterial(mark_mat);
    }
    
    public class CamControl extends AbstractControl implements Cloneable, Savable
    {

    	private float timeSinceUpdate = 0;
    	private static final float TIMEPERUPDATE = .7f;
		@Override
		protected void controlUpdate(float tpf) {
			timeSinceUpdate += tpf;
			if (timeSinceUpdate > TIMEPERUPDATE) {
				timeSinceUpdate = 0;
//				moveBlockCursor(); //causes frustum bugginess...
			}
		}
    	
		@Override
		protected void controlRender(RenderManager arg0, ViewPort arg1) {
		}
    }
}
