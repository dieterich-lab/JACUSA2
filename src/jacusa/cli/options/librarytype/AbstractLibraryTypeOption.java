package jacusa.cli.options.librarytype;

import java.util.List;

import lib.cli.options.condition.AbstractConditionACOption;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;

public abstract class AbstractLibraryTypeOption<T extends AbstractData>
extends AbstractConditionACOption<T> {

	private static final String OPT = "P";
	private static final String LONG_OPT = "build-pileup";
	
	public AbstractLibraryTypeOption(final List<AbstractConditionParameter<T>> conditionParameter, final AbstractParameter<T, ?> generalParameter) { 
		super(OPT, LONG_OPT, conditionParameter);
	}

	public AbstractLibraryTypeOption(final int conditionIndex, final AbstractConditionParameter<T> conditionParameters, final AbstractParameter<T, ?> generalParameter) { 
		super(OPT, LONG_OPT, conditionIndex, conditionParameters);
	}

	public LIBRARY_TYPE parse(String s) {
		s = s.toUpperCase();
		s = s.replace("-", "_");
		
		for (LIBRARY_TYPE l : LIBRARY_TYPE.values()) {
			if (l.toString().equals(s)) {
				return l;
			}
		}
		return null;
	}
	
	public String getPossibleValues() {
		final StringBuilder sb = new StringBuilder();
		
		for (final LIBRARY_TYPE l : LIBRARY_TYPE.values()) {
			String option = l.toString();
			option = option.replace("_", "-");
			String desc = "";

			switch (l) {
			case FR_FIRSTSTRAND:
				desc = "STRANDED library - first strand sequenced";
				break;
				
			case FR_SECONDSTRAND:
				desc = "STRANDED library - second strand sequenced";
				break;

			case UNSTRANDED:
				desc = "UNSTRANDED library";
				break;

			case MIXED:
				continue;
				
			}

			sb.append(option);
			sb.append("\t\t");
			sb.append(desc);
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
}