package lib.data.global;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

public class Kmer {
	public static final char[] ord = new char[] {'A','C','G','T','-'};
	private long[][] mat;
	private List<String> row, col;
	
	public Kmer(int k) {
		super();
		int w = (int)Math.pow(5, k);
		mat = new long[w][w];
	}
	
	public static int dro(char ch) {
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

		case '-':
			return 4;
			
		default:
			break;
		}
		return -1;
	}
	/**
	 * 
	 * @return the k-mers in any order.
	 * 
	 */

	public static List<String> composition(final int k, final String text) {

		final List<String> kmers = new ArrayList<>();

		for (int i = 0; i + k <= text.length(); i++) {

			final String kmer = text.substring(i, i + k);

			kmers.add(kmer);

		}

		//Collections.sort(kmers);

		return kmers;

	}

	/**
	 * 
	 * @return the string spelled by the genome path.
	 * 
	 */

	public static String construct(final List<String> path) {

		final StringBuilder result = new StringBuilder();

		if (!path.isEmpty()) {

			result.append(path.get(0));

			for (int i = 1; i < path.size(); i++) {

				final String node = path.get(i);

				result.append(node.charAt(node.length() - 1));

			}

		}

		return result.toString();

	}

	/**
	 * 
	 * Reconstruct a string from its k-mer composition.
	 * 
	 */

	public static String construct(final int k, final List<String> kmers) {

		if (!kmers.isEmpty()) {

			for (int i = 0; i < kmers.size(); i++) {

				// starting from each kmer as the first part

				// check if we can traverse all kmers as a path.

				final StringBuilder result = new StringBuilder(kmers.get(i));

				// construct the index list for unvisited kmers

				final Set<Integer> unvisited = new HashSet<>();

				for (int j = 0; j < kmers.size(); j++) {

					if (j != i) {

						unvisited.add(j);

					}

				}

				if (traverse(k, kmers, unvisited, result)) {

					return result.toString();

				}

			}
		}

		return "";

	}

	// a recursive function to check if we can reach all kmers.

	private static boolean traverse(final int k, final List<String> kmers, final Set<Integer> unvisited,
			final StringBuilder builder) {

		if (unvisited.isEmpty()) {

			return true;

		}

		final String prefix = builder.substring(builder.length() - k + 1);

		final Set<Integer> remaining = new HashSet<>();

		for (final int next : unvisited) {

			final String kmer = kmers.get(next);

			if (kmer.startsWith(prefix)) {

				// try to use this one as the next one on the path

				builder.append(kmer.charAt(k - 1));

				remaining.addAll(unvisited);

				remaining.remove(next);

				// continue trying with remaining kmers

				if (traverse(k, kmers, remaining, builder)) {

					return true;

				}

				// recover the temporary path for next trial

				remaining.clear();

				builder.delete(builder.length() - 1, builder.length());

			}

		}

		return false;

	}

	// test program

	public static void main(String[] args) {

		for(String k : composition(4,"composition"))
		System.out.println(k);

	}

}
