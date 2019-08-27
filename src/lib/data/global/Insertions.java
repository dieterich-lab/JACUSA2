package lib.data.global;

public class Insertions {
	// A, C, G, T
	private static int[] ins = new int[4]; // insertion sequences
	private static int[][] ups = new int[4][4]; // upstream sequences

	public static void addIns(String s) {
		for(char ch : s.toCharArray())
			BaseCounts.add(ins, ch);
	}

	public static void addUps(String s) {
		BaseCounts.add(
				ups[BaseCounts.i(s.charAt(0))], s.charAt(1));
	}
}
