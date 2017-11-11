package lib.data.builder.factory;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;
import lib.data.builder.AbstractDataBuilder;
import lib.data.builder.StrandedPileupBuilder;
import lib.data.cache.Cache;
import lib.data.cache.FRPairedEnd1BaseCallCache;
import lib.data.cache.FRPairedEnd2BaseCallCache;
import lib.data.cache.PileupCallCache;

public class StrandedPileupBuilderFactory<T extends PileupData>
extends AbstractDataBuilderFactory<T> {

	public StrandedPileupBuilderFactory(final LIBRARY_TYPE libraryType, final AbstractParameter<T> generalParameter) throws IllegalArgumentException {
		super(libraryType, generalParameter);
		if (libraryType == LIBRARY_TYPE.UNSTRANDED) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public AbstractDataBuilder<T> newInstance(final AbstractConditionParameter<T> conditionParameter) throws IllegalArgumentException {
		final Cache<T> forward = new PileupCallCache<T>(conditionParameter, getGeneralParameter().getMethodFactory());
		final Cache<T> reverse = new PileupCallCache<T>(conditionParameter, getGeneralParameter().getMethodFactory());
		
		switch (getLibraryType()) {
		case FR_FIRSTSTRAND:
			return new StrandedPileupBuilder<T>(conditionParameter, getGeneralParameter(),
					LIBRARY_TYPE.FR_FIRSTSTRAND,
					new FRPairedEnd1BaseCallCache<T>(forward, reverse));
		
		case FR_SECONDSTRAND:
			return new StrandedPileupBuilder<T>(conditionParameter, getGeneralParameter(),
					LIBRARY_TYPE.FR_SECONDSTRAND,
					new FRPairedEnd2BaseCallCache<T>(forward, reverse));

		default:
			throw new IllegalArgumentException();
		}
	}

}