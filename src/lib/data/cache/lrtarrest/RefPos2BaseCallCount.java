package lib.data.cache.lrtarrest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lib.data.basecall.array.ArrayBaseCallCount;
import lib.data.count.BaseCallCount;

public class RefPos2BaseCallCount {

	private final Map<Integer, Byte> refBase;
	private final Map<Integer, BaseCallCount> refPos2bc;

	public RefPos2BaseCallCount() {
		refBase = new HashMap<Integer, Byte>();
		refPos2bc = new HashMap<Integer, BaseCallCount>();
	}

	public RefPos2BaseCallCount(final Map<Integer, Byte> refBase, final Map<Integer, BaseCallCount> refPos2bc) {
		this();
		for (final int refPos : refBase.keySet()) {
			this.refBase.put(refPos, refBase.get(refPos));
			this.refPos2bc.put(refPos, new ArrayBaseCallCount(refPos2bc.get(refPos)));
		}
	}
	
	public void addBaseCall(final int refPos, final int baseIndex) {
		if (! refPos2bc.containsKey(refPos)) {
			refPos2bc.put(refPos, new ArrayBaseCallCount());
		}
		
		refPos2bc.get(refPos).increment(baseIndex);
	}

	public void addRefBase(final int refPos, final byte refBase) {
		if (! this.refBase.containsKey(refPos)) {
			this.refBase.put(refPos, refBase);
		} else {
			if (this.refBase.get(refPos).equals(refBase)) {
				throw new IllegalStateException();
			}
		}
	}

	
	public void add(final int refPos, final byte refBase, final BaseCallCount baseCallCount) {
		addRefBase(refPos, refBase);
		
		if (! refPos2bc.containsKey(refPos)) {
			refPos2bc.put(refPos, new ArrayBaseCallCount());
		}
		
		refPos2bc.get(refPos).add(baseCallCount);
	}
	
	public void add(RefPos2BaseCallCount src) {
		for (final int refPos : src.refPos2bc.keySet()) {
			add(refPos, src.refBase.get(refPos), src.refPos2bc.get(refPos));
		}
	}
	
	public byte getRefBase(final int refPos) {
		if (! refBase.containsKey(refPos)) {
			return 'N';
		}

		return refBase.get(refPos);
	}
	
	public Set<Integer> getRefPos() {
		return refPos2bc.keySet();
	}
	
	public BaseCallCount getBaseCallCount(final int refPos) {
		return refPos2bc.get(refPos);
	}
	
	public void clear() {
		refPos2bc.clear();
		refBase.clear();
	}
	
	public RefPos2BaseCallCount copy() {
		return new RefPos2BaseCallCount(refBase, refPos2bc);
	}
	
}
