package jacusa.cli.options.pileupbuilder;

import java.util.List;

import jacusa.pileup.builder.AbstractDataBuilderFactory;
import jacusa.pileup.builder.FRPairedEnd1PileupBuilderFactory;
import jacusa.pileup.builder.FRPairedEnd2PileupBuilderFactory;
import jacusa.pileup.builder.UnstrandedPileupBuilderFactory;
import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;
import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.BaseQualData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class OneConditionBaseQualDataBuilderOption<T extends BaseQualData>
extends AbstractDataBuilderOption<T> {

	public OneConditionBaseQualDataBuilderOption(final int conditionIndex, final AbstractConditionParameter<T> conditionParameter) {
		super(conditionIndex, conditionParameter);
	}
	
	public OneConditionBaseQualDataBuilderOption(final List<AbstractConditionParameter<T>> conditionParameters) {
		super(conditionParameters);
	}
	
	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Choose the library type and how parallel pileups are build:\n" + getPossibleValues() + 
	        		"\n default: " + LIBRARY_TYPE.UNSTRANDED)
	        	.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String s = line.getOptionValue(getOpt());
	    	LIBRARY_TYPE l = parse(s);
	    	if (l == null) {
	    		throw new IllegalArgumentException("Possible values for " + getLongOpt().toUpperCase() + ":\n" + getPossibleValues());
	    	}
	    	
	    	for (final AbstractConditionParameter<T> conditionParameter : getConditionParameters()) {
	    		conditionParameter.setPileupBuilderFactory(buildPileupBuilderFactory(l));
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