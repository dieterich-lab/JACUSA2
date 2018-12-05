package jacusa.cli.options.librarytype;

import java.util.List;

import lib.cli.options.condition.AbstractConditionACOption;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.has.LibraryType;

/**
 * General command line option to chose library type for one or all conditions.
 * @author Michael Piechotta
 * @param <T>
 */
public abstract class AbstractLibraryTypeOption
extends AbstractConditionACOption {

	// short opt
	private static final String OPT = "P";
	// long opt
	private static final String LONG_OPT = "library-type";

	private final GeneralParameter generalParameter;
	
	public AbstractLibraryTypeOption(final List<ConditionParameter> conditionParameter, 
			final GeneralParameter generalParameter) {
		
		super(OPT, LONG_OPT, conditionParameter);
		this.generalParameter = generalParameter;
	}

	public AbstractLibraryTypeOption(final int conditionIndex, final ConditionParameter conditionParameter, 
			final GeneralParameter generalParameter) {

		super(OPT, LONG_OPT, conditionIndex, conditionParameter);
		this.generalParameter = generalParameter;
	}

	/**
	 * Parse a String and return the corresponding library type. 
	 * @param s String to be parsed
	 * @return the library type that corresponds to String s or null 
	 */
	public LibraryType parse(String s) {
		// auto upper case
		s = s.toUpperCase();
		// be kind to typos 
		s = s.replace("-", "_");
		
		// find corresponding library type
		for (final LibraryType l : LibraryType.values()) {
			if (l.toString().equals(s)) {
				return l;
			}
		}

		return null;
	}

	/**
	 * Nicely formatted String of available library types for command line help.
	 * @return nicely formatted String 
	 */
	public String getPossibleValues() {
		final StringBuilder sb = new StringBuilder();

		// each line consists of: option\t\tdesc 
		for (final LibraryType l : LibraryType.values()) {
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

	protected GeneralParameter getGeneralParameter() {
		return generalParameter;
	}

}
