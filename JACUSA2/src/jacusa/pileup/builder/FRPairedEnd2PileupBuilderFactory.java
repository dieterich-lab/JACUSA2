package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.pileup.builder.inverted.FRPairedEnd2InvertedPileupBuilder;
import jacusa.util.WindowCoordinate;
import net.sf.samtools.SAMFileReader;

public class FRPairedEnd2PileupBuilderFactory<T extends BaseQualData> 
extends AbstractDataBuilderFactory<T> {
	
	public FRPairedEnd2PileupBuilderFactory() {
		super(LIBRARY_TYPE.FR_SECONDSTRAND);
	}

	@Override
	public AbstractStrandedPileupBuilder<T> newInstance(
			final WindowCoordinate windowCoordinates, 
			final SAMFileReader reader, 
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters) {
		if (condition.isInvertStrand()) {
			return new FRPairedEnd2InvertedPileupBuilder<T>(windowCoordinates, reader, condition, parameters);
		}
		
		return new FRPairedEnd2PileupBuilder<T>(windowCoordinates, reader, condition, parameters);
	}

}