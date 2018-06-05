package jacusa.cli.options.librarytype;

import java.util.ArrayList;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Specific command line option to chose library type for two conditions.
 * @author Michael Piechotta
 * @param <T>
 */
public class TwoConditionLibraryTypeOption<T extends AbstractData> 
extends OneConditionLibraryTypeOption<T> {

	// separator for library type(s) in command line
	private static final char SEP = ',';
	
	// enforce precisely two conditions
	public TwoConditionLibraryTypeOption(
			final AbstractConditionParameter<T> conditionParameter1, 
			final AbstractConditionParameter<T> conditionParameter2,
			final AbstractParameter<T, ?> generalParameter) {
		super(new ArrayList<AbstractConditionParameter<T>>() {
			private static final long serialVersionUID = 1L;
			{
				add(conditionParameter1);
				add(conditionParameter2);
			}
		}, generalParameter);
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Choose the library types for " +
					"condition1(cond1) and condition2(cond2).\nFormat: cond1,cond2. \n" +
					"Possible values for cond1 and cond2:\n" + getPossibleValues() + "\n" +
					"default: " + LIBRARY_TYPE.UNSTRANDED + SEP + LIBRARY_TYPE.UNSTRANDED)
				.build();
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
			// get option as string
			final String s = line.getOptionValue(getOpt());
	    	// expected format (L1=libraryType1, L2=...): L1,L2 -> ss[0]=L1,ss[1]=L2 
	    	final String[] ss = s.split(Character.toString(SEP));

    		final StringBuilder sb = new StringBuilder();
	    	sb.append("Format: cond1,cond2. \n");
	    	sb.append("Possible values for cond1 and cond2:\n");
	    	sb.append(getPossibleValues());

	    	// check format expected format
	    	if (ss.length != 2) {
	    		throw new IllegalArgumentException(sb.toString());
	    	}

	    	// try to parse strings and get library types
	    	final LIBRARY_TYPE libraryType1 = parse(ss[0]);
	    	final LIBRARY_TYPE libraryType2 = parse(ss[1]);

	    	// check that both library types were set
	    	if (libraryType1 == null || libraryType2 == null) {
	    		throw new IllegalArgumentException(sb.toString());
	    	}

	    	// update library type of condition parameter 
	    	getConditionParameters().get(0).setLibraryType(libraryType1);
	    	getConditionParameters().get(1).setLibraryType(libraryType2);
	    }
	}
	
}
