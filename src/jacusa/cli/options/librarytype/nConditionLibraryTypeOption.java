package jacusa.cli.options.librarytype;

import java.util.List;

import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.util.LibraryType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Specific command line option to chose library type for one condition.
 * 
 * @param <T>
 */
public class nConditionLibraryTypeOption
extends AbstractLibraryTypeOption {

	public nConditionLibraryTypeOption(final ConditionParameter conditionParameter, 
			final GeneralParameter generalParameter) {

		super(conditionParameter, generalParameter);
	}

	public nConditionLibraryTypeOption(final List<ConditionParameter> conditionParameters, 
			final GeneralParameter generalParameter) {

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
		return Option.builder()
				.longOpt(getOpt())
				.argName(LONG_OPT.toUpperCase())
				.hasArg(true)
				.desc(desc) 
	        	.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		// get option as string
    	final String s = line.getOptionValue(getOpt());
    	// try to get library for s
    	final LibraryType libraryType = parse(s);
    	// error if no library type
    	if (libraryType == null || libraryType == LibraryType.MIXED) {
    		throw new IllegalArgumentException("Unknown Library Type for -" + getOpt() + " " + s);
    	}

    	// set chosen library type
    	for (final ConditionParameter conditionParameter : getConditionParameters()) {
    		conditionParameter.setLibraryType(libraryType);
    	}
	}

}