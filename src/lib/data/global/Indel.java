package lib.data.global;

import java.util.HashMap;
import java.util.Map;

import lib.record.RecordRefProvider;

public class Indel {
	private Map<String, Integer> ins, del;
	private static Indel singleton = null;
	
	private Indel() {
		ins = new HashMap<>();
		del = new HashMap<>();
	}
	
	public static Indel getInstance() {
		if (singleton == null) singleton = new Indel();
		return singleton;
	}
	
	public Map<String, Integer> getIns() {
		return ins;
	}

	public Map<String, Integer> getDel() {
		return del;
	}

	private static int start(int s) {
		return s<0 ? 0 : s;
	}

	private static int end(int e, int l) {
		return e<l ? e : l;
	}
	
	private static char complement(char c) {
		switch (c) {
		case 'A':
		case 'a':
			return 'T';

		case 'C':
		case 'c':
			return 'G';

		case 'G':
		case 'g':
			return 'C';

		case 'T':
		case 't':
			return 'A';

		case 'N':
		case 'n':
			return 'N';
		default:
			break;
		}
		return 'N';
	}

	private static String reverseComplement(String s) {
		return new String(reverseComplement(s.toCharArray()));
	}
	
	private static char[] reverseComplement(char[] c) {
		char[] r = new char[c.length];
		for(int i=0; i<c.length; i++) {
			r[c.length-1-i] = complement(c[i]);
		}
		return r;
	}
	
	public String getIns(String read, int readPos, int cigarLen, boolean reverse) {
		String s = read.substring(start(readPos), end(readPos+cigarLen, read.length()));
		return reverse ? reverseComplement(s) : s;
	}

	public void addIns(String read, int readPos, int cigarLen, boolean reverse) {
		String s = getIns(read, readPos, cigarLen, reverse);
		addCounts(ins, s);
	}
	
	public String getDel(RecordRefProvider ref, int refPos, int readPos, int cigarLen, boolean reverse) {
		char[] c = new char[cigarLen];
		for(int i=0; i<c.length; i++) {
			c[i] = ref.getRefBase(refPos+i, readPos+i).getChar();
		}
		return new String(reverse ? reverseComplement(c) : c);
	}
	
	public void addDel(RecordRefProvider ref, int refPos, int readPos, int cigarLen, boolean reverse) {
		String s = getDel(ref, refPos, readPos, cigarLen, reverse);
		addCounts(del, s);
	}
	
	private static void addCounts(Map<String, Integer> map, String s) {
		map.put(s, map.containsKey(s) ? map.get(s)+1 : 1);
	}
}
