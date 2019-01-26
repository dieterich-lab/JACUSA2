package jacusa.filter.homopolymer;

import java.util.ArrayList;
import java.util.Collection;

import lib.util.Base;

public class Homopolymer {
	
	private Base base;
	private int position;
	private int length;
	
	private Homopolymer(final Base base, final int position, final int length) {
		this.base 		= base;
		this.position 	= position;
		this.length 	= length;
	}

	public int getLength() {
		return length;
	}

	public int getPosition() {
		return position;
	}
	
	public Base getBase() {
		return base;
	}
	
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Position: ");
		sb.append(position);
		sb.append(" Base: ");
		sb.append(base.getChar());
		sb.append(" Length: ");
		sb.append(length);
		return sb.toString();
	}

	/**
	 * Tested in @see test.jacusa.filter.homopolymer.HomopolymerBuilderTest
	 */
	public static class HomopolymerBuilder {
		
		private Homopolymer current;
		
		private final int minLength;
		private final Collection<Homopolymer> homopolymers;	
		
		public HomopolymerBuilder(final int position, final int minLength) {
			// position -1 because we simulate that we have observed Base.N 
			this(Base.N, position - 1, minLength);
		}
		
		public HomopolymerBuilder(final Base base, final int position, final int minLength) {
			current 		= new Homopolymer(base, position, 1);
			this.minLength 	= minLength;
			homopolymers 	= new ArrayList<>();
		}
		
		public HomopolymerBuilder add(Base base) {
			if (current.getBase() == base){
				extend(current);
			} else {
				if (check(current, minLength)) {
					homopolymers.add(current);
				}
				current = start(base, current);
			}
			return this;
		}

		public Collection<Homopolymer> build() {
			if (check(current, minLength)) {
				homopolymers.add(current);
			}

			return homopolymers;
		}

		private void extend(final Homopolymer homopolymer) {
			homopolymer.length++;
		}
		
		private Homopolymer start(final Base base, final Homopolymer homopolymer) {
			return new Homopolymer(
					base, 
					homopolymer.getPosition() + homopolymer.getLength(),
					1);
		}
		
		private boolean check(final Homopolymer homopolymer, final int minLength) {
			return homopolymer.getBase() != Base.N && homopolymer.getLength() >= minLength;
		}

	}
	
}