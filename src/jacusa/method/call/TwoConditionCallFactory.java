package jacusa.method.call;

import jacusa.cli.options.pileupbuilder.TwoConditionLibraryTypeOption;
import jacusa.cli.parameters.CallParameter;
import lib.cli.options.AbstractACOption;
import lib.data.AbstractData;
import lib.data.generator.DataGenerator;
import lib.data.has.hasPileupCount;

import org.apache.commons.cli.ParseException;

public class TwoConditionCallFactory<T extends AbstractData & hasPileupCount> 
extends CallFactory<T> {

	public TwoConditionCallFactory(final DataGenerator<T> dataGenerator) {
		super(new CallParameter<T>(2), dataGenerator);
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

		addACOption(new TwoConditionLibraryTypeOption<T>(
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
