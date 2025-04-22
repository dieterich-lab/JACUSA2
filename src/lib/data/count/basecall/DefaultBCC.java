package lib.data.count.basecall;

import java.util.Set;
import java.util.TreeSet;

import lib.util.Base;

public class DefaultBCC extends AbstractBCC {

	private static final long serialVersionUID = 1L;

	private int a;
	private int c;
	private int g;
	private int t;
	
	public DefaultBCC(final int a, final int c, final int g, final int t) {
		this.a = a;
		this.c = c;
		this.g = g;
		this.t = t;
	}
	
	public DefaultBCC() {
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
		return new DefaultBCC(a, c, g, t);
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
		final Set<Base> alleles = new TreeSet<Base>();
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
	public BaseCallCount add(Base base, BaseCallCount bcc) {
		switch (base) {
		case A:
			a += bcc.getBaseCall(base);
			break;

		case C:
			c += bcc.getBaseCall(base);
			break;

		case G:
			g += bcc.getBaseCall(base);
			break;

		case T:
			t += bcc.getBaseCall(base);
			break;
			
		default:
			throw new IllegalArgumentException();
		}
		return this;
	}

	@Override
	public BaseCallCount add(BaseCallCount bcc) {
		for (final Base base : bcc.getAlleles()) {
			add(base, bcc);
		}
		return this;
	}

	@Override
	public BaseCallCount add(Base dest, Base src, BaseCallCount bcc) {
		switch (dest) {
		case A:
			a += bcc.getBaseCall(src);
			break;

		case C:
			c += bcc.getBaseCall(src);
			break;

		case G:
			g += bcc.getBaseCall(src);
			break;

		case T:
			t += bcc.getBaseCall(src);
			break;
			
		default:
			throw new IllegalArgumentException();
		}
		return this;
	}

	@Override
	public BaseCallCount subtract(Base base, BaseCallCount bcc) {
		switch (base) {
		case A:
			a -= bcc.getBaseCall(base);
			break;

		case C:
			c -= bcc.getBaseCall(base);
			break;

		case G:
			g -= bcc.getBaseCall(base);
			break;

		case T:
			t -= bcc.getBaseCall(base);
			break;
			
		default:
			throw new IllegalArgumentException();
		}
		return this;
	}

	@Override
	public BaseCallCount subtract(BaseCallCount bcc) {
		for (final Base base : bcc.getAlleles()) {
			subtract(base, bcc);
		}
		return this;
	}

	@Override
	public BaseCallCount subtract(Base dest, Base src, BaseCallCount bcc) {
		switch (dest) {
		case A:
			a -= bcc.getBaseCall(src);
			break;

		case C:
			c -= bcc.getBaseCall(src);
			break;

		case G:
			g -= bcc.getBaseCall(src);
			break;

		case T:
			t -= bcc.getBaseCall(src);
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
	
	/*
	 * Parser
	 */
	
	public static class Parser extends AbstractParser {

		public Parser() {
			super();
		}
		
		public Parser(final char baseCallSep, final char empty) {
			super(baseCallSep, empty);
		}
		@Override
		public DefaultBCC parse(String s) {
			final String[] cols = split(s);
			int a = 0;
			int c = 0;
			int g = 0;
			int t = 0;

			for (int baseIndex = 0; baseIndex < cols.length; ++baseIndex) {
				final int count = Integer.parseInt(cols[baseIndex]);
				if (count < 0) {
					throw new IllegalArgumentException(); 
				}
				
				switch (baseIndex) {
				case 0:
					a = count;
					break;

				case 1:
					c = count;
					break;
					
				case 2:
					g = count;
					break;
					
				case 3:
					t = count;
					break;
					
				default:
					break;
				}

			}
			return new DefaultBCC(a, c, g, t);
		}
		
	}
	
}
