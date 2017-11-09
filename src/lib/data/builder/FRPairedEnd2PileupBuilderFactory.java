package lib.data.builder;

import lib.cli.parameters.AbstractParameters;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.BaseQualData;
import lib.util.WindowCoordinate;

import htsjdk.samtools.SamReader;

public class FRPairedEnd2PileupBuilderFactory<T extends BaseQualData> 
extends AbstractDataBuilderFactory<T> {
	
	public FRPairedEnd2PileupBuilderFactory() {
		super(LIBRARY_TYPE.FR_SECONDSTRAND);
	}

	@Override
	public AbstractStrandedPileupBuilder<T> newInstance(
			final WindowCoordinate windowCoordinates, 
			final SamReader reader, 
			final JACUSAConditionParameters<T> condition,
			final AbstractParameters<T> parameters) {
		return new FRPairedEnd2PileupBuilder<T>(windowCoordinates, reader, condition, parameters);
	}

}