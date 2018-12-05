package lib.data.count.basecall;

import java.util.HashSet;
import java.util.Set;

import lib.util.Base;

public class DefaultBaseCallCount implements BaseCallCount {

	private static final long serialVersionUID = 1L;

	private int a;
	private int c;
	private int g;
	private int t;
	
	public DefaultBaseCallCount(final int a, final int c, final int g, final int t) {
		this.a = a;
		this.c = c;
		this.g = g;
		this.t = t;
	}
	
	public DefaultBaseCallCount() {
		a = 0;
		c = 0;
		g = 0;
		t = 0;
	}
	
	@Override
	public int getCoverage() {
		return a + c + g + t;
	}

	@Override
	public BaseCallCount copy() {
		return new DefaultBaseCallCount(a, c, g, t);
	}
	
	@Override
	public int getBaseCall(Base base) {
		switch (base) {
		case A:
			return a;

		case C:
			return c;

		case G:
			return g;

		case T:
			return t;
			
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Set<Base> getAlleles() {
		final Set<Base> alleles = new HashSet<>(3);
		for (final Base base : Base.validValues()) {
			if (getBaseCall(base) > 0) {
				alleles.add(base);
			}
		}
		return alleles;
	}

	@Override
	public BaseCallCount increment(Base base) {
		switch (base) {
		case A:
			a++;
			break;

		case C:
			c++;
			break;

		case G:
			g++;
			break;

		case T:
			t++;
			break;
			
		default:
			throw new IllegalArgumentException();
		}
		return this;
	}

	@Override
	public BaseCallCount clear() {
		a = 0;
		c = 0;
		g = 0;
		t = 0;
		return this;
	}

	@Override
	public BaseCallCount set(Base base, int count) {
		switch (base) {
		case A:
			a = count;
			break;

		case C:
			c = count;
			break;

		case G:
			g = count;
			break;

		case T:
			t = count;
			break;
			
		default:
			throw new IllegalArgumentException();
		}
		return this;
	}

	@Override
	public BaseCallCount add(Base base, BaseCallCount baseCallCount) {
		switch (base) {
		case A:
			a += baseCallCount.getBaseCall(base);
			break;

		case C:
			c += baseCallCount.getBaseCall(base);
			break;

		case G:
			g += baseCallCount.getBaseCall(base);
			break;

		case T:
			t += baseCallCount.getBaseCall(base);
			break;
			
		default:
			throw new IllegalArgumentException();
		}
		return this;
	}

	@Override
	public BaseCallCount add(BaseCallCount baseCallCount) {
		for (final Base base : baseCallCount.getAlleles()) {
			add(base, baseCallCount);
		}
		return this;
	}

	@Override
	public BaseCallCount add(Base dest, Base src, BaseCallCount baseCallCount) {
		switch (dest) {
		case A:
			a += baseCallCount.getBaseCall(src);
			break;

		case C:
			c += baseCallCount.getBaseCall(src);
			break;

		case G:
			g += baseCallCount.getBaseCall(src);
			break;

		case T:
			t += baseCallCount.getBaseCall(src);
			break;
			
		default:
			throw new IllegalArgumentException();
		}
		return this;
	}

	@Override
	public BaseCallCount subtract(Base base, BaseCallCount baseCallCount) {
		switch (base) {
		case A:
			a -= baseCallCount.getBaseCall(base);
			break;

		case C:
			c -= baseCallCount.getBaseCall(base);
			break;

		case G:
			g -= baseCallCount.getBaseCall(base);
			break;

		case T:
			t -= baseCallCount.getBaseCall(base);
			break;
			
		default:
			throw new IllegalArgumentException();
		}
		return this;
	}

	@Override
	public BaseCallCount subtract(BaseCallCount baseCallCount) {
		for (final Base base : baseCallCount.getAlleles()) {
			subtract(base, baseCallCount);
		}
		return this;
	}

	@Override
	public BaseCallCount subtract(Base dest, Base src, BaseCallCount baseCallCount) {
		switch (dest) {
		case A:
			a -= baseCallCount.getBaseCall(src);
			break;

		case C:
			c -= baseCallCount.getBaseCall(src);
			break;

		case G:
			g -= baseCallCount.getBaseCall(src);
			break;

		case T:
			t -= baseCallCount.getBaseCall(src);
			break;
			
		default:
			throw new IllegalArgumentException();
		}
		return this;
	}

	@Override
	public BaseCallCount invert() {
		// a <-> t
		int tmp = a;
		a = t;
		t = tmp;
		// c <-> g
		tmp = c;
		c = g;
		g = tmp;
		return this;
	}

	@Override
	public String toString() {
		return new Parser().wrap(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof DefaultBaseCallCount)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		final DefaultBaseCallCount bcc = (DefaultBaseCallCount) obj;
		return a == bcc.a && c == bcc.c && g == bcc.g && t == bcc.t;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + a;
		hash = 31 * hash + c;
		hash = 31 * hash + g;
		hash = 31 * hash + t;
		return hash;
	}
	
	/*
	 * Factory and Parser
	 */

	public static class Factory extends BaseCallCountFactory<DefaultBaseCallCount> {
		
		@Override
		public DefaultBaseCallCount create() {
			return new DefaultBaseCallCount();
		}
		
	}
	
	public static class Parser extends AbstractParser {

		public Parser() {
			super();
		}
		
		public Parser(final char baseCallSep, final char empty) {
			super(baseCallSep, empty);
		}
		@Override
		public DefaultBaseCallCount parse(String s) {
			final String[] cols = split(s);
			int a = 0;
			int c = 0;
			int g = 0;
			int t = 0;

			for (int baseIndex = 0; baseIndex < cols.length; ++baseIndex) {
				switch (baseIndex) {
				case 0:
					a = Integer.parseInt(cols[baseIndex]);
					break;

				case 1:
					c = Integer.parseInt(cols[baseIndex]);
					break;
					
				case 2:
					g = Integer.parseInt(cols[baseIndex]);
					break;
					
				case 3:
					t = Integer.parseInt(cols[baseIndex]);
					break;
					
				default:
					break;
				}

			}
			return new DefaultBaseCallCount(a, c, g, t);
		}
		
	}
	
}
