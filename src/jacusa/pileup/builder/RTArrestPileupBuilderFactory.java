package jacusa.pileup.builder;

import lib.cli.parameters.AbstractParameters;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.BaseQualReadInfoData;
import lib.util.WindowCoordinate;

import htsjdk.samtools.SamReader;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class RTArrestPileupBuilderFactory<T extends BaseQualReadInfoData> 
extends AbstractDataBuilderFactory<T> {

	final AbstractDataBuilderFactory<T> pbf;
	
	public RTArrestPileupBuilderFactory(final AbstractDataBuilderFactory<T> pbf) {
		super(pbf.getLibraryType());
		this.pbf = pbf;
	}

	@Override
	public DataBuilder<T> newInstance(
			final WindowCoordinate windowCoordinates,
			final SamReader reader, 
			final JACUSAConditionParameters<T> condition, 
			final AbstractParameters<T> parameters) {
		return new RTArrestPileupBuilder<T>(condition, pbf.newInstance(windowCoordinates, reader, condition, parameters));
	}

}
