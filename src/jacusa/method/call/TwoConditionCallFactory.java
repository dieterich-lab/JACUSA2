package jacusa.method.call;

import jacusa.cli.options.pileupbuilder.TwoConditionBaseQualDataBuilderOption;
import lib.cli.options.AbstractACOption;
import lib.data.basecall.PileupData;

import org.apache.commons.cli.ParseException;

public class TwoConditionCallFactory extends CallFactory {

	public TwoConditionCallFactory() {
		super(2);
	}

	@Override
	public void initACOptions() {
		super.initACOptions();

		// workaround for old pileup builder 
		// and new new data builder
		AbstractACOption removeACOption = null;
		for (final AbstractACOption ACOption : getACOptions()) {
			if (ACOption.getOpt().equals("P")) {
				removeACOption = ACOption;
			}
		}
		if (removeACOption != null) {
			getACOptions().remove(removeACOption);
		}

		addACOption(new TwoConditionBaseQualDataBuilderOption<PileupData>(
				getParameter().getConditionParameters().get(0),
				getParameter().getConditionParameters().get(1),
				getParameter()));
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

}
