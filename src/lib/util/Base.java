package lib.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum Base {
	
	A(0, 3, (byte)'A'), 
	C(1, 2, (byte)'C'), 
	G(2, 1, (byte)'G'), 
	T(3, 0, (byte)'T'), 
	N(4, 4, (byte)'N');

	private static final Base[] VALID;
	private static final Map<Byte, Base> BYTE2BASE;
	private static final Map<Base, Set<Base>> REF2NON_REF;
	
	static {
		VALID = new Base[] {A, C, G, T};
		BYTE2BASE = new HashMap<Byte, Base>();
		for (Base b : Base.values()) {
			BYTE2BASE.put(b.c, b);
		}

		REF2NON_REF = new HashMap<Base, Set<Base>>(validValues().length);
		for (final Base refBase : validValues()) {
			final Set<Base> nonRefBases = new HashSet<Base>(VALID.length);
			for (final Base tmp : validValues()) {
				nonRefBases.add(tmp);
			}
			nonRefBases.remove(refBase);
			REF2NON_REF.put(refBase, nonRefBases);
		}
	}

	private final int index;
	private final byte c;
	private final int complementIndex;

	private Base(final int index, final int complementIndex, final byte c) {
		this.index 				= index;
		this.c 					= c;
		this.complementIndex 	= complementIndex;
	}
	
	public final byte getC() {
		return c;
	}
	
	public final int getIndex() {
		return index;
	}

	public final Base getComplement() {
		return VALID[complementIndex];
	}
	
	public static final Base valueOf(final byte c) {
		if (! BYTE2BASE.containsKey(c)) {
			return N;
		}

		return BYTE2BASE.get(c);
	}

	public static final Base valueOf(final int index) {
		return Base.values()[index];
	}

	public static final Base[] validValues() {
		return VALID;
	}

	public static final Set<Base> getNonRefBases(final Base refBase) {
		return REF2NON_REF.get(refBase);
	}

}
