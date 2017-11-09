package lib.data.builder;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameters;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.BaseQualReadInfoData;

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
	public AbstractDataBuilder<T> newInstance(
			final AbstractConditionParameter<T> conditionParameter, 
			final AbstractParameters<T> generalParameters) {
		return new RTArrestPileupBuilder<T>(condition, pbf.newInstance(windowCoordinates, reader, condition, generalParameters));
	}

}
