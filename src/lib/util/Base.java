package lib.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
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
	
	private static final Base[] VALID = new Base[] {A, C, G, T};
	private static final Map<Byte, Base> BYTE2BASE;
	private static final Map<Base, Set<Base>> REF2NON_REF;
	
	static {
		final Map<Base, Set<Base>> tmpREF2NON_REF = new EnumMap<>(Base.class);
		for (final Base base : validValues()) {
			final Set<Base> nonRefBases = new HashSet<>(VALID.length);
			for (final Base tmp : validValues()) {
				nonRefBases.add(tmp);
			}
			nonRefBases.remove(base);
			tmpREF2NON_REF.put(base, Collections.unmodifiableSet(nonRefBases));
		}
		tmpREF2NON_REF.put(Base.N, Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Base.N))));
		REF2NON_REF = Collections.unmodifiableMap(tmpREF2NON_REF);
		
		final Map<Byte, Base> tmpBYTE2BASE = new HashMap<>();
		for (Base b : Base.values()) {
			tmpBYTE2BASE.put(b.bite, b);
		}
		BYTE2BASE = Collections.unmodifiableMap(tmpBYTE2BASE);
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
		return new HashSet<>(REF2NON_REF.get(refBase));
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
