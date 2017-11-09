package lib.data.basecall;

import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasLibraryType;
import lib.util.Coordinate;
import lib.util.Coordinate.STRAND;

public class BaseCallData
extends AbstractData
implements hasBaseCallCount, hasLibraryType {

	private BaseCallCount baseCallCount;

	private LIBRARY_TYPE libraryType;
	private STRAND effectiveStrand;
	
	public BaseCallData() {
		super();

		baseCallCount = new BaseCallCount();
		
		libraryType		= LIBRARY_TYPE.UNSTRANDED;
		effectiveStrand	= STRAND.UNKNOWN;
	}

	public BaseCallData(final BaseCallData pileupData) {
		super(pileupData);
		this.baseCallCount = pileupData.baseCallCount .copy();
		
		this.libraryType = pileupData.getLibraryType();
		this.effectiveStrand= pileupData.effectiveStrand;
	}
	
	public BaseCallData(final Coordinate coordinate, final LIBRARY_TYPE libraryType) {
		super(coordinate);

		baseCallCount = new BaseCallCount();
		
		this.libraryType	= libraryType;
		this.effectiveStrand= STRAND.UNKNOWN;
	}
		
	@Override
	public BaseCallCount getBaseCallCount() {
		return baseCallCount;
	}

	public void add(AbstractData abstractData) {
		BaseCallData pileupData = (BaseCallData) abstractData;
		this.baseCallCount.add(pileupData.getBaseCallCount());
	}
	
	@Override
	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}

	public BaseCallData getEffective() {
		BaseCallData ret = copy();
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
				ret.getBaseCallCount().invert();
			}
			break;
			
		case FR_FIRSTSTRAND:
			ret.effectiveStrand = Coordinate.invertStrand(getCoordinate().getStrand());
			if (ret.effectiveStrand == STRAND.REVERSE) {
				ret.getBaseCallCount().invert();
			}
			break;

		}

		return ret;
	}

	public STRAND getEffectiveStrand() {
		return effectiveStrand;
	}
	
	@Override
	public BaseCallData copy() {
		return new BaseCallData(this);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Library type: ");
		sb.append(getLibraryType().toString());
		sb.append('\n');

		sb.append("Base count: ");
		sb.append(getBaseCallCount().toString());
		return sb.toString();
	}
	
}
