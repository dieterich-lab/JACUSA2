package lib.data.builder;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameters;
import lib.data.BaseQualData;
import lib.data.cache.BaseCallCache;

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
			final AbstractConditionParameter<T> conditionParameter, 
			final AbstractParameters<T> generalParameters) {
		return new UnstrandedPileupBuilder<T>(
				conditionParameter, generalParameters, 
				new BaseCallCache(generalParameters.getBaseConfig(), generalParameters.getActiveWindowSize()));
	}

}
