package jacusa.pileup.builder;

import lib.cli.parameters.AbstractParameters;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.BaseQualData;
import lib.util.WindowCoordinate;
import lib.util.Coordinate.STRAND;

import htsjdk.samtools.SamReader;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class UnstrandedPileupBuilderFactory<T extends BaseQualData> 
extends AbstractDataBuilderFactory<T> {

	public UnstrandedPileupBuilderFactory() {
		super(LIBRARY_TYPE.UNSTRANDED);
	}

	@Override
	public UnstrandedPileupBuilder<T> newInstance(
			final WindowCoordinate windowCoordinates,
			final SamReader reader, 
			final JACUSAConditionParameters<T> condition, 
			final AbstractParameters<T> parameters) {
		return new UnstrandedPileupBuilder<T>(windowCoordinates, reader, 
				STRAND.UNKNOWN, condition, parameters);
	}

}
