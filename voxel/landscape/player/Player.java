package voxel.landscape.player;

import voxel.landscape.BlockType;
import voxel.landscape.Coord3;
import voxel.landscape.map.TerrainMap;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class Player 
{
    private ActionListener userInputListener = new ActionListener() {
    	public void onAction(String name, boolean keyPressed, float tpf) {
    		if (name.equals("Break") && !keyPressed) {
    			handleBreakBlock();
    		}
    	}
    };
    TerrainMap terrainMap;
    Camera cam;
    Node worldNode;
    Audio audio;
    
    public Player(TerrainMap _terrainMap, Camera _camera, Node _worldNode, Audio _audio)
    {
    	terrainMap = _terrainMap; cam = _camera; worldNode = _worldNode;
    	audio = _audio;
    }
    
    public ActionListener getUserInputListener() { return userInputListener; }

    private void handleBreakBlock() 
    {
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        worldNode.collideWith(ray, results);

        CollisionResult closest = results.getClosestCollision();
        Coord3 closestCoo = blockLocation(closest.getContactPoint());
        audio.playBreakCompleteSound();
        terrainMap.SetBlockAndRecompute((byte) BlockType.AIR.ordinal(), closestCoo);
    }
    /*
     * TODO: set up a block face cursor
     */
    //CONSIDER: inaccurate?
    private Coord3 blockLocation(Vector3f hitPoint) {
    	Vector3f dif = hitPoint.subtract(cam.getLocation());
    	Vector3f sign = Coord3.FromVector3f(dif).sign().toVector3();
    	sign = sign.mult(.05f);
    	return Coord3.FromVector3f(hitPoint.add(sign));
    }
    
}
