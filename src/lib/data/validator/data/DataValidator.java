package lib.data.validator.data;

public interface DataValidator<T> {

	boolean isValid(T data);
	
}
