package lib.data.builder.factory;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.BaseQualReadInfoData;
import lib.data.builder.AbstractDataBuilder;
import lib.data.builder.RTArrestPileupBuilder;
import lib.data.cache.AlignmentCache;

public class RTArrestPileupBuilderFactory<T extends BaseQualReadInfoData> 
extends AbstractDataBuilderFactory<T> {

	final AbstractDataBuilderFactory<T> pbf;
	
	public RTArrestPileupBuilderFactory(final AbstractDataBuilderFactory<T> pbf) {
		super(pbf.getLibraryType(), pbf.getGeneralParameter());
		this.pbf = pbf;
	}

	@Override
	public AbstractDataBuilder<T> newInstance(final AbstractConditionParameter<T> conditionParameter) {
		return new RTArrestPileupBuilder<T>(conditionParameter, getGeneralParameter(),
				pbf.newInstance(conditionParameter), new AlignmentCache(getGeneralParameter().getActiveWindowSize()));
	}

}
