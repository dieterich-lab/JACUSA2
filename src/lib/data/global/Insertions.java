package lib.data.global;

public class Insertions {
	// A, C, G, T
	public static int[] ins = new int[4]; // insertion sequences
	public static int[] ups = new int[4]; // upstream sequences

	public static void addIns(String s) {
		add(ins, s);
	}

	public static void addUps(String s) {
		add(ups, s);
	}
	
	public static void add(int[] a, String s) {
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
