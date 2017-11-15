package lib.data.validator;

import lib.data.AbstractData;

public interface DataValidator<T extends AbstractData> {

	boolean isValid(T data);
	
}
