package lib.cli.options;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum Base {
	
	A(0, (byte)'A'), 
	C(1, (byte)'C'), 
	G(2, (byte)'G'), 
	T(3, (byte)'T'), 
	N(3, (byte)'T');

	private static Base[] VALID;
	private static Map<Byte, Base> BYTE2BASE;
	private static Map<Base, Base> BASE2BASE;
	private static Map<Base, Set<Base>> REF2NON_REF;
	static {
		VALID = new Base[] {A, C, G, T};
		BYTE2BASE = new HashMap<Byte, Base>();
		for (Base b : Base.values()) {
			BYTE2BASE.put(b.c, b);
		}
		BASE2BASE = new HashMap<Base, Base>();
		BASE2BASE.put(A, T);
		BASE2BASE.put(C, G);
		BASE2BASE.put(G, C);
		BASE2BASE.put(T, A);
		BASE2BASE.put(N, N);
		
		REF2NON_REF = new HashMap<Base, Set<Base>>(validValues().length);
		for (final Base refBase : validValues()) {
			final Set<Base> nonRefBases = new HashSet<Base>(BASE2BASE.keySet());
			nonRefBases.remove(refBase);
			REF2NON_REF.put(refBase, nonRefBases);
		}
	}

	private final int index;
	private final byte c;
	private final Base complement;

	private Base(final int index, final byte c) {
		this.index 		= index;
		this.c 			= c;
		this.complement = Base.getComplement(this);
	}
	
	public final byte getC() {
		return c;
	}
	
	public final int getIndex() {
		return index;
	}

	public final Base getComplement() {
		return complement;
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
	
	private static final Base getComplement(final Base base) {
		return BASE2BASE.get(base);
	}

}
