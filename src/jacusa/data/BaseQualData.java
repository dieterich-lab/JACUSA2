package jacusa.data;

import jacusa.pileup.builder.hasLibraryType;
import jacusa.util.Coordinate;
import jacusa.util.Coordinate.STRAND;

public class BaseQualData
extends AbstractData
implements hasBaseQualCount, hasLibraryType {

	private BaseQualCount baseQualCount;
	private char referenceBase;

	private LIBRARY_TYPE libraryType;
	
	private STRAND effectiveStrand;
	
	public BaseQualData() {
		super();

		baseQualCount 	= new BaseQualCount();
		referenceBase	= 'N';
		
		libraryType		= LIBRARY_TYPE.UNSTRANDED;
		effectiveStrand	= STRAND.UNKNOWN;
	}

	public BaseQualData(final BaseQualData pileupData) {
		super(pileupData);
		this.baseQualCount 	= pileupData.getBaseQualCount().copy();
		this.referenceBase 	= pileupData.getReferenceBase();
		
		this.libraryType	= pileupData.getLibraryType();
		this.effectiveStrand= pileupData.effectiveStrand;
	}
	
	public BaseQualData(final Coordinate coordinate, final char referenceBase,
			final LIBRARY_TYPE libraryType) {
		super(coordinate);
		
		baseQualCount		= new BaseQualCount();
		this.referenceBase	= referenceBase;
		
		this.libraryType	= libraryType;
		this.effectiveStrand= STRAND.UNKNOWN;
	}
		
	@Override
	public BaseQualCount getBaseQualCount() {
		return baseQualCount;
	}

	@Override
	public void setBaseQualCount(final BaseQualCount baseQualCount) {
		this.baseQualCount = baseQualCount;
	}

	@Override
	public void setReferenceBase(final char referenceBase) {
		this.referenceBase = referenceBase;
	}

	@Override
	public char getReferenceBase() {
		return referenceBase;
	}

	public void add(AbstractData abstractData) {
		BaseQualData baseQualData = (BaseQualData) abstractData;
		baseQualCount.add(baseQualData.getBaseQualCount());
		this.referenceBase = baseQualData.getReferenceBase();
	}
	
	@Override
	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}

	public BaseQualData getEffective() {
		BaseQualData ret = copy();
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
				ret.getBaseQualCount().invert();
			}
			break;
			
		case FR_FIRSTSTRAND:
			ret.effectiveStrand = Coordinate.invertStrand(getCoordinate().getStrand());
			if (ret.effectiveStrand == STRAND.REVERSE) {
				ret.getBaseQualCount().invert();
			}
			break;

		}

		return ret;
	}

	public STRAND getEffectiveStrand() {
		return effectiveStrand;
	}
	
	@Override
	public BaseQualData copy() {
		return new BaseQualData(this);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Library type: ");
		sb.append(getLibraryType().toString());
		sb.append('\n');

		sb.append("Base count: ");
		sb.append(getBaseQualCount().toString());
		return sb.toString();
	}
	
}
