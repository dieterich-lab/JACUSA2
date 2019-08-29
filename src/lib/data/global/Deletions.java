package lib.data.global;

public class Deletions {
	// A, C, G, T
	private static int[] del = new int[4]; // deletion sequences
	private static int[][] ups = new int[4][4]; // upstream sequences

	public static void addDel(String s) {
		for(char ch : s.toCharArray())
			BaseCounts.add(del, ch);
	}

	public static void addUps(String s) {
		BaseCounts.add(
				ups[BaseCounts.i(s.charAt(0))], s.charAt(1));
	}

}
