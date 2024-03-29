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
 * Command line option to choose library type for one condition.
 * Currently, there are the following Library Type that are supported for 
 * SE and PE:
 * UNSTRANDED, RF_FIRSTSTRAND, and FR_SECONDSTRAND - 
 */
public class nConditionLibraryTypeOption extends AbstractConditionACOption {

	public static final String OPT 		= "P";
	public static final String LONG_OPT = "lib-type";

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
		if (generalParameter.getConditionsSize() >= 1 && getcondI() == -1) {
			desc += " for all conditions";
		} else {
			desc += " for condition " + getcondI();
		}
		desc += ":\n" + getAvailableValues(availableLibTypes) + 
				"default: " + LibraryType.UNSTRANDED;
		return Option.builder(getOpt())
				.argName(LONG_OPT.toUpperCase())
				.hasArg(true)
				.desc(desc) 
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		// get option as string
		final String libraryTypeStr = line.getOptionValue(getOpt());
		// try parse libraryTypeStr
		final LibraryType libraryType = parse(libraryTypeStr);
		// error if library type null
		if (libraryType == null || libraryType == LibraryType.MIXED) {
			throw new IllegalArgumentException("Unknown Library Type for -" + 
					getOpt() + " " + libraryTypeStr);
		}
		
		// update library type
		for (final ConditionParameter cp : getConditionParameters()) {
			cp.setLibraryType(libraryType);
		}
	}
	
	/**
	 * Parse a String and return the corresponding library type. 
	 * @param s String to be parsed
	 * @return the library type that corresponds to String s or null if unknown
	 */
	public LibraryType parse(String s) {
		if (s == null) {
			return null;
		}
		// auto upper case
		s = s.toUpperCase();
		// be kind to typos
		// RF_FIRSTSTRAND and RF-FIRSTSTRAND are accepted 
		s = s.replace("-", "_");
		
		return LibraryType.valueOf(s);
	}
	
	/**
	 * Nicely formatted String of available library types for command line help.
	 * @return nicely formatted String 
	 */
	public String getAvailableValues(final Set<LibraryType> available) {
		final StringBuilder sb = new StringBuilder();

		// each line consists of: option[tabs]desc 
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
