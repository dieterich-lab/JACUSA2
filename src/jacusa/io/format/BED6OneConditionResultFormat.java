package jacusa.io.format;

import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasPileupCount;

public class BED6OneConditionResultFormat<T extends AbstractData & hasPileupCount> 
extends BED6call<T> {

	public BED6OneConditionResultFormat(AbstractParameter<T> parameters) {
		super('B', "Default", parameters);
	}

}
