package voxel.landscape;

import java.awt.Color;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum BlockType {
	NON_EXISTENT (0), 
	AIR (1), DIRT(2), 
	GRASS(3), SAND(4), 
	STONE(5), CAVESTONE(6), 
	BEDROCK(7);
	
	BlockType(int i) {
		integer = i;
	}
	private static final Map lookup = new HashMap();
	// Populate the lookup table on loading time
	static {
		for (BlockType bt : EnumSet.allOf(BlockType.class))
			lookup.put(bt.integer, bt);
	}
	public static BlockType get(int intt) {
		return (BlockType) lookup.get(intt);
	}
	public static Color debugColor(int i) {
		switch(BlockType.get(i)) {
		case  NON_EXISTENT:
			return Color.BLACK;
		case AIR:
			return new Color(.4f,.4f, 1f);
		case DIRT:
			return new Color(.4f, .2f, .3f);
		case GRASS:
			return Color.GREEN;
		case SAND:
			return new Color(.9f, .9f, .6f);
		case STONE:
			return new Color(.4f, .4f, .4f);
		case CAVESTONE:
			return new Color(.3f, .5f, .3f);
		case BEDROCK:
			return new Color(.2f,.2f,.2f);
		default:
			return Color.BLACK;
		}
	}
	
	
	public boolean equals(int i) {
		return this.ordinal() == i;
	}
	
	public static boolean isTranslucent(int i) {
		return i == BlockType.AIR.ordinal() || i == BlockType.NON_EXISTENT.ordinal();
	}
	
	public static boolean IsAir(int i) {
		return i == BlockType.AIR.ordinal();
	}
	public static boolean IsAirOrNonExistent(int i) {
		return i == BlockType.AIR.ordinal() || i == BlockType.NON_EXISTENT.ordinal();
	}
	public static boolean IsSolid(int i) {
		return !IsAirOrNonExistent(i);
	}
	
	public static boolean IsEmpty(int i) {
		return NON_EXISTENT.ordinal() == i;
	}
	
	public float getFloat() { return (float) this.ordinal(); }
	
	public static int LightLevelForType(int type) {
		return 0;
	}


	private int integer;
	
}