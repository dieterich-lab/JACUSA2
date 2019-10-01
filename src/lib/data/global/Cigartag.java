package lib.data.global;

import java.util.HashMap;
import java.util.Map;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMRecord;

public class Cigartag {
	private int cigarwin;
	private static Cigartag singleton = null;
	private Map<String, Long> cbs;
	private long[] counts;

	public static Cigartag getInstance() {
		return getInstance(16);
	}
	
	public static Cigartag getInstance(int len) {
		if (singleton == null) singleton = new Cigartag(len);
		return singleton;
	}
	
	private Cigartag(int len) {
		cigarwin = len;
		counts = new long[len+1];
		cbs = new HashMap<>();
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
        if (cb == null) {
            throw new SAMException("Cannot extract barcodes from SAMRecord with no CB tag, read: " + record.getReadName());
        }
        addCB(cb);
	}

	public Map<String, Long> getCB() {
		return cbs;
	}

	public void process(int len) {
		// TODO Auto-generated method stub
		
	}
	
}
