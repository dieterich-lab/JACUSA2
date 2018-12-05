package jacusa.cli.options.librarytype;

import java.util.ArrayList;

import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.has.LibraryType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Specific command line option to chose library type for two conditions.
 * @author Michael Piechotta
 * @param <T>
 */
public class TwoConditionLibraryTypeOption 
extends OneConditionLibraryTypeOption {

	// separator for library type(s) in command line
	public static final char SEP = ',';
	
	// enforce precisely two conditions
	public TwoConditionLibraryTypeOption(
			final ConditionParameter conditionParameter1, 
			final ConditionParameter conditionParameter2,
			final GeneralParameter generalParameter) {
		super(new ArrayList<ConditionParameter>() {
			private static final long serialVersionUID = 1L;
			{
				add(conditionParameter1);
				add(conditionParameter2);
			}
		}, generalParameter);
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Choose the library types for " +
					"condition1(cond1) and condition2(cond2).\nFormat: cond1,cond2. \n" +
					"Possible values for cond1 and cond2:\n" + getPossibleValues() + "\n" +
					"default: " + LibraryType.UNSTRANDED + SEP + LibraryType.UNSTRANDED)
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
	    	final LibraryType libraryType1 = parse(ss[0]);
	    	final LibraryType libraryType2 = parse(ss[1]);

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
