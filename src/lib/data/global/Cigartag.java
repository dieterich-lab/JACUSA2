package lib.data.global;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMRecord;

public class Cigartag {
	private int cigarwin;
	private static Cigartag singleton = null;
	private Map<String, Long> cbs;
	private long[] ins, del;

	public static Cigartag getInstance() {
		return getInstance(16);
	}
	
	public static Cigartag getInstance(int len) {
		if (singleton == null) singleton = new Cigartag(len);
		return singleton;
	}
	
	private Cigartag(int len) {
		cigarwin = len;
		cbs = new HashMap<>();
		ins = new long[len+1];
		del = new long[len+1];
	}
	
	private String cleanse(String s) {
		int i = s.indexOf('-');
		return i>=0 ? s.substring(0, i) : s;
	}
	
	private void addCB(String s) {
		if (s == null) return;
		String cb = cleanse(s);
		if (cbs.containsKey(cb))
			cbs.put(cb, cbs.get(cb)+1);
		else
			cbs.put(cb, 1L);
	}

	public void process(final SAMRecord record) {
		final String cb = record.getStringAttribute("CB");
        if (cb != null)	addCB(cb);
        process(record.getCigar());
	}

	public void process(final Cigar cc) {
        int len = getRealLength(cc);
        add(ins, getIndelArr(cc, true, cigarwin));
        add(del, getIndelArr(cc, false, cigarwin));
	}
	
	public long[] getIns() {
		return ins;
	}

	public long[] getDel() {
		return del;
	}

	public Map<String, Long> getCB() {
		return cbs;
	}

	public static int getRealLength(Cigar cc) {
		int len = 0;
		for (final CigarElement c : cc.getCigarElements()) {
			switch (c.getOperator()) {
			case M:
			case I:
			case D:
			case N:
			case S:
			case EQ:
			case X:
				len += c.getLength();
				break;
				
			case H:
			case P:
				break;

			default:
				break;
			
			}
		}
		return len;
	}

	private static int inc(BitSet bs, Integer i, int n, boolean b) {
		bs.set(i, i+n, b);
		return i+n;
	}
	
	public static void add(long[] a, long[] b) {
		for(int i=0; i<a.length; i++)
			a[i] += b[i];
	}
	
	public static long[] getIndelArr(Cigar cc, boolean ins, int len) {
		BitSet bs = new BitSet();
		int i = 0;
		long[] counts = new long[len+1];
		for (final CigarElement c : cc.getCigarElements()) {
			switch (c.getOperator()) {
			case M:
			case N:
			case S:
			case EQ:
			case X:
				i = inc(bs, i, c.getLength(), false);
				break;

			case I:
				i = inc(bs, i, c.getLength(), ins);
				break;

			case D:
				i = inc(bs, i, c.getLength(), !ins);
				break;

			case H:
			case P:
				break;

			default:
				break;
			
			}
		}
		for(int j=0; j<i-i%len; j+=len) {
			counts[bs.get(j,j+len).cardinality()] ++;
		}
		return counts;
	}
}
