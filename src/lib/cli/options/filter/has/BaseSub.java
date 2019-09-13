package lib.cli.options.filter.has;

import lib.util.Base;

public enum BaseSub {
	
	A2C(Base.A, Base.C),
	A2G(Base.A, Base.G),
	A2T(Base.A, Base.T),
	
	C2A(Base.C, Base.A),
	C2G(Base.C, Base.G),
	C2T(Base.C, Base.T),
	
	G2A(Base.G, Base.A),
	G2C(Base.G, Base.C),
	G2T(Base.G, Base.T),
	
	T2A(Base.T, Base.A),
	T2C(Base.T, Base.C),
	T2G(Base.T, Base.G);
	
	private final Base from;
	private final Base to;
    private final String string;

    public static final String SEP = "2";
    
	BaseSub(Base from, Base to) {
		this.from = from;
		this.to = to;
		string = from.toString() + SEP + to.toString();
	}

	public Base getFrom() {
		return from;
	}
	
	public Base getTo() {
		return to;
	}
	
	@Override
	public String toString() {
		return string;
	}

	public static BaseSub bases2enum(final Base from, final Base to) {
		switch (from) {
		
		case A:
			switch (to) {
			case C:
				return BaseSub.A2C;
			case G:
				return BaseSub.A2G;
			case T:
				return BaseSub.A2T;
			default:
				throw new IllegalArgumentException("Illegal Base: " + to);
			}
			
		case C:
			switch (to) {
			case A:
				return BaseSub.C2A;
			case G:
				return BaseSub.C2G;
			case T:
				return BaseSub.C2T;
			default:
				throw new IllegalArgumentException("Illegal Base: " + to);
			}

		case G:
			switch (to) {
			case A:
				return BaseSub.G2A;
			case C:
				return BaseSub.G2C;
			case T:
				return BaseSub.G2T;
			default:
				throw new IllegalArgumentException("Illegal Base: " + to);
			}
		
		case T:
			switch (to) {
			case A:
				return BaseSub.T2A;
			case C:
				return BaseSub.T2C;
			case G:
				return BaseSub.T2G;
			default:
				throw new IllegalArgumentException("Illegal Base: " + to);
			}
			
		default:
			throw new IllegalArgumentException("Illegal Base: " + from);
		}
	}
	
	public static BaseSub string2enum(final String s) {
		if (s.length() != 2 + SEP.length()) {
			throw new IllegalArgumentException("Unrecognised base substitution: " + s);
		}
		final Base from = Base.valueOf(s.charAt(0));
		final Base to = Base.valueOf(s.charAt(s.length() - 1));
		return bases2enum(from, to);
	}

}