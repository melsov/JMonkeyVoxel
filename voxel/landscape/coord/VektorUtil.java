package voxel.landscape.coord;

import voxel.landscape.Coord3;

import com.jme3.math.Vector3f;
import static java.lang.Math.*;

public class VektorUtil {

	public static Vector3f Frac(Vector3f v) {
    	return v.subtract(Coord3.FromVector3f(v).toVector3());
    }
	public static Vector3f Floor(Vector3f v) {
		return Coord3.FromVector3f(v).toVector3();
	}
    public static Vector3f OneIfNeg(Vector3f v) {
    	return new Vector3f(v.x < 0 ? 1 : 0, v.y < 0 ? 0 : 1, v.z < 0 ? 1 : 0);
    }
    public static Vector3f OneIfPos(Vector3f v) {
    	return new Vector3f(v.x > 0 ? 1 : 0, v.y > 0 ? 0 : 1, v.z > 0 ? 1 : 0);
    }
    public static Vector3f Sign(Vector3f v) {
    	return new Vector3f(signum(v.x), signum(v.y), signum(v.z));
    }
    public static Vector3f Abs(Vector3f v) {
    	return new Vector3f(abs(v.x),abs(v.y),abs(v.z));
    }
    public static Vector3f Round(Vector3f v) {
    	return new Vector3f(round(v.x), round(v.y), round(v.z));
    }
    public static Vector3f MaskClosestToWholeNumber(Vector3f v) {
    	Vector3f test = VektorUtil.Abs(VektorUtil.Round(v).subtract(v) );
    	Vector3f result = Vector3f.UNIT_X;
    	if (test.y < test.x && test.y < test.z) {
    		result = Vector3f.UNIT_Y;
    	} else if (test.z < test.x) {
    		result = Vector3f.UNIT_Z;
    	}
    	return result;
    }
    
}
