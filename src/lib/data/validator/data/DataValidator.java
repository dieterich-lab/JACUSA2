package lib.data.validator.data;

import lib.data.AbstractData;

public interface DataValidator<T extends AbstractData> {

	boolean isValid(T data);
	
}
