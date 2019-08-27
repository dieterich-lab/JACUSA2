package lib.data.global;

public class BaseCounts {
	// A, C, G, T
	private static int[] num = new int[4];

	public static void add(String s) {
		for(char ch : s.toCharArray())
			add(num, ch);
	}
	
	static void add(int[] a, char ch) {
		a[i(ch)]++;
	}
	
	static int i(char ch) {
		switch (ch) {
		case 'A':
		case 'a':
			return 0;

		case 'C':
		case 'c':
			return 1;

		case 'G':
		case 'g':
			return 2;

		case 'T':
		case 't':
			return 3;
		default:
			break;
		}
		return -1;
	}
}
