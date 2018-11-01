package jacusa.cli.options.librarytype;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.has.LibraryType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Specific command line option to chose library type for one condition.
 * @author Michael Piechotta
 * @param <T>
 */
public class OneConditionLibraryTypeOption
extends AbstractLibraryTypeOption {

	public OneConditionLibraryTypeOption(final int conditionIndex, final AbstractConditionParameter conditionParameter, 
			final AbstractParameter generalParameter) {

		super(conditionIndex, conditionParameter, generalParameter);
	}

	public OneConditionLibraryTypeOption(final List<AbstractConditionParameter> conditionParameters, 
			final AbstractParameter generalParameter) {

		super(conditionParameters, generalParameter);
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		String desc = "Choose the library type";
		if (getGeneralParameter().getConditionsSize() >= 1 && getConditionIndex() == -1) {
			desc += " for all conditions";
		} else {
			desc += " for condition " + getConditionIndex();
		}
		desc += ":\n" + getPossibleValues() + 
        		"\n default: " + LibraryType.UNSTRANDED;
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc(desc) 
	        	.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
			// get option as string
	    	final String s = line.getOptionValue(getOpt());
	    	// try to get library for s
	    	final LibraryType libraryType = parse(s);
	    	// error if no library type
	    	if (libraryType == null) {
	    		throw new IllegalArgumentException("Unknown Library Type for -" + getOpt() + " " + s);
	    	}

	    	// set chosen library type
	    	for (final AbstractConditionParameter conditionParameter : getConditionParameters()) {
	    		conditionParameter.setLibraryType(libraryType);
	    	}
	    }
	}

}