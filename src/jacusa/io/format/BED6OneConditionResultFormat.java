package jacusa.io.format;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;

public class BED6OneConditionResultFormat extends BED6call {

	public BED6OneConditionResultFormat(AbstractParameters<BaseQualData> parameters) {
		super('B', "Default", parameters);
	}

}
