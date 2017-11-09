package lib.data.builder.factory;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;
import lib.data.builder.UnstrandedPileupBuilder;
import lib.data.cache.BaseCallCache;

public class UnstrandedPileupBuilderFactory<T extends PileupData> 
extends AbstractDataBuilderFactory<T> {

	public UnstrandedPileupBuilderFactory(final AbstractParameter<T> generalParameter) {
		super(LIBRARY_TYPE.UNSTRANDED, generalParameter);
	}
	
	public UnstrandedPileupBuilderFactory() {
		this(null);
	}

	@Override
	public UnstrandedPileupBuilder<T> newInstance(final AbstractConditionParameter<T> conditionParameter) {
		return new UnstrandedPileupBuilder<T>(
				conditionParameter, getGeneralParameter(), 
				new BaseCallCache(getGeneralParameter().getBaseConfig(), getGeneralParameter().getActiveWindowSize()));
	}

}
