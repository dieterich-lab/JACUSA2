package lib.data.validator;

import lib.data.AbstractData;

public interface ReplicateDataValidator<T extends AbstractData> {

	boolean isValid(T[] replicateContainer);
	
}
