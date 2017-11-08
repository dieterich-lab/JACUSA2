package jacusa.io.format;

import lib.cli.parameters.AbstractParameters;
import lib.data.BaseQualData;

public class BED6OneConditionResultFormat extends BED6call {

	public BED6OneConditionResultFormat(AbstractParameters<BaseQualData> parameters) {
		super('B', "Default", parameters);
	}

}
