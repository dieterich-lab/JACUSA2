package jacusa.cli.options.pileupbuilder;

import java.util.List;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualReadInfoData;
import jacusa.method.AbstractMethodFactory;
import jacusa.pileup.builder.AbstractDataBuilderFactory;
import jacusa.pileup.builder.RTArrestPileupBuilderFactory;
import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;

public class OneConditionBaseQualReadInfoDataBuilderOption<T extends BaseQualReadInfoData>
extends OneConditionBaseQualDataBuilderOption<T> {

	public OneConditionBaseQualReadInfoDataBuilderOption(final int conditionIndex, final ConditionParameters<T> condition) {
		super(conditionIndex, condition);
	}

	public OneConditionBaseQualReadInfoDataBuilderOption(List<ConditionParameters<T>> conditions) {
		super(conditions);
	}
	
	protected AbstractDataBuilderFactory<T> buildPileupBuilderFactory(
			final AbstractMethodFactory<T> abstractMethodFactory,
			final LIBRARY_TYPE libraryType) {
		return new RTArrestPileupBuilderFactory<T>(super.buildPileupBuilderFactory(libraryType));
	}
	
}