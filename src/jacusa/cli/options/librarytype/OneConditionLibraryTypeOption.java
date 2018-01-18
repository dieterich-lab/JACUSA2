package jacusa.cli.options.librarytype;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class OneConditionLibraryTypeOption<T extends AbstractData>
extends AbstractLibraryTypeOption<T> {

	public OneConditionLibraryTypeOption(final int conditionIndex, final AbstractConditionParameter<T> conditionParameter, final AbstractParameter<T, ?> generalParameter) {
		super(conditionIndex, conditionParameter, generalParameter);
	}
	
	public OneConditionLibraryTypeOption(final List<AbstractConditionParameter<T>> conditionParameters, final AbstractParameter<T, ?> generalParameter) {
		super(conditionParameters, generalParameter);
	}
	
	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Choose the library type:\n" + getPossibleValues() + 
	        		"\n default: " + LIBRARY_TYPE.UNSTRANDED)
	        	.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String s = line.getOptionValue(getOpt());
	    	LIBRARY_TYPE libraryType = parse(s);
	    	if (libraryType == null) {
	    		throw new IllegalArgumentException("Unknown Library Type for for -" + getOpt() + " --" + getLongOpt().toUpperCase());
	    	}
	    	
	    	for (final AbstractConditionParameter<T> conditionParameter : getConditionParameters()) {
	    		conditionParameter.setLibraryType(libraryType);
	    	}
	    }
	}

}