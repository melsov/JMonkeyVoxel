package voxel.landscape.chunkbuild;

import voxel.landscape.Chunk;

import com.jme3.export.Savable;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Quad;

public class ChunkBrain extends AbstractControl implements Cloneable, Savable 
{
	private Chunk chunk;
	private boolean dirty, lightDirty;
	
	public ChunkBrain(Chunk _chunk) {
		chunk = _chunk;
		Mesh mesh = new Quad(1,1); 
		Geometry geom = new Geometry("chunk_geom", mesh);
		geom.move(_chunk.originInBlockCoords().toVector3());
		geom.addControl(this);
	}
	@Override
	protected void controlUpdate(float timePerFrame) {
		if(dirty) {
			buildMesh();
			dirty = lightDirty = false;
		}
		if(lightDirty) {
			buildMeshLight();
			lightDirty = false;
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
		
	}
	
	/*
	 * NOTES: need to be able to build (and rebuild) a mesh for the chunk
	 * and set and reset our geometry's ('spatial's') mesh to the (re)built mesh.
	 */
	
	private Mesh getMesh() {
		Geometry geom = getGeometry();
		if (geom.getMesh() == null) {
			geom.setMesh(new Mesh());
		}
		return geom.getMesh();
	}
	public Geometry getGeometry() {
		return (Geometry) getSpatial();
	}
	
	private void buildMesh() {
		ChunkBuilder.buildMesh(chunk, getMesh());
	}
	
	private void buildMeshLight() {
		ChunkBuilder.buildMesh(chunk, getMesh(), true);
	}

	public void SetLightDirty() {
		lightDirty=true;
	}
	
	public void SetDirty() {
		dirty=true;
	}
}
