package lib.data.global;

public class Insertions {
	// A, C, G, T
	private static int[] ins = new int[4]; // insertion sequences
	private static int[] ups = new int[4]; // upstream sequences

	public static void addIns(String s) {
		BaseCounts.add(ins, s);
	}

	public static void addUps(String s) {
		BaseCounts.add(ups, s);
	}
}
