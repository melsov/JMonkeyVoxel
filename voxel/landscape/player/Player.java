package voxel.landscape.player;

import voxel.landscape.BlockType;
import voxel.landscape.Coord3;
import voxel.landscape.Direction;
import voxel.landscape.VoxelLandscape;
import voxel.landscape.coord.VektorUtil;
import voxel.landscape.map.TerrainMap;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.Savable;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;

public class Player 
{
	private static float MOVE_SPEED = 100f; 
    private ActionListener userInputListener = new ActionListener() {
    	public void onAction(String name, boolean keyPressed, float tpf) {
    		Vector3f move = null;
    		if (name.equals("Break") && !keyPressed) {
    			handleBreakBlock();
    			playerNode.move(Vector3f.UNIT_Y);
    		}
    		else if (name.equals("Place") && !keyPressed) {
    			handlePlaceBlock();
    		}
    		else if (name.equals("moveForward") ) {
    			move = Vector3f.UNIT_Z.mult(tpf * MOVE_SPEED);
    		}
    		else if (name.equals("moveBackward") ) {
    			move = Vector3f.UNIT_Z.mult(-tpf * MOVE_SPEED);
    		}
    		else if (name.equals("moveRight") ) {
    			move = Vector3f.UNIT_X.mult(-tpf * MOVE_SPEED);
    		}
    		else if (name.equals("moveLeft") ) {
    			move = Vector3f.UNIT_X.mult(tpf * MOVE_SPEED);
    		}
    		else if (name.equals("moveUp") ) {
    			move = Vector3f.UNIT_Y.mult(tpf * MOVE_SPEED);
    		}
    		else if (name.equals("moveDown") ) {
    			move = Vector3f.UNIT_Y.mult(-tpf * MOVE_SPEED);
    		}
    		if (move != null) {
    			Quaternion camro = cam.getRotation();
    			move = camro.mult(move);
    			playerNode.move(move);
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
//    private Geometry[] triMarkerSpheres = new Geometry[3];
    private Node playerNode;
    private Node overlayNode;
//    private CameraNode camNode;
    
    public static final int BREAK_BLOCK_RADIUS = 100;
    
    public Player(TerrainMap _terrainMap, Camera _camera, Node _worldNode, Audio _audio, VoxelLandscape _app, Node _overlayNode)
    {
    	terrainMap = _terrainMap; cam = _camera; terrainNode = _worldNode;
    	audio = _audio;
    	terrainNode.addControl(camControl);
    	app = _app;
    	overlayNode = _overlayNode;
    	initBlockCursor();
    	overlayNode.attachChild(blockCursor);
    	initPlayerGeom();
    	
//    	Material sphereMat = app.wireFrameMaterialWithColor(ColorRGBA.Magenta);
//    	int i=0;
//    	for (; i < triMarkerSpheres.length; ++i) {
//    		Geometry s = new Geometry("sphere", new Sphere(4,6,.1f));
//    		s.setMaterial(sphereMat);
//    		terrainNode.attachChild(s);
//    		triMarkerSpheres[i] = s;
//    	}
    	
//    	Line camLaser = new Line(Vector3f.UNIT_Z, Vector3f.UNIT_X.mult(300f));
//    	Geometry clgeom = new Geometry("cam_laser", camLaser);
//    	clgeom.setMaterial(app.wireFrameMaterialWithColor(ColorRGBA.Magenta));
//    	CameraNode camNode = new CameraNode("cam_node", _camera);
//    	
//    	camNode.attachChild(clgeom);
//    	terrainNode.attachChild(camNode);
    
    }
    public Node getPlayerNode() { return playerNode; }

    
    public ActionListener getUserInputListener() { return userInputListener; }

    private void moveBlockCursor() {
    	Vector3f pos = stepThroughBlocksUntilHitSolid(cam.getLocation(), cam.getDirection(), false);
    	if (pos == null) pos = Vector3f.NEGATIVE_INFINITY;
    	pos = VektorUtil.Floor(pos);
    	blockCursor.setLocalTranslation(pos);
    }
    private void handleBreakBlock() 
    {
    	Vector3f vhit = stepThroughBlocksUntilHitSolid(cam.getLocation(), cam.getDirection(), false);
    	if (vhit == null) return;
    	Coord3 hitV =Coord3.FromVector3f( vhit  ); 
    	if (hitV == null) return;
        audio.playBreakCompleteSound();
        terrainMap.SetBlockAndRecompute((byte) BlockType.AIR.ordinal(), hitV);
    }
    private void handlePlaceBlock()
    {
    	Vector3f vhit = stepThroughBlocksUntilHitSolid(cam.getLocation(), cam.getDirection(), true);
    	if (vhit == null) return;
    	Coord3 placeCo = Coord3.FromVector3f( vhit);
    	if (placeCo == null) return;
    	audio.playBreakCompleteSound();
    	terrainMap.SetBlockAndRecompute((byte) BlockType.GRASS.ordinal(), placeCo);
    }
	/*
	 * updating JMonkey geometry bounds is expensive...rely completely on block lookups for placing/breaking
	 */
    private Vector3f stepThroughBlocksUntilHitSolid(Vector3f start, Vector3f direction, boolean wantPlaceBlock) {
    	byte block = (byte) BlockType.NON_EXISTENT.ordinal();
    	start = start.add(new Vector3f(.5f,.5f,.5f));
    	
    	Coord3 hit = null;
    	int scale = 0;
    	Vector3f hitV = null;
    	Vector3f cheatFracDir = direction.mult(.25f);
    	while (BlockType.IsAirOrNonExistent(block)) {
    		hitV = start.add( cheatFracDir.mult(scale));
    		hit = Coord3.FromVector3f( hitV );
    		block = terrainMap.lookupBlock(hit);
    		scale++;
    		if (scale > BREAK_BLOCK_RADIUS) return null;
    	}

    	if (wantPlaceBlock) {
    		Vector3f oppdir = direction.mult(-1f);
    		float escapeLength = DistToCorner(hitV, direction).dot(oppdir);
    		Vector3f escapeTheBlock = hitV.add(oppdir.mult(escapeLength));
    		Vector3f escapeNudge = VektorUtil.Sign(oppdir).mult(VektorUtil.MaskClosestToWholeNumber(escapeTheBlock).mult(.5f));
    		return escapeTheBlock.add(escapeNudge);
    	}
    	
    	return hitV;
    }
	 /* CONSIDER: implement to replace 'cheat Frac Dir' */
    private static Vector3f[] ComputeSteps(Vector3f pos, Vector3f dir) {
    	Vector3f _pos = pos.clone();
    	
    	Vector3f blockCorner = VektorUtil.OneIfPos(dir).add(_pos);
    	blockCorner = VektorUtil.Floor(blockCorner);
    	return null; 
    }
    private static Vector3f DistToCorner(Vector3f pos, Vector3f dir) {
    	Vector3f corner = EntryCorner(pos, dir);
    	return corner.subtract(pos);
    }
    private static Vector3f EntryCorner(Vector3f pos, Vector3f dir) {
    	Vector3f corner = VektorUtil.OneIfNeg(dir).add(pos);
    	return VektorUtil.Floor(corner);
    }

    /*
     * TODO: add block cursor to a non-terrain node. so it won't collide with rays
     */
    protected void initBlockCursor() {
      Box box = new Box(.505f, .505f, .505f);
      blockCursor = new Geometry("block_cursor", box);
      Material mark_mat = app.wireFrameMaterialWithColor(ColorRGBA.Black); 
      blockCursor.setMaterial(mark_mat);
    }
    private void initPlayerGeom()
    {
    	Box box = new Box(.1f,.5f,.1f);
    	Geometry playerGeom = new Geometry("player_geom", box);
    	playerGeom.setMaterial(app.wireFrameMaterialWithColor(ColorRGBA.BlackNoAlpha));
    	playerNode = new Node("player_node");
    	playerNode.attachChild(playerGeom);
    	terrainNode.attachChild(playerNode);
    	
//    	camNode = new CameraNode("cam_node", cam);
//    	camNode.setControlDir(ControlDirection.CameraToSpatial);
//    	playerNode.attachChild(camNode);
//    	
//    	camNode.setLocalTranslation(new Vector3f(0, 5, -5));
//    	camNode.lookAt(playerNode.getLocalTranslation(), Vector3f.UNIT_Y);
//    	
    	playerNode.setLocalTranslation(new Vector3f(40,50,40));
//    	playerNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

    private void checkCollisions(float tpf) {
    	Vector3f ploc = playerNode.getLocalTranslation();
    	Vector3f push = Vector3f.ZERO;

    	Coord3 pco = Coord3.FromVector3f(ploc);
    	if (BlockType.IsSolid(terrainMap.lookupBlock(pco))) {
    		push = Vector3f.UNIT_Y;
    	}
    	if (BlockType.IsSolid(terrainMap.lookupBlock(pco.add(new Coord3(0,2,0))))) {
    		push = Vector3f.UNIT_Y.mult(-1f);
    	}
    	playerNode.move(push.mult(tpf * 400f));
    	
//    	float margin = .2f;
//    	
//    	for (int i = 1; i < 6; i += 2) {
//    		Vector3f dir = Direction.DirectionVector3fs[i];
//    		Coord3 dirco = Direction.DirectionCoords[i];
//    		
//    	}
    }
    public class CamControl extends AbstractControl implements Cloneable, Savable
    {
    	private float timeSinceUpdate = 0;
    	private static final float TIMEPERUPDATE = .5f;
		@Override
		protected void controlUpdate(float tpf) {
			timeSinceUpdate += tpf;
			if (timeSinceUpdate > TIMEPERUPDATE) {
				timeSinceUpdate = 0;
				moveBlockCursor(); 
			}
			checkCollisions(tpf);
			
		}
    	
		@Override
		protected void controlRender(RenderManager arg0, ViewPort arg1) {
		}
    }
}
