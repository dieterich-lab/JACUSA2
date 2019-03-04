package jacusa.cli.options.librarytype;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lib.cli.options.condition.AbstractConditionACOption;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.util.LibraryType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Specific command line option to chose library type for one condition.
 * Currently, there are the following Library Type that are supported for SE and PE:
 * UNSTRANDED, RF_FIRSTSTRAND, and FR_SECONDSTRAND
 */
public class nConditionLibraryTypeOption extends AbstractConditionACOption {

	public static final String OPT 		= "P";
	public static final String LONG_OPT = "library-type";

	private Set<LibraryType> availableLibTypes;
	private final GeneralParameter generalParameter;
	
	public nConditionLibraryTypeOption(
			final Set<LibraryType> availableLibTypes,
			final ConditionParameter conditionParameter, 
			final GeneralParameter generalParameter) {

		super(OPT, LONG_OPT, conditionParameter);
		this.availableLibTypes 	= availableLibTypes;
		this.generalParameter 	= generalParameter;
	}

	public nConditionLibraryTypeOption(
			final Set<LibraryType> availableLibTypes,
			final List<ConditionParameter> conditionParameters, 
			final GeneralParameter generalParameter) {

		super(OPT, LONG_OPT, conditionParameters);
		this.availableLibTypes 	= availableLibTypes;
		this.generalParameter 	= generalParameter;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		String desc = "Choose the library type";
		if (generalParameter.getConditionsSize() >= 1 && getConditionIndex() == -1) {
			desc += " for all conditions";
		} else {
			desc += " for condition " + getConditionIndex();
		}
		desc += ":\n" + getAvailableValues(availableLibTypes) + 
        		"\n default: " + LibraryType.UNSTRANDED;
		return Option.builder(getOpt())
				.argName(LONG_OPT.toUpperCase())
				.hasArg(true)
				.desc(desc) 
	        	.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		// get option as string
    	final String optionValue = line.getOptionValue(getOpt());
    	// try to get library for
    	final LibraryType libraryType = parse(optionValue);
    	// error if no library type
    	if (libraryType == null || libraryType == LibraryType.MIXED) {
    		throw new IllegalArgumentException("Unknown Library Type for -" + getOpt() + " " + optionValue);
    	}

    	// set chosen library type
    	for (final ConditionParameter conditionParameter : getConditionParameters()) {
    		conditionParameter.setLibraryType(libraryType);
    	}
	}
	
	/**
	 * Parse a String and return the corresponding library type. 
	 * @param s String to be parsed
	 * @return the library type that corresponds to String s or null 
	 */
	public LibraryType parse(String s) {
		if (s == null) {
			return null;
		}
		// auto upper case
		s = s.toUpperCase();
		// be kind to typos 
		s = s.replace("-", "_");
		
		return LibraryType.valueOf(s);
	}

	/**
	 * Nicely formatted String of available library types for command line help.
	 * @return nicely formatted String 
	 */
	public String getAvailableValues(final Set<LibraryType> available) {
		final StringBuilder sb = new StringBuilder();

		// each line consists of: option\t\tdesc 
		for (final LibraryType l : new TreeSet<>(available)) {
			String option = l.toString();
			option = option.replace("_", "-");
			String desc = "";

			
			switch (l) {
			case RF_FIRSTSTRAND:
				desc = "\tSTRANDED library - first strand sequenced";
				break;
				
			case FR_SECONDSTRAND:
				desc = "\tSTRANDED library - second strand sequenced";
				break;

			case UNSTRANDED:
				desc = "\t\tUNSTRANDED library";
				break;

			case MIXED:
				continue;
			}

			sb.append(option);
			sb.append(desc);
			sb.append('\n');
		}
		
		return sb.toString();
	}

}