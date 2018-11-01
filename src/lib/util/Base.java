package lib.util;

import java.util.Arrays;
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
			BYTE2BASE.put(b.bite, b);
		}

		REF2NON_REF = new HashMap<Base, Set<Base>>(validValues().length + 1);
		for (final Base refBase : validValues()) {
			final Set<Base> nonRefBases = new HashSet<Base>(VALID.length);
			for (final Base tmp : validValues()) {
				nonRefBases.add(tmp);
			}
			nonRefBases.remove(refBase);
			REF2NON_REF.put(refBase, nonRefBases);
		}
		REF2NON_REF.put(Base.N, new HashSet<>(Arrays.asList(Base.N)));
	}

	private final int index;
	private final byte bite;
	private final int complementIndex;

	private Base(final int index, final int complementIndex, final byte bite) {
		this.index 				= index;
		this.bite 				= bite;
		this.complementIndex 	= complementIndex;
	}
	
	public final byte getByte() {
		return bite;
	}
	
	public final int getIndex() {
		return index;
	}

	public final Base getComplement() {
		return values()[complementIndex];
	}

	public final char getChar() {
		return (char)bite;
	}
	
	public static final Base valueOf(final byte bite) {
		if (! BYTE2BASE.containsKey(bite)) {
			throw new IllegalArgumentException("Byte " + bite + " unknown");
		}

		return BYTE2BASE.get(bite);
	}
	
	public static final Base valueOf(final char c) {
		return BYTE2BASE.get((byte)c);
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

	public static final String wrap(final Base base) {
		return base.toString();
	}
	
	public static Base mergeBase(final Base base1, final Base base2) {
		if (base1 == base2) {
			return base1;
		}
		if(base1 == Base.N) {
			return base2;
		}
		if(base2 == Base.N) {
			return base1;
		}
		throw new IllegalStateException("Don't know how to merge " + base1.toString() + " and " + base2.toString());
	}

}
