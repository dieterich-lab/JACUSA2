package jacusa.cli.options.pileupbuilder;

import java.util.ArrayList;

import jacusa.pileup.builder.AbstractDataBuilderFactory;
import jacusa.pileup.builder.FRPairedEnd1PileupBuilderFactory;
import jacusa.pileup.builder.FRPairedEnd2PileupBuilderFactory;
import jacusa.pileup.builder.UnstrandedPileupBuilderFactory;
import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;
import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.BaseQualData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class TwoConditionBaseQualDataBuilderOption<T extends BaseQualData> 
extends AbstractDataBuilderOption<T> {

	private static final char SEP = ',';
	
	public TwoConditionBaseQualDataBuilderOption(
			final AbstractConditionParameter<T> conditionParameter1, 
			final AbstractConditionParameter<T> conditionParameter2) {
		super(new ArrayList<AbstractConditionParameter<T>>() {
			private static final long serialVersionUID = 1L;
			{
				add(conditionParameter1);
				add(conditionParameter2);
			}
		});
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Choose the library types and how parallel pileups are build for " +
					"condition1(cond1) and condition2(cond2).\nFormat: cond1,cond2. \n" +
					"Possible values for cond1 and cond2:\n" + getPossibleValues() + "\n" +
					"default: " + LIBRARY_TYPE.UNSTRANDED + SEP + LIBRARY_TYPE.UNSTRANDED)
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String s = line.getOptionValue(getOpt());
	    	String[] ss = s.split(Character.toString(SEP));

	    	StringBuilder sb = new StringBuilder();
	    	sb.append("Format: cond1,cond2. \n");
	    	sb.append("Possible values for cond1 and cond2:\n");
	    	sb.append(getPossibleValues());

	    	if (ss.length != 2) {
	    		throw new IllegalArgumentException(sb.toString());
	    	}

	    	LIBRARY_TYPE l1 = parse(ss[0]);
	    	LIBRARY_TYPE l2 = parse(ss[1]);

	    	if (l1 == null || l2 == null) {
	    		throw new IllegalArgumentException(sb.toString());
	    	}

	    	getConditionParameters().get(0).setPileupBuilderFactory(buildPileupBuilderFactory(l1));
	    	getConditionParameters().get(1).setPileupBuilderFactory(buildPileupBuilderFactory(l2));
	    }
	}

	@Override
	protected AbstractDataBuilderFactory<T> buildPileupBuilderFactory(
			final LIBRARY_TYPE libraryType) {
		switch(libraryType) {
		
		case UNSTRANDED:
			return new UnstrandedPileupBuilderFactory<T>();
		
		case FR_FIRSTSTRAND:
			return new FRPairedEnd1PileupBuilderFactory<T>();
		
		case FR_SECONDSTRAND:
			return new FRPairedEnd2PileupBuilderFactory<T>();
		}
		
		return null;
	}
}