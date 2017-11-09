package lib.data.builder.factory;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;
import lib.data.builder.AbstractDataBuilder;
import lib.data.builder.StrandedPileupBuilder;
import lib.data.cache.FRPairedEnd1BaseCallCache;
import lib.data.cache.FRPairedEnd2BaseCallCache;

public class StrandedPileupBuilderFactory<T extends PileupData>
extends AbstractDataBuilderFactory<T> {

	public StrandedPileupBuilderFactory(final LIBRARY_TYPE libraryType, final AbstractParameter<T> generalParameter) throws IllegalArgumentException {
		super(libraryType, generalParameter);
		if (libraryType == LIBRARY_TYPE.UNSTRANDED) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public AbstractDataBuilder<T> newInstance(AbstractConditionParameter<T> conditionParameter) throws IllegalArgumentException {
		
		switch (getLibraryType()) {
		case FR_FIRSTSTRAND:
			return new StrandedPileupBuilder<T>(conditionParameter, getGeneralParameter(),
					LIBRARY_TYPE.FR_FIRSTSTRAND,
					new FRPairedEnd1BaseCallCache(getGeneralParameter().getBaseConfig(), getGeneralParameter().getActiveWindowSize()));
		
		case FR_SECONDSTRAND:
			return new StrandedPileupBuilder<T>(conditionParameter, getGeneralParameter(),
					LIBRARY_TYPE.FR_SECONDSTRAND,
					new FRPairedEnd2BaseCallCache(getGeneralParameter().getBaseConfig(), getGeneralParameter().getActiveWindowSize()));

		default:
			throw new IllegalArgumentException();
		}
		
		
		
	}
	
}