package jacusa.cli.options.pileupbuilder;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.factory.AbstractDataBuilderFactory;
import lib.data.builder.factory.StrandedPileupBuilderFactory;
import lib.data.builder.factory.UnstrandedPileupBuilderFactory;
import lib.data.has.hasPileupCount;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class OneConditionPileupDataBuilderOption<T extends AbstractData & hasPileupCount>
extends AbstractDataBuilderOption<T> {

	public OneConditionPileupDataBuilderOption(final int conditionIndex, final AbstractConditionParameter<T> conditionParameter, final AbstractParameter<T> generalParameter) {
		super(conditionIndex, conditionParameter, generalParameter);
	}
	
	public OneConditionPileupDataBuilderOption(final List<AbstractConditionParameter<T>> conditionParameters, final AbstractParameter<T> generalParameter) {
		super(conditionParameters, generalParameter);
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
	    		conditionParameter.setDataBuilderFactory(buildPileupBuilderFactory(l));
	    	}
	    }
	}

	@Override
	protected AbstractDataBuilderFactory<T> buildPileupBuilderFactory(
			final LIBRARY_TYPE libraryType) {
		switch(libraryType) {
		
		case UNSTRANDED:
			return new UnstrandedPileupBuilderFactory<T>(getGeneralParameter());
		
		case FR_FIRSTSTRAND:
		case FR_SECONDSTRAND:
			return new StrandedPileupBuilderFactory<T>(libraryType, getGeneralParameter());
		}
		
		return null;
	}
}