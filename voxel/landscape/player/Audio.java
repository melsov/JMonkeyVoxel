package voxel.landscape.player;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;

public class Audio 
{
	AudioNode blockBreakComplete;
	AudioNode audio_nature;
	AssetManager assetManager;
	Node rootNode;
	
	public Audio(AssetManager _assetManager, Node _rootNode) {
		assetManager = _assetManager; 
		rootNode = _rootNode;
		initAudio();
	}
	
	/** We create two audio nodes. */
	private void initAudio() {
	    /* gun shot sound is to be triggered by a mouse click. */
		blockBreakComplete = new AudioNode(assetManager, "Sound/Effects/Gun.wav", false);
	    blockBreakComplete.setPositional(false);
	    blockBreakComplete.setLooping(false);
	    blockBreakComplete.setVolume(2);
	    rootNode.attachChild(blockBreakComplete);
	 
	    /* nature sound - keeps playing in a loop. */
		audio_nature = new AudioNode(assetManager, "Sound/Environment/Ocean Waves.ogg", true);
		audio_nature.setLooping(true);  // activate continuous playing
		audio_nature.setPositional(true);   
		audio_nature.setVolume(3);
		rootNode.attachChild(audio_nature);
//		audio_nature.play(); // play continuously!
	}
	  
	public void playBreakCompleteSound() {
		blockBreakComplete.playInstance(); //TEST!!
	}
	 
}
