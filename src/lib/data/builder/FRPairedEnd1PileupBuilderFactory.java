package lib.data.builder;

import lib.cli.parameters.AbstractParameters;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.BaseQualData;
import lib.util.WindowCoordinate;

import htsjdk.samtools.SamReader;

public class FRPairedEnd1PileupBuilderFactory<T extends BaseQualData>
extends AbstractDataBuilderFactory<T> {

	public FRPairedEnd1PileupBuilderFactory() {
		super(LIBRARY_TYPE.FR_FIRSTSTRAND);
	}

	@Override
	public DataBuilder<T> newInstance(
			WindowCoordinate windowCoordinates, 
			SamReader reader, 
			JACUSAConditionParameters<T> condition, 
			AbstractParameters<T> parameters) {
		return new FRPairedEnd1PileupBuilder<T>(
				windowCoordinates, reader, condition, parameters);
	}
	
}