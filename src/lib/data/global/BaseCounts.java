package lib.data.global;

public class BaseCounts {
	// A, C, G, T
	private static int[] num = new int[4];

	public static void add(String s) {
		add(num, s);
	}
	
	static void add(int[] a, String s) {
		for(char ch : s.toCharArray()) {
			switch (ch) {
			case 'A':
			case 'a':
				a[0]++;
				break;

			case 'C':
			case 'c':
				a[1]++;
				break;

			case 'G':
			case 'g':
				a[2]++;
				break;

			case 'T':
			case 't':
				a[3]++;
				break;
			default:
				break;
			}
		}
	}
}
