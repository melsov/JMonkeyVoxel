package voxel.landscape.util;

public class Asserter {
	public static void assertTrue(boolean condition) {
		assertTrue(condition, "");
	}
	public static void assertTrue(boolean condition, String s) {
		if (!condition) {
			System.out.println(s);
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	public static void assertFalseAndDie(String s) {
		assertTrue(false, s);
	}
}
