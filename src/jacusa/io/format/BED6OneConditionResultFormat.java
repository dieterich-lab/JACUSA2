package jacusa.io.format;

import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;

public class BED6OneConditionResultFormat extends BED6call {

	public BED6OneConditionResultFormat(AbstractParameter<PileupData> parameters) {
		super('B', "Default", parameters);
	}

}
