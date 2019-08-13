package test.utlis;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;

public class CigarBuilder {
	
	private final List<CigarElement> cigarElements;
	private CigarElement tmpElement;
	
	public CigarBuilder(final int readLength) {
		cigarElements = new ArrayList<>();
	}
	
	public CigarBuilder addMatch(final int length) {
		cigarElements.add(new CigarElement(length, CigarOperator.M));
		return this;
	}
	
	public CigarBuilder addInsertion(final int length) {
		cigarElements.add(new CigarElement(length, CigarOperator.I));
		return this;
	}
	
	public CigarBuilder addDeletion(final int length) {
		cigarElements.add(new CigarElement(length, CigarOperator.D));
		return this;
	}
	
	public CigarBuilder addIntron(final int length) {
		cigarElements.add(new CigarElement(length, CigarOperator.N));
		return this;
	}
	
	public CigarBuilder addSoftClip(final int length) {
		if (cigarElements.size() > 0) {
			throw new IllegalStateException("Cannot add soft clip in the middle of the read");
		}
		cigarElements.add(new CigarElement(length, CigarOperator.S));
		return this;
	}
	
	public CigarBuilder addHardClip(final int length) {
		if (cigarElements.size() > 0) {
			throw new IllegalStateException("Cannot add hard clip in the middle of the read");
		}
		cigarElements.add(new CigarElement(length, CigarOperator.H));
		return this;
	}
	
//	public CigarBuilder addPadding(final int length) {
//		throw new NotImplementedException();
//	}
	
	public Cigar build() {
		if (tmpElement != null) {
			cigarElements.add(tmpElement);
			tmpElement = null;
		}
		return  new Cigar(cigarElements);
	}
	
}