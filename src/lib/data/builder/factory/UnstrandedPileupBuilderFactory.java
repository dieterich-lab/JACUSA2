package lib.data.builder.factory;


import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;
import lib.data.builder.UnstrandedPileupBuilder;
import lib.data.cache.PileupCallCache;

public class UnstrandedPileupBuilderFactory<T extends PileupData> 
extends AbstractDataBuilderFactory<T> {

	public UnstrandedPileupBuilderFactory() {
		this(null);
	}
	
	public UnstrandedPileupBuilderFactory(final AbstractParameter<T> generalParameter) {
		super(LIBRARY_TYPE.UNSTRANDED, generalParameter);
	}

	@Override
	public UnstrandedPileupBuilder<T> newInstance(final AbstractConditionParameter<T> conditionParameter) {
		return new UnstrandedPileupBuilder<T>(conditionParameter, getGeneralParameter(), 
				new PileupCallCache<T>(conditionParameter, getGeneralParameter().getMethodFactory()));
	}

}
