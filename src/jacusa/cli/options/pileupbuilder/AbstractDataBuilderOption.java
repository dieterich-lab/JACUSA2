package jacusa.cli.options.pileupbuilder;

import java.util.List;

import jacusa.cli.options.condition.AbstractConditionACOption;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;
import jacusa.pileup.builder.AbstractDataBuilderFactory;
import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;

public abstract class AbstractDataBuilderOption<T extends AbstractData>
extends AbstractConditionACOption<T> {

	private static final String OPT = "P";
	private static final String LONG_OPT = "build-pileup";

	public AbstractDataBuilderOption(final List<ConditionParameters<T>> conditions) { 
		super(OPT, LONG_OPT, conditions);
	}

	public AbstractDataBuilderOption(final int conditionIndex, final ConditionParameters<T> condition) { 
		super(OPT, LONG_OPT, conditionIndex, condition);
	}
	
	protected abstract AbstractDataBuilderFactory<T> buildPileupBuilderFactory(
			final LIBRARY_TYPE libraryType);

	public LIBRARY_TYPE parse(String s) {
		s = s.toUpperCase();
		s = s.replace("-", "_");
		
		switch(LIBRARY_TYPE.valueOf(s)) {

		case UNSTRANDED:
			return LIBRARY_TYPE.UNSTRANDED;
			
		case FR_FIRSTSTRAND:
			return LIBRARY_TYPE.FR_FIRSTSTRAND;
		
		case FR_SECONDSTRAND:
			return LIBRARY_TYPE.FR_SECONDSTRAND;
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

			}

			sb.append(option);
			sb.append("\t\t");
			sb.append(desc);
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
}