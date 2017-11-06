package jacusa.cli.options.pileupbuilder;

import java.util.List;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.pileup.builder.AbstractDataBuilderFactory;
import jacusa.pileup.builder.FRPairedEnd1PileupBuilderFactory;
import jacusa.pileup.builder.FRPairedEnd2PileupBuilderFactory;
import jacusa.pileup.builder.UnstrandedPileupBuilderFactory;
import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class OneConditionBaseQualDataBuilderOption<T extends BaseQualData>
extends AbstractDataBuilderOption<T> {

	public OneConditionBaseQualDataBuilderOption(final int conditionIndex, final ConditionParameters<T> condition) {
		super(conditionIndex, condition);
	}
	
	public OneConditionBaseQualDataBuilderOption(final List<ConditionParameters<T>> conditions) {
		super(conditions);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName(getLongOpt().toUpperCase())
			.hasArg(true)
	        .withDescription("Choose the library type and how parallel pileups are build:\n" + getPossibleValues() + 
	        		"\n default: " + LIBRARY_TYPE.UNSTRANDED)
	        .create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String s = line.getOptionValue(getOpt());
	    	LIBRARY_TYPE l = parse(s);
	    	if (l == null) {
	    		throw new IllegalArgumentException("Possible values for " + getLongOpt().toUpperCase() + ":\n" + getPossibleValues());
	    	}
	    	
	    	for (final ConditionParameters<T> condition : getConditions()) {
	    		condition.setPileupBuilderFactory(buildPileupBuilderFactory(l));
	    	}
	    }
	}

	protected AbstractDataBuilderFactory<T> buildPileupBuilderFactory(final LIBRARY_TYPE libraryType) {
		
		switch(libraryType) {
		
		case UNSTRANDED:
			return new UnstrandedPileupBuilderFactory<T>();
		
		case FR_FIRSTSTRAND:
			return new FRPairedEnd1PileupBuilderFactory<T>();
		
		case FR_SECONDSTRAND:
			return new FRPairedEnd2PileupBuilderFactory<T>();
		}

		return null;
	}
	
}