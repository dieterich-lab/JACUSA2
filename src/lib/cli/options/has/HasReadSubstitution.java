package lib.cli.options.has;

import java.util.SortedSet;

import lib.util.Base;

public interface HasReadSubstitution {

	public static String READ_SUB 		= "read_sub";
	public static String READ_SUB_BASES = "read_sub_bases";
	
	SortedSet<BaseSubstitution> getReadSubstitutions();
	void addReadSubstition(BaseSubstitution baseSubstitution);
	
	public enum BaseSubstitution {
		
		AtoC(Base.A, Base.C),
		AtoG(Base.A, Base.G),
		AtoT(Base.A, Base.T),
		
		CtoA(Base.C, Base.A),
		CtoG(Base.C, Base.G),
		CtoT(Base.C, Base.T),
		
		GtoA(Base.G, Base.A),
		GtoC(Base.G, Base.C),
		GtoT(Base.G, Base.T),
		
		TtoA(Base.T, Base.A),
		TtoC(Base.T, Base.C),
		TtoG(Base.T, Base.G);
		
		private final Base from;
		private final Base to;
	    private final String string;

	    public static final String SEP = ">";
	    
		BaseSubstitution(Base from, Base to) {
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
		
		public String toString() {
			return string;
		}

		public static BaseSubstitution bases2enum(final Base from, final Base to) {
			switch (from) {
			
			case A:
				switch (to) {
				case C:
					return BaseSubstitution.AtoC;
				case G:
					return BaseSubstitution.AtoG;
				case T:
					return BaseSubstitution.AtoT;
				default:
					throw new IllegalArgumentException("Illegal Base: " + to);
				}
				
			case C:
				switch (to) {
				case A:
					return BaseSubstitution.CtoA;
				case G:
					return BaseSubstitution.CtoG;
				case T:
					return BaseSubstitution.CtoT;
				default:
					throw new IllegalArgumentException("Illegal Base: " + to);
				}

			case G:
				switch (to) {
				case A:
					return BaseSubstitution.GtoA;
				case C:
					return BaseSubstitution.GtoC;
				case T:
					return BaseSubstitution.GtoT;
				default:
					throw new IllegalArgumentException("Illegal Base: " + to);
				}
			
			case T:
				switch (to) {
				case A:
					return BaseSubstitution.TtoA;
				case C:
					return BaseSubstitution.TtoC;
				case G:
					return BaseSubstitution.TtoG;
				default:
					throw new IllegalArgumentException("Illegal Base: " + to);
				}
				
			default:
				throw new IllegalArgumentException("Illegal Base: " + from);
			}
		}
		
		public static BaseSubstitution string2enum(final String s) {
			if (s.length() != 2 + SEP.length()) {
				throw new IllegalArgumentException("Unrecognised base substitution: " + s);
			}
			final Base from = Base.valueOf(s.charAt(0));
			final Base to = Base.valueOf(s.charAt(s.length() - 1));
			return bases2enum(from, to);
		}

	}
	
}
