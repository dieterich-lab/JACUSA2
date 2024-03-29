package jacusa.filter.homopolymer;

import java.util.ArrayList;
import java.util.Collection;

import lib.util.Base;

/**
 * Simple class to define a homopolymer
 */
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
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append("Position: ")
			.append(position)
			.append(" Base: ")
			.append(base.getChar())
			.append(" Length: ")
			.append(length)
			.toString();
	}

	/**
	 * This class will identify a collection of hompolymers in a series of base call counts with user
	 * defined minLength values.
	 * 
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
			if (current.getBase() == base){ // extend if last and new base are identical
				extend(current);
			} else {
				if (check(current, minLength)) { // check if the last sequence of base call is a homopolymer
					homopolymers.add(current);
				}
				// start a new sequence
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

		// extend the length of a potential homopolymer
		private void extend(final Homopolymer homopolymer) {
			homopolymer.length++;
		}
		
		// start a new potential homopolymer
		private Homopolymer start(final Base base, final Homopolymer homopolymer) {
			return new Homopolymer(
					base, 
					homopolymer.getPosition() + homopolymer.getLength(),
					1);
		}
		
		// check if homopolymer meets the requirements
		private boolean check(final Homopolymer homopolymer, final int minLength) {
			return homopolymer.getBase() != Base.N && homopolymer.getLength() >= minLength;
		}
		
	}
	
}
