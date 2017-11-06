package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.pileup.builder.inverted.FRPairedEnd1InvertedPileupBuilder;
import jacusa.util.WindowCoordinate;
import net.sf.samtools.SAMFileReader;

public class FRPairedEnd1PileupBuilderFactory<T extends BaseQualData>
extends AbstractDataBuilderFactory<T> {

	public FRPairedEnd1PileupBuilderFactory() {
		super(LIBRARY_TYPE.FR_FIRSTSTRAND);
	}

	@Override
	public DataBuilder<T> newInstance(
			WindowCoordinate windowCoordinates, 
			SAMFileReader reader, 
			ConditionParameters<T> condition, 
			AbstractParameters<T> parameters) {
		if (condition.isInvertStrand()) {
			return new FRPairedEnd1InvertedPileupBuilder<T>(
					windowCoordinates, reader, condition, parameters);
		}
		return new FRPairedEnd1PileupBuilder<T>(
				windowCoordinates, reader, condition, parameters);
	}
	
}