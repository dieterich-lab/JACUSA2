package lib.data.global;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Kmer {
	public static final char[] ord = new char[] {'A','C','G','T','-'};
	private long[][] mat;
	private String[] row, col;
	private int k;
	
	public Kmer(int k) {
		this.k = k;
		int w = (int)Math.pow(ord.length, k);
		mat = new long[w][w];
		row = col = setNames();
	}

	public void perm(Set<String> set, String s) {
		if(s.length() == k) {
			set.add(s);
		} else {
			for(int j=0; j<ord.length; j++)
				perm(set, s+ord[j]);
		}
	}

	public String[] getNames() {
		return row;
	}
	
	public String[] setNames() {
		int w = (int)Math.pow(ord.length, k);
		String[] names = new String[w];
		Set<String> set = new TreeSet<>();
		int i = 0;
		perm(set, "");
		for(String s: set)
			names[i++] = s;
		return names;
	}

	public int getIcol(String s) {
		return getI(s, col);
	}
	
	public int getIrow(String s) {
		return getI(s, row);
	}
	
	public static int getI(String s, String[] v) {
		for (int i = 0; i < v.length; i++) {
			if (s.equals(v[i]))
				return i;
		}
		return 0;
	}

	public void add(String s, String r) {
		mat[getIrow(s)][getIcol(r)] ++;
	}

	public void add(String s) {
		add(s,s);
	}
	
	public static int dro(char ch) {
		switch (ch) {
		case 'A':
		case 'a':
			return 1;

		case 'C':
		case 'c':
			return 2;

		case 'G':
		case 'g':
			return 3;

		case 'T':
		case 't':
			return 4;

		case '-':
			return 0;
			
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
				final Set<Integer> unvisited = new TreeSet<>();
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
		final Set<Integer> remaining = new TreeSet<>();
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
		Kmer k = new Kmer(2);
		k.add("TC");
		System.out.println();
	}

}
