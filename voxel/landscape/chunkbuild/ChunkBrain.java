package voxel.landscape.chunkbuild;

import voxel.landscape.Chunk;
import voxel.landscape.MeshSet;

import com.jme3.export.Savable;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Quad;

/*
 * Build (or rebuild) a mesh for the chunk
 * and set and reset our geometry's ('spatial's') mesh to the (re)built mesh.
 */
public class ChunkBrain extends AbstractControl implements Cloneable, Savable, ThreadCompleteListener 
{
	private Chunk chunk;
	private boolean dirty, lightDirty;
	private AsyncBuildMesh asyncBuildMesh = null;
	private boolean shouldApplyMesh = false;
	
	public ChunkBrain(Chunk _chunk) {
		chunk = _chunk;
		Mesh mesh = new Quad(1,1); 
		Geometry geom = new Geometry("chunk_geom", mesh);
		geom.setLocalTranslation(_chunk.originInBlockCoords().toVector3());
		geom.addControl(this);
	}
	@Override
	protected void controlUpdate(float timePerFrame) {
		/*
		if (shouldApplyMesh) {
			ChunkBuilder.ApplyMeshSet(asyncBuildMesh.getMeshSet(), getMesh(), asyncBuildMesh.getOnlyLight());
			getGeometry().updateModelBound();
			asyncBuildMesh = null;
			shouldApplyMesh = false;
		}
		*/
		if(dirty) {
			buildMesh(false);
			dirty = lightDirty = false;
		}
		if(lightDirty) {
			buildMeshLight();
			lightDirty = false;
		}
	}

	@Override
	protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
		//do nothing here
	}

	private Mesh getMesh() {
		Geometry geom = getGeometry();
		if (geom.getMesh() == null) {
			Mesh mesh = new Mesh();
			mesh.setDynamic();
			mesh.setMode(Mesh.Mode.Triangles);
			geom.setMesh(mesh);
		}
		return geom.getMesh();
	}
	public Geometry getGeometry() { return (Geometry) getSpatial(); }

	private void buildMeshLight() { buildMesh(true); }
	
	private void buildMesh(boolean onlyLight) 
	{
		/*
		if (asyncBuildMesh != null) return;
		asyncBuildMesh = new AsyncBuildMesh(onlyLight);
		asyncBuildMesh.addListener(this);
		Thread t = new Thread(asyncBuildMesh);
		t.start();
		*/
		MeshSet mset = ChunkBuilder.buildMesh(chunk, getMesh(), onlyLight);
		ChunkBuilder.ApplyMeshSet(mset, getMesh(), onlyLight);

		getGeometry().updateModelBound();
//		getSpatial().updateGeometricState(); //don't rely on JMonkey collisions at all?
	}
	public class AsyncBuildMesh extends ResponsiveRunnable
	{

		private boolean onlyLight;
		private MeshSet mset;
		public AsyncBuildMesh(boolean _onlyLight) {
			onlyLight = _onlyLight;
		}
		public MeshSet getMeshSet() { return mset; }
		public boolean getOnlyLight() { return onlyLight; }
		@Override
		public void doRun() {
			mset = ChunkBuilder.buildMesh(chunk, onlyLight);
		}
		
	}
	@Override
	public void notifyThreadComplete(ResponsiveRunnable responsizeRunnable) {
		if (responsizeRunnable.getClass() == AsyncBuildMesh.class) {
//			shouldApplyMesh = true;
		}
	}
	

	public void SetLightDirty() {
		lightDirty=true;
	}
	
	public void SetDirty() {
		dirty=true;
	}

}
