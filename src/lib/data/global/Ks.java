package lib.data.global;

import java.util.ArrayList;
import java.util.List;

public class Ks {
	private static Ks ks = null;
	private static int k = 6;
	private List<Kmer> kmers;
	
	private Ks() {
		kmers = new ArrayList<Kmer>();
		for(int i=0; i<k; i++)
			kmers.add(new Kmer(i));
	}
	
	public static Ks getInstance() {
		if (ks == null) ks = new Ks();
		return ks;
	}
	
	private static int start(int s) {
		return s<0 ? 0 : s;
	}

	private static int end(int e, int l) {
		return e<l ? e : l;
	}
	
	public String getIns(String read, int readPos, int cigarLen, boolean reverse) {
		if (reverse)
			return read.substring(start(readPos-k), end(readPos+cigarLen+k-1, read.length()));
		else 
			return read.substring(start(readPos-k), end(readPos+cigarLen+k-1, read.length()));
	}

	public void addIns(String read, int readPos, int cigarLen, boolean reverse) {
		System.out.print(reverse+"\t");
		System.out.println(getIns(read, readPos, cigarLen, reverse));
	}
	
	
}
