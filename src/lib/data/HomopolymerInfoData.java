package lib.data;

import lib.data.has.hasHomopolymerInfo;
import lib.util.coordinate.Coordinate;

public class HomopolymerInfoData
extends AbstractData
implements hasHomopolymerInfo {

	private boolean isHomopolymer;

	public HomopolymerInfoData(final HomopolymerInfoData pileupData) {
		super(pileupData);
		isHomopolymer = pileupData.isHomopolymer;
	}
	
	public HomopolymerInfoData(final LIBRARY_TYPE libraryType, final Coordinate coordinate) {
		super(libraryType, coordinate);

		isHomopolymer = false;
	}

	@Override
	public boolean isHomopolymer() {
		return isHomopolymer;
	}
	
	@Override
	public void setHomopolymer(boolean b) {
		isHomopolymer = b;
	}
	
	public void add(AbstractData abstractData) {
		HomopolymerInfoData data = (HomopolymerInfoData) abstractData;
		if (data.isHomopolymer) {
			isHomopolymer = true;
		}
	}
	
	@Override
	public HomopolymerInfoData copy() {
		return new HomopolymerInfoData(this);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Library type: ");
		sb.append(getLibraryType().toString());
		sb.append('\n');

		sb.append("Homopolymer: ");
		sb.append(isHomopolymer);
		return sb.toString();
	}

	
	
}
