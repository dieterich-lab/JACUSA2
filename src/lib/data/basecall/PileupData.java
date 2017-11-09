package lib.data.basecall;

import lib.data.AbstractData;
import lib.data.has.hasLibraryType;
import lib.data.has.hasPileupCount;
import lib.util.Coordinate;
import lib.util.Coordinate.STRAND;

public class PileupData
extends AbstractData
implements hasPileupCount, hasLibraryType {

	private PileupCount pileupCount;

	private LIBRARY_TYPE libraryType;
	private STRAND effectiveStrand;
	
	public PileupData() {
		super();

		pileupCount = new PileupCount();
		
		libraryType		= LIBRARY_TYPE.UNSTRANDED;
		effectiveStrand	= STRAND.UNKNOWN;
	}

	public PileupData(final PileupData pileupData) {
		super(pileupData);
		this.pileupCount = pileupData.pileupCount .copy();
		
		this.libraryType = pileupData.getLibraryType();
		this.effectiveStrand= pileupData.effectiveStrand;
	}
	
	public PileupData(final Coordinate coordinate, final char referenceBase,
			final LIBRARY_TYPE libraryType) {
		super(coordinate);

		pileupCount = new PileupCount();
		pileupCount.setReferenceBase(referenceBase);
		
		this.libraryType	= libraryType;
		this.effectiveStrand= STRAND.UNKNOWN;
	}
		
	@Override
	public PileupCount getPileupCount() {
		return pileupCount;
	}

	@Override
	public void setPileupCount(final PileupCount pileupCount) {
		this.pileupCount = pileupCount;
	}

	public void add(AbstractData abstractData) {
		PileupData pileupData = (PileupData) abstractData;
		this.pileupCount.add(pileupData.getPileupCount());
	}
	
	@Override
	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}

	public PileupData getEffective() {
		PileupData ret = copy();
		if (ret.effectiveStrand != STRAND.UNKNOWN) {
			return ret;
		}

		switch (getLibraryType()) {

		case UNSTRANDED:
			ret.effectiveStrand = STRAND.FORWARD;
			break;

		case FR_SECONDSTRAND:
			ret.effectiveStrand = getCoordinate().getStrand();
			if (ret.effectiveStrand == STRAND.REVERSE) {
				ret.getPileupCount().invert();
			}
			break;
			
		case FR_FIRSTSTRAND:
			ret.effectiveStrand = Coordinate.invertStrand(getCoordinate().getStrand());
			if (ret.effectiveStrand == STRAND.REVERSE) {
				ret.getPileupCount().invert();
			}
			break;

		}

		return ret;
	}

	public STRAND getEffectiveStrand() {
		return effectiveStrand;
	}
	
	@Override
	public PileupData copy() {
		return new PileupData(this);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Library type: ");
		sb.append(getLibraryType().toString());
		sb.append('\n');

		sb.append("Base count: ");
		sb.append(getPileupCount().toString());
		return sb.toString();
	}

	@Override
	public int getCoverage() {
		return pileupCount.getCoverage();
	}

	@Override
	public char getReferenceBase() {
		return pileupCount.getReferenceBase();
	}

	@Override
	public void setReferenceBase(char referenceBase) {
		pileupCount.setReferenceBase(referenceBase);
	}
	
	@Override
	public BaseCallCount getBaseCallCount() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
